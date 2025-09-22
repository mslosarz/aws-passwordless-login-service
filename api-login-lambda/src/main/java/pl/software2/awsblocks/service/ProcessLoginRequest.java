package pl.software2.awsblocks.service;

import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.software2.awsblocks.dataaccess.LoginRequests;
import pl.software2.awsblocks.dataaccess.Users;
import pl.software2.awsblocks.lambda.config.LambdaConfig;
import pl.software2.awsblocks.lambda.exceptions.UnauthorizedException;
import pl.software2.awsblocks.lambda.routes.content.ApiGatewayResponseProducer;
import pl.software2.awsblocks.model.LoginRequest;
import pl.software2.awsblocks.model.LoginResponse;
import pl.software2.awsblocks.persistence.model.loginrequests.LoginRequestsTable;
import pl.software2.awsblocks.service.jwt.GenerateJWTToken;

import javax.inject.Inject;
import java.time.Duration;
import java.util.Map;

import static java.lang.Integer.parseInt;
import static java.lang.String.format;
import static pl.software2.awsblocks.config.EnvironmentVariables.ACCESS_TOKEN_TTL_IN_MINUTES;

@Slf4j
@AllArgsConstructor(onConstructor = @__(@Inject))
public class ProcessLoginRequest {
    private final LambdaConfig config;
    private final LoginRequests loginRequests;
    private final Users users;
    private final ApiGatewayResponseProducer producer;
    private final GenerateJWTToken generateJWTToken;

    public APIGatewayV2HTTPResponse handle(LoginRequest loginRequest) {
        verifyLoginRequest(loginRequest);
        updateUserInDatabase(loginRequest.email());
        return generateApiResponseWithCookieAndJWT(loginRequest);
    }

    private void verifyLoginRequest(LoginRequest loginRequest) {
        LoginRequestsTable currentRequest = loginRequests.getLoginRequest(loginRequest.email());
        if (currentRequest == null || !currentRequest.getToken().equalsIgnoreCase(loginRequest.token())) {
            throw new UnauthorizedException();
        }
    }

    private void updateUserInDatabase(String email) {
        users.createOrUpdate(email);
    }
    private APIGatewayV2HTTPResponse generateApiResponseWithCookieAndJWT(LoginRequest loginRequest) {
        var accessTokenTTL = Duration.ofMinutes(parseInt(config.getValue(ACCESS_TOKEN_TTL_IN_MINUTES.name())));
        var tokenWithSessionId = generateJWTToken.generateJwtToken(loginRequest);
        var cookieSession = format("sessionId=%s; Secure; Path=/; HttpOnly; Max-Age=%d", tokenWithSessionId.sessionId(), accessTokenTTL.toSeconds());
        return producer.ok(
                new LoginResponse(tokenWithSessionId.token()),
                Map.of("Set-Cookie", cookieSession)
        );
    }
}
