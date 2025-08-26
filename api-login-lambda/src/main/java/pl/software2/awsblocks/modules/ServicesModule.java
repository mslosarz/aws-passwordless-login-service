package pl.software2.awsblocks.modules;

import com.fasterxml.jackson.databind.ObjectMapper;
import dagger.Module;
import dagger.Provides;
import pl.software2.awsblocks.lambda.config.LambdaConfig;
import pl.software2.awsblocks.service.auth.StoreLoginRequestAndSendToken;
import software.amazon.awssdk.services.sesv2.SesV2Client;

@Module
public class ServicesModule {
    @Provides
    static StoreLoginRequestAndSendToken provideAuthGenerateService(SesV2Client client, LambdaConfig config, ObjectMapper objectMapper) {
        return new StoreLoginRequestAndSendToken(client, config, objectMapper);
    }
}
