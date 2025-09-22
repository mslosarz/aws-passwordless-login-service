package pl.software2.awsblocks.service.jwt;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.software2.awsblocks.lambda.config.LambdaConfig;
import pl.software2.awsblocks.lambda.model.jwt.JWTTokenSecret;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;

import javax.inject.Inject;

import static pl.software2.awsblocks.config.EnvironmentVariables.JWT_TOKEN_SECRET_NAME;

@Slf4j
@AllArgsConstructor(onConstructor = @__(@Inject))
public class LoadJWTTokenSecret {
    private final LambdaConfig config;
    private final SecretsManagerClient secretsManagerClient;
    private final JWTTokenSecret jwtTokenSecret;

    public void loadSecret() {
        if (jwtTokenSecret.isEmpty()) {
            log.info("Loading JWT token secret");
            GetSecretValueResponse secretValue = secretsManagerClient.getSecretValue(GetSecretValueRequest.builder()
                    .secretId(config.getValue(JWT_TOKEN_SECRET_NAME.name()))
                    .build());
            jwtTokenSecret.setSecret(secretValue.secretString().getBytes());
        }
    }
}
