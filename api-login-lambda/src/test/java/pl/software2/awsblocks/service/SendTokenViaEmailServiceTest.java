package pl.software2.awsblocks.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import pl.software2.awsblocks.config.EnvironmentVariables;
import pl.software2.awsblocks.lambda.config.LambdaConfig;
import software.amazon.awssdk.services.sesv2.SesV2Client;
import software.amazon.awssdk.services.sesv2.model.Destination;
import software.amazon.awssdk.services.sesv2.model.EmailContent;
import software.amazon.awssdk.services.sesv2.model.SendEmailRequest;
import software.amazon.awssdk.services.sesv2.model.Template;

import static org.mockito.Mockito.*;

class SendTokenViaEmailServiceTest {
    private final SesV2Client sesV2Client = mock(SesV2Client.class);
    private final LambdaConfig lambdaConfig = mock(LambdaConfig.class);
    private final SendTokenViaEmailService service = new SendTokenViaEmailService(
            sesV2Client,
            lambdaConfig,
            new ObjectMapper()
    );

    @Test
    void givenRequestWithConfigAndGenerator_whenSendingToken_thenTokenIsSent() {
        // given
        when(lambdaConfig.getValue(EnvironmentVariables.DOMAIN_NAME.name())).thenReturn("domain.com");
        when(lambdaConfig.getValue(EnvironmentVariables.NO_REPLY_ACCOUNT.name())).thenReturn("no-reply");
        when(lambdaConfig.getValue(EnvironmentVariables.TEMPLATE_NAME.name())).thenReturn("template");

        // when
        service.sendEmailViaSes("test@mail.com", "123456");

        // then
        verify(lambdaConfig, times(3)).getValue(anyString());
        verify(sesV2Client).sendEmail(
                SendEmailRequest.builder()
                        .fromEmailAddress("no-reply@domain.com")
                        .destination(Destination.builder().toAddresses("test@mail.com").build())
                        .content(EmailContent.builder()
                                .template(Template.builder()
                                        .templateName("template")
                                        .templateData("{\"token\":\"123456\"}")
                                        .build())
                                .build())
                        .build()
        );
        verifyNoMoreInteractions(sesV2Client, lambdaConfig);
    }
}