package pl.software2.awsblocks.lambda;

import com.fasterxml.jackson.databind.ObjectMapper;
import dagger.Module;
import dagger.Provides;
import pl.software2.awsblocks.lambda.config.LambdaConfig;

import javax.inject.Singleton;
import java.security.SecureRandom;
import java.time.Clock;
import java.util.random.RandomGenerator;

@Module
public class CommonModule {
    @Provides
    @Singleton
    static ObjectMapper provideObjectMapper() {
        return new ObjectMapper().findAndRegisterModules();
    }

    @Provides
    @Singleton
    static LambdaConfig lambdaConfig() {
        return new LambdaConfig();
    }

    @Provides
    @Singleton
    static RandomGenerator randomGenerator() {
        return new SecureRandom();
    }

    @Provides
    static Clock clock() {
        return Clock.systemDefaultZone();
    }
}
