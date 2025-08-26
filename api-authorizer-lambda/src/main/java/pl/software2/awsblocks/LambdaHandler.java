package pl.software2.awsblocks;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2CustomAuthorizerEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import pl.software2.awsblocks.model.ApiGwAuthorizerBasicResponse;

import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

@Slf4j
public class LambdaHandler implements RequestStreamHandler {
    private static final AppComponent component = DaggerAppComponent.create();

    static {
        java.security.Security.setProperty("networkaddress.cache.ttl", "0");
    }

    @Inject
    ObjectMapper objectMapper;

    public LambdaHandler() {
        component.inject(this);
    }

    @Override
    public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {
        var event = objectMapper.readValue(inputStream, APIGatewayV2CustomAuthorizerEvent.class);
        log.info("Received APIGatewayV2CustomAuthorizerEvent: {}", event);
        objectMapper.writeValue(outputStream, ApiGwAuthorizerBasicResponse.builder().authorized(true).context(Map.of("test", "test")).build());
    }
}