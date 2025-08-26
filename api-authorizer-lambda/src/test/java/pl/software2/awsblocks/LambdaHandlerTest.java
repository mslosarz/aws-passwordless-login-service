package pl.software2.awsblocks;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2CustomAuthorizerEvent;
import org.junit.jupiter.api.Test;
import pl.software2.awsblocks.model.ApiGwAuthorizerBasicResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static pl.software2.awsblocks.lambda.test.ContentUtils.loadJsonResource;
import static pl.software2.awsblocks.lambda.test.LambdaUtils.callLambda;


class LambdaHandlerTest {
    private final LambdaHandler appHandler = new LambdaHandler();
    private final Context context = mock(Context.class);

    @Test
    public void givenAuthRequest_whenCallingLambda_thenReturnApiGwAuthorizerBasicResponse() {
        // given
        var rq = loadJsonResource(this,"auth_rq.json", APIGatewayV2CustomAuthorizerEvent.class);

        // when
        var response = callLambda(appHandler, rq, context, ApiGwAuthorizerBasicResponse.class);

        // then
        assertThat(response).isNotNull();
    }
}