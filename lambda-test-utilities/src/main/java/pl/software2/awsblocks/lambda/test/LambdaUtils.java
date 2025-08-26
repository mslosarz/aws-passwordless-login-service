package pl.software2.awsblocks.lambda.test;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import lombok.experimental.UtilityClass;
import org.apache.hc.core5.http.HttpStatus;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.apache.hc.core5.http.HttpHeaders.CONTENT_ENCODING;
import static pl.software2.awsblocks.lambda.test.ContentUtils.*;

@UtilityClass
public class LambdaUtils {

    public static <RQ, RS> Response<RS> callApiLambda(RequestStreamHandler lambda, RQ request, Context context,
                                               Class<RS> responseModel) {
        try (final ByteArrayInputStream inputStream = new ByteArrayInputStream(objectMapper().writeValueAsBytes(request))) {
            final var outputStream = new ByteArrayOutputStream();
            lambda.handleRequest(inputStream, outputStream, context);
            outputStream.close();
            final var apiGwProxyResponse = objectMapper().readValue(outputStream.toString(StandardCharsets.UTF_8), APIGatewayV2HTTPResponse.class);

            if (apiGwProxyResponse.getIsBase64Encoded() && "gzip".equalsIgnoreCase(getEncoding(apiGwProxyResponse.getHeaders()))) {
                return buildResponse(apiGwProxyResponse, unzip(decode(apiGwProxyResponse.getBody())), responseModel);
            } else if(apiGwProxyResponse.getIsBase64Encoded()){
                return buildResponse(apiGwProxyResponse, decodeToString(apiGwProxyResponse.getBody()), responseModel);
            }
            return buildResponse(apiGwProxyResponse, apiGwProxyResponse.getBody(), responseModel);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static <RS> Response<RS> buildResponse(APIGatewayV2HTTPResponse apiGwProxyResponse, String body, Class<RS> responseModel) throws Exception {
        if(apiGwProxyResponse.getStatusCode() == HttpStatus.SC_OK) {
            return new Response<>(apiGwProxyResponse, objectMapper().readValue(body, responseModel), null);
        }
        return new Response<>(apiGwProxyResponse, null, objectMapper().readValue(body, ErrorResponse.class));
    }

    public static <RQ, RS> RS callLambda(RequestStreamHandler lambda, RQ request, Context context,
                                                      Class<RS> response) {
        try (final ByteArrayInputStream inputStream = new ByteArrayInputStream(objectMapper().writeValueAsBytes(request))) {
            final var outputStream = new ByteArrayOutputStream();
            lambda.handleRequest(inputStream, outputStream, context);
            outputStream.close();
            return objectMapper().readValue(outputStream.toString(StandardCharsets.UTF_8), response);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public record Response<RS>(APIGatewayV2HTTPResponse rawResponse, RS model, ErrorResponse error) {
    }

    private static String getEncoding(Map<String, String> headers){
        return headers.entrySet().stream()
                .filter(entry -> entry.getKey().equalsIgnoreCase(CONTENT_ENCODING))
                .map(Map.Entry::getValue)
                .findAny()
                .orElse(null);
    }
}