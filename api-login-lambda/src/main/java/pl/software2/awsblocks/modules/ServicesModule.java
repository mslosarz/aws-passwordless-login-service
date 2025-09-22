package pl.software2.awsblocks.modules;

import com.fasterxml.jackson.databind.ObjectMapper;
import dagger.Module;
import dagger.Provides;
import pl.software2.awsblocks.dataaccess.LoginRequests;
import pl.software2.awsblocks.dataaccess.Users;
import pl.software2.awsblocks.lambda.config.LambdaConfig;
import pl.software2.awsblocks.lambda.routes.content.ApiGatewayResponseProducer;
import pl.software2.awsblocks.lambda.model.jwt.JWTTokenSecret;
import pl.software2.awsblocks.service.ProcessGenerateLoginRequest;
import pl.software2.awsblocks.service.ProcessLoginRequest;
import pl.software2.awsblocks.service.SendTokenViaEmail;
import pl.software2.awsblocks.service.jwt.GenerateJWTToken;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.services.sesv2.SesV2Client;

import javax.inject.Singleton;
import java.time.Clock;
import java.util.random.RandomGenerator;

@Module
public class ServicesModule {

    @Provides
    @Singleton
    static JWTTokenSecret provideJWTTokenSecret() {
        return new JWTTokenSecret(new byte[0]);
    }

    @Provides
    static ProcessGenerateLoginRequest processGenerateLoginRequestService(
            RandomGenerator randomGenerator,
            SendTokenViaEmail sendTokenViaEmail,
            LoginRequests loginRequests
    ) {
        return new ProcessGenerateLoginRequest(randomGenerator, sendTokenViaEmail, loginRequests);
    }

    @Provides
    static ProcessLoginRequest processLoginRequestService(
            LambdaConfig config,
            LoginRequests loginRequests,
            Users users,
            ApiGatewayResponseProducer producer,
            GenerateJWTToken generateJWTToken
    ) {
        return new ProcessLoginRequest(config, loginRequests, users, producer, generateJWTToken);
    }

    @Provides
    static GenerateJWTToken generateJWTTokenService(
            LambdaConfig config,
            JWTTokenSecret secret,
            RandomGenerator randomGenerator,
            Clock clock
    ){
        return new GenerateJWTToken(config, randomGenerator, clock, secret);
    }

    @Provides
    static SendTokenViaEmail sendTokenViaEmailService(
            SesV2Client client,
            LambdaConfig config,
            ObjectMapper objectMapper
    ) {
        return new SendTokenViaEmail(client, config, objectMapper);
    }

    @Provides
    static LoginRequests storeLoginRequestService(
            DynamoDbEnhancedClient client,
            LambdaConfig config,
            Clock clock
    ) {
        return new LoginRequests(client, config, clock);
    }
}
