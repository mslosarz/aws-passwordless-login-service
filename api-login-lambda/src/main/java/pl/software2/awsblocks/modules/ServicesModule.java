package pl.software2.awsblocks.modules;

import com.fasterxml.jackson.databind.ObjectMapper;
import dagger.Module;
import dagger.Provides;
import pl.software2.awsblocks.lambda.config.LambdaConfig;
import pl.software2.awsblocks.service.ProcessGenerateLoginRequestService;
import pl.software2.awsblocks.service.SendTokenViaEmailService;
import pl.software2.awsblocks.service.StoreLoginRequestService;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.services.sesv2.SesV2Client;

import java.time.Clock;
import java.util.random.RandomGenerator;

@Module
public class ServicesModule {

    @Provides
    static ProcessGenerateLoginRequestService processGenerateLoginRequestService(
            RandomGenerator randomGenerator,
            SendTokenViaEmailService sendTokenViaEmailService,
            StoreLoginRequestService storeLoginRequestService
    ) {
        return new ProcessGenerateLoginRequestService(randomGenerator, sendTokenViaEmailService, storeLoginRequestService);
    }

    @Provides
    static SendTokenViaEmailService sendTokenViaEmailService(
            SesV2Client client,
            LambdaConfig config,
            ObjectMapper objectMapper
    ) {
        return new SendTokenViaEmailService(client, config, objectMapper);
    }

    @Provides
    static StoreLoginRequestService storeLoginRequestService(
            DynamoDbEnhancedClient client,
            LambdaConfig config,
            Clock clock
    ) {
        return new StoreLoginRequestService(client, config, clock);
    }
}
