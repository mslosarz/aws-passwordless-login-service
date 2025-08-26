package pl.software2.awsblocks.modules;

import dagger.Module;
import dagger.Provides;
import software.amazon.awssdk.services.sesv2.SesV2Client;

@Module
public class AwsModule {
    @Provides
    static SesV2Client sesV2Client() {
        return SesV2Client.builder().build();
    }
}
