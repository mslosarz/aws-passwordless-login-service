package pl.software2.awsblocks.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.software2.awsblocks.lambda.config.LambdaConfig;
import pl.software2.awsblocks.persistence.model.loginrequests.LoginRequestsTable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import javax.inject.Inject;
import java.time.Clock;
import java.time.Duration;

import static java.lang.Integer.parseInt;
import static java.time.Duration.ofMinutes;
import static pl.software2.awsblocks.config.EnvironmentVariables.LOGIN_REQUESTS_TABLE;
import static pl.software2.awsblocks.config.EnvironmentVariables.TOKEN_VALIDITY_IN_MINUTES;

@Slf4j
@AllArgsConstructor(onConstructor = @__(@Inject))
public class StoreLoginRequestService {
    private final DynamoDbEnhancedClient dynamoDbEnhancedClient;
    private final LambdaConfig lambdaConfig;
    private final Clock clock;

    public void createOrUpdateLoginRequest(String email, String token) {
        DynamoDbTable<LoginRequestsTable> table = dynamoDbEnhancedClient.table(lambdaConfig.getValue(LOGIN_REQUESTS_TABLE.name()), TableSchema.fromBean(LoginRequestsTable.class));
        LoginRequestsTable loginRequest = createRequestObject(email, token);
        if (requestDoesNotExist(email, table)) {
            table.putItem(loginRequest);
        } else {
            table.updateItem(loginRequest);
        }
    }

    private LoginRequestsTable createRequestObject(String email, String token) {
        Duration minutes = ofMinutes(parseInt(lambdaConfig.getValue(TOKEN_VALIDITY_IN_MINUTES.name())));
        return LoginRequestsTable.builder()
                .email(email)
                .token(token)
                .build()
                .recalculateExpiration(clock, minutes);
    }

    private static boolean requestDoesNotExist(String email, DynamoDbTable<LoginRequestsTable> table) {
        return table.getItem(Key.builder().partitionValue(email).build()) == null;
    }
}
