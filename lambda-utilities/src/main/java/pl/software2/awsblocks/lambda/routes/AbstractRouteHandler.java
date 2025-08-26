package pl.software2.awsblocks.lambda.routes;

import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import lombok.extern.slf4j.Slf4j;
import pl.software2.awsblocks.lambda.exceptions.BadRequestException;
import pl.software2.awsblocks.lambda.exceptions.NotFoundException;
import pl.software2.awsblocks.lambda.routes.content.ApiGatewayResponseProducer;

@Slf4j
public abstract class AbstractRouteHandler<T> implements RouteHandler {
    protected final ApiGatewayResponseProducer producer;

    public AbstractRouteHandler(ApiGatewayResponseProducer producer) {
        this.producer = producer;
    }

    @Override
    public abstract boolean supports(APIGatewayV2HTTPEvent request);

    protected abstract T handleRequest(APIGatewayV2HTTPEvent request);

    @Override
    public APIGatewayV2HTTPResponse handle(APIGatewayV2HTTPEvent request) {
        try {
            return producer.ok(handleRequest(request));
        } catch (BadRequestException e) {
            log.error("Bad request", e);
            return producer.badRequest(e.getMessage());
        } catch (NotFoundException e) {
            log.error("Not found", e);
            return producer.notFound(request.getRawPath(), e.getMessage());
        } catch (Exception e) {
            log.error("Internal server error", e);
            return producer.internalServerError(e.getMessage());
        }
    }
}
