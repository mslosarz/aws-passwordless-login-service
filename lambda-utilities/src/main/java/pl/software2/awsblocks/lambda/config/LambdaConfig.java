package pl.software2.awsblocks.lambda.config;

import javax.inject.Inject;

public class LambdaConfig {
    @Inject
    public LambdaConfig() {
    }

    public String getValue(String variable) {
        return System.getenv(variable);
    }
}
