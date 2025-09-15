package pl.software2.awsblocks.persistence;

import dagger.Module;
import dagger.Provides;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

@Module
public class PersistenceModule {
    @Provides
    static DynamoDbClient dynamoDbClient() {
        return DynamoDbClient.builder().build();
    }

    @Provides
    static DynamoDbEnhancedClient enhancedDocument(DynamoDbClient dynamoDbClient) {
        return DynamoDbEnhancedClient.builder().dynamoDbClient(dynamoDbClient).build();
    }
}
