package pl.software2.awsblocks.modules;

import dagger.Module;
import dagger.Provides;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.sesv2.SesV2Client;

import javax.inject.Singleton;

@Module
public class AwsModule {
    @Provides
    @Singleton
    static SesV2Client sesV2Client() {
        return SesV2Client.builder().build();
    }

    @Provides
    @Singleton
    static SecretsManagerClient secretsManager() {
        return SecretsManagerClient.builder().build();
    }
}
