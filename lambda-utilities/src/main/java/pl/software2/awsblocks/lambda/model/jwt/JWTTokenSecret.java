package pl.software2.awsblocks.lambda.model.jwt;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JWTTokenSecret {
    private byte[] secret;

    public boolean isEmpty(){
        return secret == null || secret.length == 0;
    }
}
