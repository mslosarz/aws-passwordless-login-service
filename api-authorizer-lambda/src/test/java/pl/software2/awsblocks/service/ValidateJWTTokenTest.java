package pl.software2.awsblocks.service;

import com.amazonaws.services.lambda.runtime.events.APIGatewayV2CustomAuthorizerEvent;
import org.junit.jupiter.api.Test;
import pl.software2.awsblocks.config.EnvironmentVariables;
import pl.software2.awsblocks.lambda.config.LambdaConfig;
import pl.software2.awsblocks.lambda.model.jwt.JWTTokenSecret;
import pl.software2.awsblocks.lambda.test.Fixtures;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import static org.apache.hc.core5.http.HttpHeaders.AUTHORIZATION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.*;
import static pl.software2.awsblocks.lambda.model.jwt.JwtTokenSharedFields.USER_ID;

class ValidateJWTTokenTest {
    private final APIGatewayV2CustomAuthorizerEvent event = mock(APIGatewayV2CustomAuthorizerEvent.class);
    private final LambdaConfig config = mock(LambdaConfig.class);
    private final JWTTokenSecret jwtTokenSecret = new JWTTokenSecret("test".getBytes());
    private final ValidateJWTToken service = new ValidateJWTToken(jwtTokenSecret, config, Fixtures.fixedClock());

    @Test
    void givenValidAuthRequest_whenCallingValidateAuthRequest_thenReturnAuthorizedWithUser() {
        // given
        when(event.getCookies()).thenReturn(List.of("sessionId=000000000000000000000000000000000000000000000000000000000000000000000000000000000000"));
        when(event.getHeaders()).thenReturn(Map.of(AUTHORIZATION, "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJlbWlhbEB0ZXN0LmNvbSIsImV4cCI6MTczNTczNjQwMCwiaXNzIjoiZG9tYWluLmNvbSIsImlhdCI6MTczNTczMjgwMCwibmJmIjoxNzM1NzMyODAwLCJzZXNzaW9uRmluZ2VycHJpbnQiOiI3M2MxOTBjNzk0YTU2OTVkYmU4ZTA0M2RlNTMwMjNlMjk4MGI4MDJiY2MyZTQ5OTIwYzZkZjhkZjY3MTRlMTVjIiwicm9sZXMiOlsidXNlciJdLCJlbWFpbCI6ImVtaWFsQHRlc3QuY29tIn0.XxpHLgUI_8zZpkSvWJsshZLETswI6JdoFzpWak-jgHE"));
        when(config.getValue(anyString())).thenReturn("domain.com");

        // when
        var apiGwAuthorizerBasicResponse = service.validateAuthRequest(event);


        // then
        assertThat(apiGwAuthorizerBasicResponse.isAuthorized()).isTrue();
        assertThat(apiGwAuthorizerBasicResponse.getContext()).isEqualTo(Map.of(USER_ID, "emial@test.com"));
        verify(config).getValue(EnvironmentVariables.DOMAIN_NAME.name());
    }

    @Test
    void givenExpiredAuthRequest_whenCallingValidateAuthRequest_thenReturnUnauthorized() {
        // given
        when(event.getCookies()).thenReturn(List.of("sessionId=000000000000000000000000000000000000000000000000000000000000000000000000000000000000"));
        when(event.getHeaders()).thenReturn(Map.of(AUTHORIZATION, "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJlbWlhbEB0ZXN0LmNvbSIsImV4cCI6MTczNTczNjQwMCwiaXNzIjoiZG9tYWluLmNvbSIsImlhdCI6MTczNTczMjgwMCwibmJmIjoxNzM1NzMyODAwLCJzZXNzaW9uRmluZ2VycHJpbnQiOiI3M2MxOTBjNzk0YTU2OTVkYmU4ZTA0M2RlNTMwMjNlMjk4MGI4MDJiY2MyZTQ5OTIwYzZkZjhkZjY3MTRlMTVjIiwicm9sZXMiOlsidXNlciJdLCJlbWFpbCI6ImVtaWFsQHRlc3QuY29tIn0.XxpHLgUI_8zZpkSvWJsshZLETswI6JdoFzpWak-jgHE"));
        when(config.getValue(anyString())).thenReturn("domain.com");
        var clock = Fixtures.fixedClock(Fixtures.fixedClock().instant().plus(Duration.ofHours(3)).toString());

        // when
        var exception = catchThrowable(() -> new ValidateJWTToken(jwtTokenSecret, config, clock).validateAuthRequest(event));

        // then
        assertThat(exception).isNotNull();
        verify(config).getValue(EnvironmentVariables.DOMAIN_NAME.name());
    }
}