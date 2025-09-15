package pl.software2.awsblocks.persistence.model.loginrequests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

import java.time.Clock;
import java.time.Duration;


@Data
@Builder(toBuilder = true)
@DynamoDbBean
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequestsTable {
    private String email;
    private String token;
    private Long expiresAt;

    @DynamoDbPartitionKey
    @DynamoDbAttribute("email")
    public String getEmail() {
        return email;
    }

    public LoginRequestsTable recalculateExpiration(Clock now, Duration duration) {
        if (duration.isNegative()) {
            this.expiresAt = now.instant().getEpochSecond();
        } else {
            this.expiresAt = now.instant().plus(duration).getEpochSecond();
        }
        return this;
    }

}
