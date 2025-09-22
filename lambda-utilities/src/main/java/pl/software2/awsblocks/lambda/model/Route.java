package pl.software2.awsblocks.lambda.model;

import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import org.apache.hc.core5.http.Method;

public record Route(Method method, String path) {
    public static Route fromRequest(APIGatewayV2HTTPEvent event) {
        var requestContext = event.getRequestContext();
        var http = requestContext.getHttp();
        return new Route(
                Method.valueOf(http.getMethod()),
                http.getPath().replaceFirst("/" + requestContext.getStage(), "")
        );
    }
}

