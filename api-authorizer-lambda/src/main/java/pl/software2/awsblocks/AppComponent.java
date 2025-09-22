package pl.software2.awsblocks;

import dagger.Component;
import pl.software2.awsblocks.lambda.CommonModule;
import pl.software2.awsblocks.modules.AwsModule;
import pl.software2.awsblocks.modules.ServicesModule;

import javax.inject.Singleton;

@Singleton
@Component(modules = {CommonModule.class, AwsModule.class, ServicesModule.class})
public interface AppComponent {
    void inject(LambdaHandler lambdaHandler);
}
