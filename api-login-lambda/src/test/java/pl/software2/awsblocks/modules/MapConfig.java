package pl.software2.awsblocks.modules;

import lombok.AllArgsConstructor;
import lombok.Builder;
import pl.software2.awsblocks.config.EnvironmentVariables;
import pl.software2.awsblocks.lambda.config.LambdaConfig;

import java.util.Map;

@Builder
@AllArgsConstructor
public class MapConfig extends LambdaConfig {
    private final Map<EnvironmentVariables, String> configs;

    @Override
    public String getValue(String variable) {
        return configs.get(EnvironmentVariables.valueOf(variable));
    }
}
