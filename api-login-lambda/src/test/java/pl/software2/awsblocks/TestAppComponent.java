package pl.software2.awsblocks;

import dagger.Component;
import pl.software2.awsblocks.modules.RoutesModule;
import pl.software2.awsblocks.modules.ServicesModule;
import pl.software2.awsblocks.modules.TestAwsModule;
import pl.software2.awsblocks.modules.TestCommonModule;
import pl.software2.awsblocks.persistence.PersistenceModule;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.sesv2.SesV2Client;

import javax.inject.Singleton;


@Singleton
@Component(modules = {TestCommonModule.class, TestAwsModule.class, RoutesModule.class, ServicesModule.class, PersistenceModule.class})
public interface TestAppComponent extends AppComponent {

    SesV2Client sesV2Client();

    SecretsManagerClient secretsManager();
}
