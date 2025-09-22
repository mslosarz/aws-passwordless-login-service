package pl.software2.awsblocks.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import pl.software2.awsblocks.dataaccess.LoginRequests;
import pl.software2.awsblocks.dataaccess.Users;
import pl.software2.awsblocks.lambda.config.LambdaConfig;
import pl.software2.awsblocks.lambda.exceptions.UnauthorizedException;
import pl.software2.awsblocks.lambda.routes.content.ApiGatewayResponseProducer;
import pl.software2.awsblocks.model.LoginRequest;
import pl.software2.awsblocks.persistence.model.loginrequests.LoginRequestsTable;
import pl.software2.awsblocks.service.jwt.GenerateJWTToken;
import pl.software2.awsblocks.service.jwt.TokenWithSessionId;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static pl.software2.awsblocks.config.EnvironmentVariables.ACCESS_TOKEN_TTL_IN_MINUTES;
import static pl.software2.awsblocks.lambda.test.ContentUtils.decode;
import static pl.software2.awsblocks.lambda.test.ContentUtils.unzip;

class ProcessLoginRequestTest {
    private static final LoginRequest LOGIN_REQUEST = new LoginRequest("test@email.com", "123456");
    private final LoginRequests loginRequests = mock(LoginRequests.class);
    private final Users users = mock(Users.class);
    private final LambdaConfig config = mock(LambdaConfig.class);
    private final ApiGatewayResponseProducer producer = new ApiGatewayResponseProducer(new ObjectMapper());
    private final GenerateJWTToken generateJWTToken = mock(GenerateJWTToken.class);
    private final ProcessLoginRequest service = new ProcessLoginRequest(
            config,
            loginRequests,
            users,
            producer,
            generateJWTToken
    );


    @Test
    void givenLoginRequestForNotExistingEmail_whenCalled_thenThrowUnauthorizedException() {
        // given
        given(loginRequests.getLoginRequest(any())).willReturn(null);

        // when
        var result = catchThrowableOfType(UnauthorizedException.class, () -> service.handle(LOGIN_REQUEST));

        // then
        assertThat(result).isNotNull();
        verify(loginRequests).getLoginRequest("test@email.com");
        verifyNoInteractions(config, users, generateJWTToken);
    }

    @Test
    void givenLoginRequestForExistingEmailWithWrongToken_whenCalled_thenThrowUnauthorizedException2() {
        // given
       given(loginRequests.getLoginRequest(any())).willReturn(new LoginRequestsTable("test@email.com", "654321", 1L));

        // when
        var result = catchThrowableOfType(UnauthorizedException.class, () -> service.handle(LOGIN_REQUEST));

        // then
        assertThat(result).isNotNull();
        verify(loginRequests).getLoginRequest("test@email.com");
        verifyNoInteractions(config, users, generateJWTToken);
    }

    @Test
    void givenValidLoginRequest_whenHandleCalled_thenDatabaseUpdateAndJWTGenerationAreInvoked() {
        // given
        given(loginRequests.getLoginRequest(any())).willReturn(new LoginRequestsTable("test@email.com", "123456", 1L));
        given(config.getValue(ACCESS_TOKEN_TTL_IN_MINUTES.name())).willReturn("60");
        var mockTokenWithSessionId = new TokenWithSessionId("mockToken", "mockSessionId");
        given(generateJWTToken.generateJwtToken(LOGIN_REQUEST)).willReturn(mockTokenWithSessionId);

        // when
        var response = service.handle(LOGIN_REQUEST);

        // then
        verify(users).createOrUpdate(LOGIN_REQUEST.email());
        verify(generateJWTToken).generateJwtToken(LOGIN_REQUEST);
        verify(config).getValue(ACCESS_TOKEN_TTL_IN_MINUTES.name());
        assertThat(response).isNotNull();
    }

    @Test
    void givenValidLoginRequest_whenHandleCalled_thenResponseContainsJWTAndSetCookieHeader() {
        // given
        given(loginRequests.getLoginRequest(any())).willReturn(new LoginRequestsTable("test@email.com", "123456", 1L));
        given(config.getValue(ACCESS_TOKEN_TTL_IN_MINUTES.name())).willReturn("60");
        var mockTokenWithSessionId = new TokenWithSessionId("mockToken", "mockSessionId");
        given(generateJWTToken.generateJwtToken(LOGIN_REQUEST)).willReturn(mockTokenWithSessionId);

        // when
        var response = service.handle(LOGIN_REQUEST);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getHeaders()).containsKey("Set-Cookie");
        assertThat(response.getHeaders().get("Set-Cookie")).contains("sessionId=mockSessionId");
        assertThat(unzip(decode(response.getBody()))).contains("mockToken");
    }
}