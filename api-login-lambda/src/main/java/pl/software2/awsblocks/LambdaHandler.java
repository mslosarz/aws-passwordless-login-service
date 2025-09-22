package pl.software2.awsblocks;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import pl.software2.awsblocks.lambda.model.Route;
import pl.software2.awsblocks.lambda.routes.RouteHandler;
import pl.software2.awsblocks.lambda.routes.content.ApiGatewayResponseProducer;
import pl.software2.awsblocks.service.jwt.LoadJWTTokenSecret;

import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;

@Slf4j
public class LambdaHandler implements RequestStreamHandler {
    private static final AppComponent component = DaggerAppComponent.create();

    static {
        java.security.Security.setProperty("networkaddress.cache.ttl", "0");
    }

    @Inject
    LoadJWTTokenSecret loadJWTTokenSecret;

    @Inject
    ObjectMapper objectMapper;
    @Inject
    ApiGatewayResponseProducer responseProducer;
    @Inject
    Set<RouteHandler> routes;

    public LambdaHandler() {
        component.inject(this);
    }

    LambdaHandler(AppComponent component) {
        component.inject(this);
    }

    @Override
    public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {
        var event = objectMapper.readValue(inputStream, APIGatewayV2HTTPEvent.class);
        log.info("Received APIGatewayV2HTTPEvent: {}", event);
        loadJWTTokenSecret.loadSecret();
        APIGatewayV2HTTPResponse response = routes.stream()
                .filter(route -> route.supports(Route.fromRequest(event)))
                .findFirst()
                .map(routeHandler -> {
                    log.info("Route handler: {}", routeHandler.getClass().getSimpleName());
                    return routeHandler.handle(event);
                })
                .orElse(responseProducer.notFound(event.getRawPath()));

        objectMapper.writeValue(outputStream, response);
    }
}