package pl.software2.awsblocks.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.software2.awsblocks.lambda.config.LambdaConfig;
import pl.software2.awsblocks.model.TokenEMailParameters;
import software.amazon.awssdk.services.sesv2.SesV2Client;
import software.amazon.awssdk.services.sesv2.model.Destination;
import software.amazon.awssdk.services.sesv2.model.EmailContent;
import software.amazon.awssdk.services.sesv2.model.SendEmailRequest;
import software.amazon.awssdk.services.sesv2.model.Template;

import javax.inject.Inject;

import static java.lang.String.format;
import static pl.software2.awsblocks.config.EnvironmentVariables.*;

@Slf4j
@AllArgsConstructor(onConstructor = @__(@Inject))
public class SendTokenViaEmailService {
    private final SesV2Client sesV2Client;
    private final LambdaConfig lambdaConfig;
    private final ObjectMapper objectMapper;

    public void sendEmailViaSes(String email, String token) {
        SendEmailRequest build = SendEmailRequest.builder()
                .fromEmailAddress(format("%s@%s", lambdaConfig.getValue(NO_REPLY_ACCOUNT.name()), lambdaConfig.getValue(DOMAIN_NAME.name())))
                .destination(Destination.builder()
                        .toAddresses(email)
                        .build())
                .content(EmailContent.builder()
                        .template(Template.builder()
                                .templateName(lambdaConfig.getValue(TEMPLATE_NAME.name()))
                                .templateData(generateTemplateData(
                                        new TokenEMailParameters(token)
                                ))
                                .build())
                        .build())
                .build();
        log.debug("Send email response: {}", sesV2Client.sendEmail(build));
    }

    private String generateTemplateData(TokenEMailParameters parameters) {
        try {
            return objectMapper.writeValueAsString(parameters);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
