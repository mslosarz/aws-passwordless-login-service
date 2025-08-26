package pl.software2.awsblocks.modules;

import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntoSet;
import pl.software2.awsblocks.lambda.routes.RouteHandler;
import pl.software2.awsblocks.routes.Echo;

@Module
public class RoutesModule {
    @Provides
    @IntoSet
    static RouteHandler echo(Echo echo) {
        return echo;
    }
}
