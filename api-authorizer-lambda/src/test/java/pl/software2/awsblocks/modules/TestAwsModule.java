package pl.software2.awsblocks.modules;

import dagger.Module;
import dagger.Provides;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;

import javax.inject.Singleton;

import static org.mockito.Mockito.mock;

@Module
public class TestAwsModule {
    @Provides
    @Singleton
    static SecretsManagerClient secretsManager() {
        return mock(SecretsManagerClient.class);
    }

}