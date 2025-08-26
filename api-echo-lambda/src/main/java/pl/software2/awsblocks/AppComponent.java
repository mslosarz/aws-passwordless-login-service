package pl.software2.awsblocks;

import dagger.Component;
import pl.software2.awsblocks.lambda.CommonModule;
import pl.software2.awsblocks.modules.RoutesModule;

import javax.inject.Singleton;

@Singleton
@Component(modules = {CommonModule.class, RoutesModule.class})
public interface AppComponent {
    void inject(LambdaHandler appHandler);
}
