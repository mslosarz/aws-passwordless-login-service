package pl.software2.awsblocks;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static pl.software2.awsblocks.lambda.test.ContentUtils.loadJsonResource;
import static pl.software2.awsblocks.lambda.test.LambdaUtils.callApiLambda;


class LambdaHandlerTest {
    private final TestAppComponent component = DaggerTestAppComponent.create();
    private final LambdaHandler lambdaHandler = new LambdaHandler(component);
    private final Context context = mock(Context.class);
    private final SecretsManagerClient secretClient = component.secretsManager();

    @Test
    public void notFoundTest() {
        // given
        var rq = loadJsonResource(this, "rq.json", APIGatewayV2HTTPEvent.class);
        when(secretClient.getSecretValue(any(GetSecretValueRequest.class))).thenReturn(GetSecretValueResponse.builder().secretString("test").build());

        // when
        var response = callApiLambda(lambdaHandler, rq, context, APIGatewayV2HTTPEvent.class);

        // then
        assertThat(response.error()).isNotNull();
    }
}