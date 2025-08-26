package pl.software2.awsblocks;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2CustomAuthorizerEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import pl.software2.awsblocks.model.ApiGwAuthorizerBasicResponse;
import pl.software2.awsblocks.routes.ApiGatewayResponseProducer;
import pl.software2.awsblocks.routes.RouteHandler;

import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.Set;

public class AuthHandler implements RequestStreamHandler {
    static {
        java.security.Security.setProperty("networkaddress.cache.ttl", "0");
    }

    private static final AppComponent component = DaggerAppComponent.create();

    @Inject
    ObjectMapper objectMapper;
    @Inject
    ApiGatewayResponseProducer responseProducer;
    @Inject
    Set<RouteHandler> routes;

    public AuthHandler() {
        component.inject(this);
    }

    @Override
    public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {
        var event = objectMapper.readValue(inputStream, APIGatewayV2CustomAuthorizerEvent.class);
        context.getLogger().log(event.toString());
        objectMapper.writeValue(outputStream, ApiGwAuthorizerBasicResponse.builder().authorized(true).context(Map.of("test", "test")).build());
    }
}