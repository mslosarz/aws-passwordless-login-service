package pl.software2.awsblocks.routes;

import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import lombok.extern.slf4j.Slf4j;
import pl.software2.awsblocks.lambda.routes.AbstractRouteHandler;
import pl.software2.awsblocks.lambda.routes.content.ApiGatewayResponseProducer;
import pl.software2.awsblocks.lambda.routes.content.PayloadDecoder;
import pl.software2.awsblocks.model.GenerateTokenRequest;
import pl.software2.awsblocks.model.GenerateTokenResponse;
import pl.software2.awsblocks.service.ProcessGenerateLoginRequestService;

import javax.inject.Inject;


@Slf4j
public class PostLoginGenerateHandler extends AbstractRouteHandler<GenerateTokenResponse> {
    private final ProcessGenerateLoginRequestService service;
    private final PayloadDecoder payloadDecoder;

    @Inject
    public PostLoginGenerateHandler(
            ApiGatewayResponseProducer producer,
            ProcessGenerateLoginRequestService service,
            PayloadDecoder payloadDecoder) {
        super(producer);
        this.service = service;
        this.payloadDecoder = payloadDecoder;
    }

    @Override
    public boolean supports(APIGatewayV2HTTPEvent request) {
        var http = request.getRequestContext().getHttp();
        return http.getMethod().equalsIgnoreCase("POST") &&
                http.getPath().toLowerCase().endsWith("/login/generate");
    }

    @Override
    protected GenerateTokenResponse handleRequest(APIGatewayV2HTTPEvent request) {
        return service.handle(payloadDecoder.extractBody(request, GenerateTokenRequest.class));
    }
}
