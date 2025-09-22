package pl.software2.awsblocks.modules;

import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntoSet;
import pl.software2.awsblocks.lambda.routes.RouteHandler;
import pl.software2.awsblocks.routes.PostLoginGenerateHandler;
import pl.software2.awsblocks.routes.PostLoginPerformHandler;

@Module
public class RoutesModule {

    @Provides
    @IntoSet
    static RouteHandler postAuthGenerateHandler(PostLoginGenerateHandler postLoginGenerateHandler) {
        return postLoginGenerateHandler;
    }

    @Provides
    @IntoSet
    static RouteHandler postLoginHandler(PostLoginPerformHandler postLoginPerformHandler) {
        return postLoginPerformHandler;
    }
}
