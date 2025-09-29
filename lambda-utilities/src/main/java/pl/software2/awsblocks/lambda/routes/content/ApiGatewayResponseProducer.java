package pl.software2.awsblocks.lambda.routes.content;

import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import pl.software2.awsblocks.lambda.model.ErrorResponseModel;

import javax.inject.Inject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

import static java.util.Collections.emptyMap;
import static org.apache.hc.core5.http.HttpHeaders.CONTENT_ENCODING;
import static org.apache.hc.core5.http.HttpHeaders.CONTENT_TYPE;
import static org.apache.hc.core5.http.HttpStatus.*;

public class ApiGatewayResponseProducer {
    private static final Map<String, String> gzipJsonHeader = Map.of(
            CONTENT_TYPE, "application/json",
            CONTENT_ENCODING, "gzip"
    );
    private final ObjectMapper objectMapper;

    @Inject
    public ApiGatewayResponseProducer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public <T> APIGatewayV2HTTPResponse ok(T body) {
        return ok(body, emptyMap());
    }

    public <T> APIGatewayV2HTTPResponse ok(T body, Map<String, String> headers) {
        return APIGatewayV2HTTPResponse.builder()
                .withStatusCode(SC_OK)
                .withHeaders(mergeHeaders(headers))
                .withBody(Base64.getEncoder().encodeToString(gzip(body)))
                .withIsBase64Encoded(true)
                .build();
    }

    private static Map<String, String> mergeHeaders(Map<String, String> headers) {
        var result = new HashMap<>(gzipJsonHeader);
        result.putAll(headers);
        return result;
    }

    public APIGatewayV2HTTPResponse notFound(String path, String message) {
        return APIGatewayV2HTTPResponse.builder()
                .withStatusCode(SC_NOT_FOUND)
                .withHeaders(gzipJsonHeader)
                .withBody(Base64.getEncoder().encodeToString(gzip(ErrorResponseModel.notFound(path + " " + message))))
                .withIsBase64Encoded(true)
                .build();
    }

    public APIGatewayV2HTTPResponse notFound(String path) {
        return notFound(path, "was not found");
    }

    public APIGatewayV2HTTPResponse badRequest(String message) {
        return APIGatewayV2HTTPResponse.builder()
                .withStatusCode(SC_BAD_REQUEST)
                .withHeaders(gzipJsonHeader)
                .withBody(Base64.getEncoder().encodeToString(gzip(ErrorResponseModel.badRequest(message))))
                .withIsBase64Encoded(true)
                .build();
    }

    public APIGatewayV2HTTPResponse internalServerError(String message) {
        return APIGatewayV2HTTPResponse.builder()
                .withStatusCode(SC_INTERNAL_SERVER_ERROR)
                .withHeaders(gzipJsonHeader)
                .withBody(Base64.getEncoder().encodeToString(gzip(ErrorResponseModel.internalServerError(message))))
                .withIsBase64Encoded(true)
                .build();
    }

    public APIGatewayV2HTTPResponse unauthorized() {
        return APIGatewayV2HTTPResponse.builder()
                .withStatusCode(SC_UNAUTHORIZED)
                .withHeaders(gzipJsonHeader)
                .withBody(Base64.getEncoder().encodeToString(gzip(ErrorResponseModel.unauthorized())))
                .withIsBase64Encoded(true)
                .build();
    }
    private byte[] gzip(Object event) {
        var outputStream = new ByteArrayOutputStream();
        try (GZIPOutputStream gzip = new GZIPOutputStream(outputStream)) {
            gzip.write(objectMapper.writeValueAsBytes(event));
            gzip.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return outputStream.toByteArray();
    }
}
