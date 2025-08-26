package pl.software2.awsblocks.service.auth;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import pl.software2.awsblocks.lambda.config.LambdaConfig;
import pl.software2.awsblocks.model.GenerateTokenRequest;
import pl.software2.awsblocks.model.GenerateTokenResponse;
import pl.software2.awsblocks.model.TokenEMailParameters;
import software.amazon.awssdk.services.sesv2.SesV2Client;
import software.amazon.awssdk.services.sesv2.model.*;

import javax.inject.Inject;
import java.util.Random;

import static pl.software2.awsblocks.config.EnvironmentVariables.DOMAIN_NAME;
import static pl.software2.awsblocks.config.EnvironmentVariables.TEMPLATE_NAME;

@Slf4j
public class StoreLoginRequestAndSendToken {
    private final SesV2Client client;
    private final LambdaConfig lambdaConfig;
    private final ObjectMapper objectMapper;
    private final Random random;

    @Inject
    public StoreLoginRequestAndSendToken(SesV2Client client, LambdaConfig lambdaConfig, ObjectMapper objectMapper) {
        this.client = client;
        this.lambdaConfig = lambdaConfig;
        this.objectMapper = objectMapper;
        this.random = new Random();
    }

    public GenerateTokenResponse handle(GenerateTokenRequest generateTokenRequest) {
        SendEmailRequest build = SendEmailRequest.builder()
                .fromEmailAddress("no-reply@" + lambdaConfig.getValue(DOMAIN_NAME.name()))
                .destination(Destination.builder()
                        .toAddresses(generateTokenRequest.email())
                        .build())
                .content(EmailContent.builder()
                        .template(Template.builder()
                                .templateName(lambdaConfig.getValue(TEMPLATE_NAME.name()))
                                .templateData(generateTemplateData(
                                        new TokenEMailParameters(
                                                String.format("%06d", random.nextInt(1_000_000))
                                        )))
                                .build())
                        .build())
                .build();
        try {
            SendEmailResponse sendEmailResponse = client.sendEmail(build);
            log.info("Send email response: {}", sendEmailResponse);
        } catch (Exception e) {
            log.error("Internal server error", e);
        }
        return new GenerateTokenResponse();
    }

    private String generateTemplateData(TokenEMailParameters parameters) {
        try {
            return objectMapper.writeValueAsString(parameters);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
