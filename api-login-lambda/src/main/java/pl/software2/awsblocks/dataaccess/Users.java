package pl.software2.awsblocks.dataaccess;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.software2.awsblocks.config.EnvironmentVariables;
import pl.software2.awsblocks.lambda.config.LambdaConfig;
import pl.software2.awsblocks.persistence.model.users.UsersTable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import javax.inject.Inject;
import java.time.Clock;
import java.time.ZoneOffset;

@Slf4j
@AllArgsConstructor(onConstructor = @__(@Inject))
public class Users {
    private final DynamoDbEnhancedClient dynamoDbEnhancedClient;
    private final LambdaConfig lambdaConfig;
    private final Clock clock;

    public void createOrUpdate(String email) {
        var table = getTable();
        var item = table.getItem(Key.builder().partitionValue(email).build());
        if (item == null) {
            table.putItem(UsersTable.builder()
                    .email(email)
                    .createdAt(clock.instant().atZone(ZoneOffset.UTC))
                    .lastLoginAt(clock.instant().atZone(ZoneOffset.UTC))
                    .build());
        } else {
            item.setLastLoginAt(clock.instant().atZone(ZoneOffset.UTC));
            table.updateItem(item);
        }
    }

    private DynamoDbTable<UsersTable> getTable() {
        return dynamoDbEnhancedClient.table(lambdaConfig.getValue(EnvironmentVariables.USERS_TABLE.name()), TableSchema.fromBean(UsersTable.class));
    }
}
