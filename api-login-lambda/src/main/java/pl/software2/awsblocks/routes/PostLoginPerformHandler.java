package pl.software2.awsblocks.routes;

import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.core5.http.Method;
import pl.software2.awsblocks.lambda.model.Route;
import pl.software2.awsblocks.lambda.routes.RouteHandler;
import pl.software2.awsblocks.lambda.routes.content.PayloadDecoder;
import pl.software2.awsblocks.model.LoginRequest;
import pl.software2.awsblocks.service.ProcessLoginRequest;

import javax.inject.Inject;


@Slf4j
@AllArgsConstructor(onConstructor = @__(@Inject))
public class PostLoginPerformHandler implements RouteHandler {
    private final Route route = new Route(Method.POST, "/login/perform");
    private final ProcessLoginRequest service;
    private final PayloadDecoder payloadDecoder;

    @Override
    public boolean supports(Route request) {
        return request.equals(route);
    }

    public APIGatewayV2HTTPResponse handle(APIGatewayV2HTTPEvent request) {
        return service.handle(payloadDecoder.extractBody(request, LoginRequest.class));
    }
}
