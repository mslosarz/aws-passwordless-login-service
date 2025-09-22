package pl.software2.awsblocks.lambda.routes;

import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import pl.software2.awsblocks.lambda.model.Route;

public interface RouteHandler {
    boolean supports(Route request);

    APIGatewayV2HTTPResponse handle(APIGatewayV2HTTPEvent request);
}
