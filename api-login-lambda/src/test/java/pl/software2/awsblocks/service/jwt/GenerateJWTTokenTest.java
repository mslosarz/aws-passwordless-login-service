package pl.software2.awsblocks.service.jwt;

import org.junit.jupiter.api.Test;
import pl.software2.awsblocks.lambda.config.LambdaConfig;
import pl.software2.awsblocks.lambda.test.Fixtures;
import pl.software2.awsblocks.lambda.model.jwt.JWTTokenSecret;
import pl.software2.awsblocks.model.LoginRequest;

import java.time.Clock;
import java.util.random.RandomGenerator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static pl.software2.awsblocks.config.EnvironmentVariables.ACCESS_TOKEN_TTL_IN_MINUTES;
import static pl.software2.awsblocks.config.EnvironmentVariables.DOMAIN_NAME;


class GenerateJWTTokenTest {
    private final LambdaConfig config = mock(LambdaConfig.class);
    private final RandomGenerator random = mock(RandomGenerator.class);
    private final Clock clock = Fixtures.fixedClock();
    private final JWTTokenSecret secret = new JWTTokenSecret("test".getBytes());
    private final GenerateJWTToken service = new GenerateJWTToken(config, random, clock, secret);

    @Test
    void givenConfigWithLoginRequest_whenTokenGenerated_thenValidTokenAndSessionIdReturned() {
        // given
        when(config.getValue(any())).thenReturn("60").thenReturn("domain.com");

        // when
        var tokenWithSessionId = service.generateJwtToken(new LoginRequest("emial@test.com", "token"));

        // then
        assertThat(tokenWithSessionId).isNotNull();
        assertThat(tokenWithSessionId.token()).isEqualTo("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJlbWlhbEB0ZXN0LmNvbSIsImV4cCI6MTczNTczNjQwMCwiaXNzIjoiZG9tYWluLmNvbSIsImlhdCI6MTczNTczMjgwMCwibmJmIjoxNzM1NzMyODAwLCJzZXNzaW9uSWQiOiI3M2MxOTBjNzk0YTU2OTVkYmU4ZTA0M2RlNTMwMjNlMjk4MGI4MDJiY2MyZTQ5OTIwYzZkZjhkZjY3MTRlMTVjIn0.jHpxZXBZQ7y3b4gL4OmrL5amclARAlaNcye0KoVfpzM");
        assertThat(tokenWithSessionId.sessionId()).isEqualTo("000000000000000000000000000000000000000000000000000000000000000000000000000000000000");
        verify(random).nextBytes(any(byte[].class));
        verify(config).getValue(ACCESS_TOKEN_TTL_IN_MINUTES.name());
        verify(config).getValue(DOMAIN_NAME.name());
    }
}