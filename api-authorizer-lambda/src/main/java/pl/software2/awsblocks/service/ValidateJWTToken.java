package pl.software2.awsblocks.service;

import com.amazonaws.services.lambda.runtime.events.APIGatewayV2CustomAuthorizerEvent;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import pl.software2.awsblocks.config.EnvironmentVariables;
import pl.software2.awsblocks.lambda.config.LambdaConfig;
import pl.software2.awsblocks.lambda.exceptions.InternalServerErrorException;
import pl.software2.awsblocks.lambda.exceptions.UnauthorizedException;
import pl.software2.awsblocks.lambda.model.jwt.JWTTokenSecret;
import pl.software2.awsblocks.model.ApiGwAuthorizerBasicResponse;

import javax.inject.Inject;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Clock;
import java.util.Map;

import static org.apache.hc.core5.http.HttpHeaders.AUTHORIZATION;

@Slf4j
@AllArgsConstructor(onConstructor_ = @__(@Inject))
public class ValidateJWTToken {
    private final JWTTokenSecret jwtTokenSecret;
    private final LambdaConfig config;
    private final Clock clock;
    public ApiGwAuthorizerBasicResponse validateAuthRequest(APIGatewayV2CustomAuthorizerEvent event) {
        DecodedJWT decodedJWT = decodeToken(event);
        if(!decodedJWT.getClaim("roles").asList(String.class).contains("user")){
            throw new UnauthorizedException("User does not have required role");
        }
        return ApiGwAuthorizerBasicResponse.builder()
                .authorized(true)
                .context(Map.of("user", decodedJWT.getClaim("email").asString()))
                .build();
    }

    private DecodedJWT decodeToken(APIGatewayV2CustomAuthorizerEvent event) {
        String sessionId = event.getCookies().stream()
                .map(cookie -> cookie.replaceFirst("sessionId=", ""))
                .findFirst().orElse("");

        String jwt = event.getHeaders().get(AUTHORIZATION)
                .replaceFirst("Bearer ", "");

        try {
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            var sessionFingerprint = Hex.encodeHexString(sha256.digest(sessionId.getBytes(StandardCharsets.UTF_8)));
            JWTVerifier jwtVerifier = ((JWTVerifier.BaseVerification)JWT.require(Algorithm.HMAC256(jwtTokenSecret.getSecret()))
                    .withIssuer(config.getValue(EnvironmentVariables.DOMAIN_NAME.name()))
                    .withClaim("sessionFingerprint", sessionFingerprint)
                    .withClaimPresence("roles")
                    .withClaimPresence("email"))
                    .build(clock);
            return jwtVerifier.verify(jwt);
        } catch (NoSuchAlgorithmException e) {
            throw new InternalServerErrorException("SHA-256 algorithm not found", e);
        }
    }
}
