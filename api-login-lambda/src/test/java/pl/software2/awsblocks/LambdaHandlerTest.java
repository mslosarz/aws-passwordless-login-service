package pl.software2.awsblocks;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static pl.software2.awsblocks.lambda.test.ContentUtils.loadJsonResource;
import static pl.software2.awsblocks.lambda.test.LambdaUtils.callApiLambda;


class LambdaHandlerTest {
    private final LambdaHandler lambdaHandler = new LambdaHandler();
    private final Context context = mock(Context.class);

    @Test
    public void notFoundTest() {
        // given
        var rq = loadJsonResource(this, "rq.json", APIGatewayV2HTTPEvent.class);

        // when
        var response = callApiLambda(lambdaHandler, rq, context, APIGatewayV2HTTPEvent.class);

        // then
        assertThat(response.error()).isNotNull();
    }
}