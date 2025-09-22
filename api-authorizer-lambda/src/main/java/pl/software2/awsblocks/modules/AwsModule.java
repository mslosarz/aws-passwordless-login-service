package pl.software2.awsblocks.modules;

import dagger.Module;
import dagger.Provides;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;

import javax.inject.Singleton;

@Module
public class AwsModule {
    @Provides
    @Singleton
    static SecretsManagerClient secretsManager() {
        return SecretsManagerClient.builder().build();
    }
}
