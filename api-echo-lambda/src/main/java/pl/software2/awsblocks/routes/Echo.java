package pl.software2.awsblocks.routes;


import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import pl.software2.awsblocks.lambda.model.Route;
import pl.software2.awsblocks.lambda.routes.AbstractRouteHandler;
import pl.software2.awsblocks.lambda.routes.content.ApiGatewayResponseProducer;

import javax.inject.Inject;

public class Echo extends AbstractRouteHandler<APIGatewayV2HTTPEvent> {

    @Inject
    public Echo(ApiGatewayResponseProducer producer) {
        super(producer);
    }

    @Override
    public boolean supports(Route request) {
        return true;
    }

    @Override
    protected APIGatewayV2HTTPEvent handleRequest(APIGatewayV2HTTPEvent request) {
        return request;
    }
}
