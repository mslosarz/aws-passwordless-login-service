package pl.software2.awsblocks.lambda.routes;

import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;

public interface RouteHandler {
    default int priority() {
        return 0;
    }
    boolean supports(APIGatewayV2HTTPEvent request);

    APIGatewayV2HTTPResponse handle(APIGatewayV2HTTPEvent request);
}
