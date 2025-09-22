package pl.software2.awsblocks.modules;

import com.fasterxml.jackson.databind.ObjectMapper;
import dagger.Module;
import dagger.Provides;
import pl.software2.awsblocks.config.EnvironmentVariables;
import pl.software2.awsblocks.lambda.config.LambdaConfig;
import pl.software2.awsblocks.lambda.test.Fixtures;

import javax.inject.Singleton;
import java.security.SecureRandom;
import java.time.Clock;
import java.util.Map;
import java.util.random.RandomGenerator;

@Module
public class TestCommonModule {
    @Provides
    @Singleton
    static LambdaConfig lambdaConfig() {
        return new MapConfig(Map.of(
                EnvironmentVariables.DOMAIN_NAME, "domain.com",
                EnvironmentVariables.JWT_TOKEN_SECRET_NAME, ""
        ));
    }

    @Provides
    @Singleton
    static RandomGenerator randomGenerator() {
        return new SecureRandom();
    }

    @Provides
    @Singleton
    static Clock clock() {
        return Fixtures.fixedClock();
    }

    @Provides
    @Singleton
    static ObjectMapper provideObjectMapper() {
        return new ObjectMapper().findAndRegisterModules();
    }

}
