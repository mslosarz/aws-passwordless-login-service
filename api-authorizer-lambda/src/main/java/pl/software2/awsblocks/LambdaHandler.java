package pl.software2.awsblocks;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2CustomAuthorizerEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import pl.software2.awsblocks.model.ApiGwAuthorizerBasicResponse;
import pl.software2.awsblocks.service.LoadJWTTokenSecret;
import pl.software2.awsblocks.service.ValidateJWTToken;

import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@Slf4j
public class LambdaHandler implements RequestStreamHandler {
    private static final AppComponent component = DaggerAppComponent.create();

    static {
        java.security.Security.setProperty("networkaddress.cache.ttl", "0");
    }

    @Inject
    ObjectMapper objectMapper;
    @Inject
    LoadJWTTokenSecret loadJWTTokenSecret;
    @Inject
    ValidateJWTToken validateJWTToken;

    public LambdaHandler() {
        component.inject(this);
    }

    LambdaHandler(AppComponent component) {
        component.inject(this);
    }

    @Override
    public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {
        loadJWTTokenSecret.loadSecret();
        var event = objectMapper.readValue(inputStream, APIGatewayV2CustomAuthorizerEvent.class);
        try {
            objectMapper.writeValue(outputStream, validateJWTToken.validateAuthRequest(event));
        } catch (Exception e) {
            objectMapper.writeValue(outputStream, unauthorizedResponse());
        }
    }

    private static ApiGwAuthorizerBasicResponse unauthorizedResponse() {
        return ApiGwAuthorizerBasicResponse.builder()
                .authorized(false)
                .build();
    }
}