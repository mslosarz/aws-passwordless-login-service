package pl.software2.awsblocks.modules;

import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntoSet;
import pl.software2.awsblocks.lambda.routes.RouteHandler;
import pl.software2.awsblocks.routes.PostLoginGenerateHandler;

@Module
public class RoutesModule {

    @Provides
    @IntoSet
    static RouteHandler postAuthGenerateHandler(PostLoginGenerateHandler postLoginGenerateHandler) {
        return postLoginGenerateHandler;
    }
}
