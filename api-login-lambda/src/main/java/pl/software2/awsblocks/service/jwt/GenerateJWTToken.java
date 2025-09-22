package pl.software2.awsblocks.service.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import pl.software2.awsblocks.lambda.config.LambdaConfig;
import pl.software2.awsblocks.lambda.exceptions.InternalServerErrorException;
import pl.software2.awsblocks.lambda.model.jwt.JWTTokenSecret;
import pl.software2.awsblocks.model.LoginRequest;

import javax.inject.Inject;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.random.RandomGenerator;

import static java.lang.Integer.parseInt;
import static pl.software2.awsblocks.config.EnvironmentVariables.ACCESS_TOKEN_TTL_IN_MINUTES;
import static pl.software2.awsblocks.config.EnvironmentVariables.DOMAIN_NAME;

@Slf4j
@AllArgsConstructor(onConstructor = @__(@Inject))
public class GenerateJWTToken {
    private final LambdaConfig config;
    private final RandomGenerator randomGenerator;
    private final Clock clock;
    private final JWTTokenSecret jwtTokenSecret;

    public TokenWithSessionId generateJwtToken(LoginRequest loginRequest) {
        byte[] randomToken = new byte[42];
        randomGenerator.nextBytes(randomToken);
        var sessionId = Hex.encodeHexString(randomToken);
        var accessTokenTTL = Duration.ofMinutes(parseInt(config.getValue(ACCESS_TOKEN_TTL_IN_MINUTES.name())));
        try {
            var sha256 = MessageDigest.getInstance("SHA-256");
            var sessionFingerprint = Hex.encodeHexString(sha256.digest(sessionId.getBytes(StandardCharsets.UTF_8)));
            Instant now = clock.instant();
            return new TokenWithSessionId(JWT.create().withSubject(loginRequest.email())
                    .withExpiresAt(now.plus(accessTokenTTL))
                    .withIssuer(config.getValue(DOMAIN_NAME.name()))
                    .withIssuedAt(now)
                    .withNotBefore(now)
                    .withClaim("sessionFingerprint", sessionFingerprint)
                    .withClaim("roles", List.of("user"))
                    .withClaim("email", loginRequest.email())
                    .withAudience()
                    .sign(Algorithm.HMAC256(jwtTokenSecret.getSecret())), sessionId);
        } catch (NoSuchAlgorithmException e) {
            throw new InternalServerErrorException("Something went wrong", e);
        }
    }
}
