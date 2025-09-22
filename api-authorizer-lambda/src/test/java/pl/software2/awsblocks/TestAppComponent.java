package pl.software2.awsblocks;

import dagger.Component;
import pl.software2.awsblocks.modules.ServicesModule;
import pl.software2.awsblocks.modules.TestAwsModule;
import pl.software2.awsblocks.modules.TestCommonModule;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;

import javax.inject.Singleton;


@Singleton
@Component(modules = {TestCommonModule.class, TestAwsModule.class, ServicesModule.class})
public interface TestAppComponent extends AppComponent {

    SecretsManagerClient secretsManager();
}
