package pl.software2.awsblocks.lambda.routes.content;

import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import pl.software2.awsblocks.lambda.model.ErrorResponseModel;

import javax.inject.Inject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

public class ApiGatewayResponseProducer {
    private static final Map<String, String> gzipJsonHeader = Map.of(
            "Content-Type", "application/json",
            "Content-Encoding", "gzip"
    );
    private final ObjectMapper objectMapper;

    @Inject
    public ApiGatewayResponseProducer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public <T> APIGatewayV2HTTPResponse ok(T body) {
        return APIGatewayV2HTTPResponse.builder()
                .withStatusCode(200)
                .withHeaders(gzipJsonHeader)
                .withBody(Base64.getEncoder().encodeToString(gzip(body)))
                .withIsBase64Encoded(true)
                .build();
    }

    public APIGatewayV2HTTPResponse notFound(String path, String message) {
        return APIGatewayV2HTTPResponse.builder()
                .withStatusCode(404)
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
                .withStatusCode(400)
                .withHeaders(gzipJsonHeader)
                .withBody(Base64.getEncoder().encodeToString(gzip(ErrorResponseModel.badRequest(message))))
                .withIsBase64Encoded(true)
                .build();
    }

    public APIGatewayV2HTTPResponse internalServerError(String message) {
        return APIGatewayV2HTTPResponse.builder()
                .withStatusCode(500)
                .withHeaders(gzipJsonHeader)
                .withBody(Base64.getEncoder().encodeToString(gzip(ErrorResponseModel.internalServerError(message))))
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
