package pl.software2.awsblocks.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.software2.awsblocks.dataaccess.LoginRequests;
import pl.software2.awsblocks.model.GenerateTokenRequest;
import pl.software2.awsblocks.model.GenerateTokenResponse;

import javax.inject.Inject;
import java.util.random.RandomGenerator;

import static java.lang.String.format;

@Slf4j
@AllArgsConstructor(onConstructor = @__(@Inject))
public class ProcessGenerateLoginRequest {

    private final RandomGenerator randomGenerator;
    private final SendTokenViaEmail sendTokenViaEmail;
    private final LoginRequests loginRequests;

    public GenerateTokenResponse handle(GenerateTokenRequest generateTokenRequest) {
        var token = format("%08d", randomGenerator.nextInt(100_000_000));
        String email = generateTokenRequest.email();
        loginRequests.createOrUpdateLoginRequest(email, token);
        sendTokenViaEmail.sendEmailViaSes(email, token);
        return new GenerateTokenResponse();
    }
}
