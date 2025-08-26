package pl.software2.awsblocks.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ApiGwAuthorizerBasicResponse {
    @JsonProperty("isAuthorized")
    private boolean authorized;
    private Map<String, String> context;
}
