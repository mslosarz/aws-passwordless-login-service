package pl.software2.awsblocks;

import dagger.Component;
import pl.software2.awsblocks.lambda.CommonModule;

import javax.inject.Singleton;

@Singleton
@Component(modules = {CommonModule.class})
public interface AppComponent {
    void inject(LambdaHandler lambdaHandler);
}
