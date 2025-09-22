package pl.software2.awsblocks;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2CustomAuthorizerEvent;
import org.junit.jupiter.api.Test;
import pl.software2.awsblocks.model.ApiGwAuthorizerBasicResponse;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static pl.software2.awsblocks.lambda.test.ContentUtils.loadJsonResource;
import static pl.software2.awsblocks.lambda.test.LambdaUtils.callLambda;


class LambdaHandlerTest {
    private final TestAppComponent component = DaggerTestAppComponent.create();
    private final SecretsManagerClient secretClient = component.secretsManager();
    private final LambdaHandler appHandler = new LambdaHandler(component);
    private final Context context = mock(Context.class);

    @Test
    public void givenAuthRequest_whenCallingLambda_thenReturnApiGwAuthorizerBasicResponse() {
        // given
        var rq = loadJsonResource(this,"auth_rq.json", APIGatewayV2CustomAuthorizerEvent.class);
        when(secretClient.getSecretValue(any(GetSecretValueRequest.class))).thenReturn(GetSecretValueResponse.builder().secretString("test").build());

        // when
        var response = callLambda(appHandler, rq, context, ApiGwAuthorizerBasicResponse.class);

        // then
        assertThat(response).isNotNull();
    }
}