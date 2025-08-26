package pl.software2.awsblocks.lambda.routes.content;

import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import pl.software2.awsblocks.lambda.exceptions.BadRequestException;

import javax.inject.Inject;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import static org.apache.hc.core5.http.HttpHeaders.CONTENT_ENCODING;

@Slf4j
public class PayloadDecoder {
    private final ObjectMapper objectMapper;

    @Inject
    PayloadDecoder(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public <T> T extractBody(APIGatewayV2HTTPEvent request, Class<T> clazz) {
        try {
            return decode(request, clazz);
        } catch (IOException e) {
            throw new BadRequestException(e.getMessage(), e);
        }
    }

    private <T> T decode(APIGatewayV2HTTPEvent request, Class<T> clazz) throws IOException {
        if (request.getIsBase64Encoded() && "gzip".equalsIgnoreCase(getEncodingFrom(request))) {
            var body = new String(unzip(decodeBodyOf(request)));
            log.info("Decoded unzipped request body: {}", body);
            return objectMapper.readValue(body, clazz);
        }
        if (request.getIsBase64Encoded()) {
            var decodedBody = new String(decodeBodyOf(request));
            log.info("Decoded request body: {}", decodedBody);
            return objectMapper.readValue(decodedBody, clazz);
        }
        log.info("Request body: {}", request.getBody());
        return objectMapper.readValue(request.getBody(), clazz);
    }

    private static byte[] unzip(byte[] buf) throws IOException {
        var gis = new GZIPInputStream(new ByteArrayInputStream(buf));
        return gis.readAllBytes();
    }

    private static byte[] decodeBodyOf(APIGatewayV2HTTPEvent request) {
        return Base64.getDecoder().decode(request.getBody());
    }

    private static String getEncodingFrom(APIGatewayV2HTTPEvent request) {
        return request.getHeaders().entrySet().stream()
                .filter(entry -> entry.getKey().equalsIgnoreCase(CONTENT_ENCODING))
                .map(Map.Entry::getValue)
                .findAny()
                .orElse(null);
    }
}
