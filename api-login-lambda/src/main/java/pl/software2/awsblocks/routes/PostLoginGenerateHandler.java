package pl.software2.awsblocks.routes;

import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.core5.http.Method;
import pl.software2.awsblocks.lambda.model.Route;
import pl.software2.awsblocks.lambda.routes.AbstractRouteHandler;
import pl.software2.awsblocks.lambda.routes.content.ApiGatewayResponseProducer;
import pl.software2.awsblocks.lambda.routes.content.PayloadDecoder;
import pl.software2.awsblocks.model.GenerateTokenRequest;
import pl.software2.awsblocks.model.GenerateTokenResponse;
import pl.software2.awsblocks.service.ProcessGenerateLoginRequest;

import javax.inject.Inject;


@Slf4j
public class PostLoginGenerateHandler extends AbstractRouteHandler<GenerateTokenResponse> {
    private final Route route = new Route(Method.POST, "/login/generate");
    private final ProcessGenerateLoginRequest service;
    private final PayloadDecoder payloadDecoder;

    @Inject
    public PostLoginGenerateHandler(
            ApiGatewayResponseProducer producer,
            ProcessGenerateLoginRequest service,
            PayloadDecoder payloadDecoder) {
        super(producer);
        this.service = service;
        this.payloadDecoder = payloadDecoder;
    }

    @Override
    public boolean supports(Route request) {
        return request.equals(route);
    }

    @Override
    protected GenerateTokenResponse handleRequest(APIGatewayV2HTTPEvent request) {
        return service.handle(payloadDecoder.extractBody(request, GenerateTokenRequest.class));
    }
}
