package pl.software2.awsblocks.modules;

import dagger.Module;
import dagger.Provides;
import pl.software2.awsblocks.lambda.config.LambdaConfig;
import pl.software2.awsblocks.lambda.model.jwt.JWTTokenSecret;
import pl.software2.awsblocks.service.LoadJWTTokenSecret;
import pl.software2.awsblocks.service.ValidateJWTToken;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;

import javax.inject.Singleton;
import java.time.Clock;

@Module
public class ServicesModule {

    @Provides
    @Singleton
    static JWTTokenSecret provideJWTTokenSecret() {
        return new JWTTokenSecret(new byte[0]);
    }

    @Provides
    static LoadJWTTokenSecret loadJWTTokenSecret(
            LambdaConfig config,
            SecretsManagerClient secretsManagerClient,
            JWTTokenSecret secret
    ) {
        return new LoadJWTTokenSecret(config, secretsManagerClient, secret);
    }

    @Provides
    static ValidateJWTToken validateJWTToken(
            JWTTokenSecret secret,
            LambdaConfig config,
            Clock clock
    ) {
        return new ValidateJWTToken(secret, config, clock);
    }
}
