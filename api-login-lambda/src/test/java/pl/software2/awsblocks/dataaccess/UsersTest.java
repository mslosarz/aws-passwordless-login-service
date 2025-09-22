package pl.software2.awsblocks.dataaccess;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import pl.software2.awsblocks.lambda.config.LambdaConfig;
import pl.software2.awsblocks.lambda.test.Fixtures;
import pl.software2.awsblocks.persistence.model.users.UsersTable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import java.time.Clock;
import java.time.Duration;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static pl.software2.awsblocks.config.EnvironmentVariables.USERS_TABLE;

class UsersTest {
    private final DynamoDbEnhancedClient dynamoDbEnhancedClient = mock(DynamoDbEnhancedClient.class);
    private final LambdaConfig lambdaConfig = mock(LambdaConfig.class);
    private final Clock clock = Fixtures.fixedClock();
    private final Users users = new Users(dynamoDbEnhancedClient, lambdaConfig, clock);

    private final DynamoDbTable<UsersTable> table = (DynamoDbTable<UsersTable>) mock(DynamoDbTable.class);
    private final String EMAIL = "test@example.com";

    @Test
    void givenNewUser_whenCreating_thenUserCreated() {
        // given
        when(dynamoDbEnhancedClient.table(any(), any(TableSchema.class))).thenReturn(table);
        when(table.getItem(any(Key.class))).thenReturn(null);
        when(lambdaConfig.getValue(any())).thenReturn("UsersTable");

        // when
        users.createOrUpdate(EMAIL);

        // then
        verify(table).getItem(Key.builder().partitionValue(EMAIL).build());
        ArgumentCaptor<UsersTable> captor = ArgumentCaptor.forClass(UsersTable.class);
        verify(table).putItem(captor.capture());

        var createdUser = captor.getValue();
        assertThat(createdUser.getEmail()).isEqualTo(EMAIL);
        assertThat(createdUser.getCreatedAt()).isEqualTo(createdUser.getLastLoginAt());
        assertThat(createdUser.getCreatedAt()).isEqualTo(clock.instant().atZone(ZoneOffset.UTC));
        verify(lambdaConfig).getValue(USERS_TABLE.name());
        verifyNoMoreInteractions(table, lambdaConfig);
    }

    @Test
    void givenExistingUser_whenCalling_thenUserUpdated() {
        // given
        when(dynamoDbEnhancedClient.table(any(), any(TableSchema.class))).thenReturn(table);
        var twoDaysAgo = Fixtures.fixedClock().instant().minus(Duration.ofDays(2)).atZone(ZoneOffset.UTC);
        var existingUser = UsersTable.builder()
                .email(EMAIL)
                .lastLoginAt(twoDaysAgo)
                .createdAt(twoDaysAgo)
                .build();
        when(table.getItem(any(Key.class))).thenReturn(existingUser);
        when(lambdaConfig.getValue(any())).thenReturn("UsersTable");

        // when
        users.createOrUpdate(EMAIL);

        // then
        verify(table).getItem(Key.builder().partitionValue(EMAIL).build());
        ArgumentCaptor<UsersTable> captor = ArgumentCaptor.forClass(UsersTable.class);
        verify(table).updateItem(captor.capture());

        var createdUser = captor.getValue();
        assertThat(createdUser.getEmail()).isEqualTo(EMAIL);
        assertThat(createdUser.getCreatedAt()).isNotEqualTo(createdUser.getLastLoginAt());
        assertThat(createdUser.getLastLoginAt()).isEqualTo(clock.instant().atZone(ZoneOffset.UTC));
        assertThat(createdUser.getCreatedAt()).isEqualTo(twoDaysAgo);
        verify(lambdaConfig).getValue(USERS_TABLE.name());
        verifyNoMoreInteractions(table, lambdaConfig);
    }
}