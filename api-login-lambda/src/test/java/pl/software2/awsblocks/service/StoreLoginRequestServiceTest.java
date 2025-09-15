package pl.software2.awsblocks.service;

import org.junit.jupiter.api.Test;
import pl.software2.awsblocks.lambda.config.LambdaConfig;
import pl.software2.awsblocks.persistence.model.loginrequests.LoginRequestsTable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import static java.time.Duration.ofMinutes;
import static org.mockito.Mockito.*;
import static pl.software2.awsblocks.config.EnvironmentVariables.LOGIN_REQUESTS_TABLE;
import static pl.software2.awsblocks.config.EnvironmentVariables.TOKEN_VALIDITY_IN_MINUTES;
import static software.amazon.awssdk.enhanced.dynamodb.TableSchema.fromBean;

class StoreLoginRequestServiceTest {
    private final DynamoDbEnhancedClient dynamoDbEnhancedClient = mock(DynamoDbEnhancedClient.class);
    private final LambdaConfig lambdaConfig = mock(LambdaConfig.class);
    private final DynamoDbTable<LoginRequestsTable> table = (DynamoDbTable<LoginRequestsTable>) mock(DynamoDbTable.class);
    private final Clock clock = Clock.fixed(Instant.parse("2025-01-01T12:00:00.00Z"), ZoneId.of("CET"));
    private final StoreLoginRequestService service = new StoreLoginRequestService(
            dynamoDbEnhancedClient,
            lambdaConfig,
            clock
    );

    @Test
    void givenConfigAndEmptyDynamoDbTable_whenStoring_thenNewEntryStored() {
        // given
        when(lambdaConfig.getValue(LOGIN_REQUESTS_TABLE.name())).thenReturn("LOGIN_REQUESTS_TABLE");
        when(lambdaConfig.getValue(TOKEN_VALIDITY_IN_MINUTES.name())).thenReturn("30");
        when(dynamoDbEnhancedClient.table(any(), any(TableSchema.class))).thenReturn(table);

        // when
        service.createOrUpdateLoginRequest("test@mail.com", "123456");

        // then
        verify(table).getItem(Key.builder().partitionValue("test@mail.com").build());
        verify(table).putItem(LoginRequestsTable.builder()
                .email("test@mail.com")
                .token("123456")
                .expiresAt(clock.instant().plus(ofMinutes(30)).getEpochSecond())
                .build());
        verify(lambdaConfig).getValue(LOGIN_REQUESTS_TABLE.name());
        verify(lambdaConfig).getValue(TOKEN_VALIDITY_IN_MINUTES.name());
        verify(dynamoDbEnhancedClient).table("LOGIN_REQUESTS_TABLE", fromBean(LoginRequestsTable.class));
        verifyNoMoreInteractions(table, lambdaConfig, dynamoDbEnhancedClient);
    }

    @Test
    void givenConfigAndExistingItem_whenStoring_thenNewEntryStored() {
        // given
        when(lambdaConfig.getValue(LOGIN_REQUESTS_TABLE.name())).thenReturn("LOGIN_REQUESTS_TABLE");
        when(lambdaConfig.getValue(TOKEN_VALIDITY_IN_MINUTES.name())).thenReturn("30");
        when(dynamoDbEnhancedClient.table(any(), any(TableSchema.class))).thenReturn(table);
        when(table.getItem(any(Key.class))).thenReturn(LoginRequestsTable.builder().build());

        // when
        service.createOrUpdateLoginRequest("test@mail.com", "123456");

        // then
        verify(table).getItem(Key.builder().partitionValue("test@mail.com").build());
        verify(table).updateItem(LoginRequestsTable.builder()
                .email("test@mail.com")
                .token("123456")
                .expiresAt(clock.instant().plus(ofMinutes(30)).getEpochSecond())
                .build());
        verify(lambdaConfig).getValue(LOGIN_REQUESTS_TABLE.name());
        verify(lambdaConfig).getValue(TOKEN_VALIDITY_IN_MINUTES.name());
        verify(dynamoDbEnhancedClient).table("LOGIN_REQUESTS_TABLE", fromBean(LoginRequestsTable.class));
        verifyNoMoreInteractions(table, lambdaConfig, dynamoDbEnhancedClient);
    }
}