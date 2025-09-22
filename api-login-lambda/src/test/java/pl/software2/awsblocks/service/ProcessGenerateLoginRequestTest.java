package pl.software2.awsblocks.service;

import org.junit.jupiter.api.Test;
import pl.software2.awsblocks.dataaccess.LoginRequests;
import pl.software2.awsblocks.model.GenerateTokenRequest;

import java.util.random.RandomGenerator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ProcessGenerateLoginRequestTest {
    private final SendTokenViaEmail sendTokenViaEmail = mock(SendTokenViaEmail.class);
    private final LoginRequests loginRequests = mock(LoginRequests.class);
    private final RandomGenerator randomGenerator = mock(RandomGenerator.class);
    private final ProcessGenerateLoginRequest service = new ProcessGenerateLoginRequest(
            randomGenerator,
            sendTokenViaEmail,
            loginRequests
    );

    @Test
    void givenRequest_whenHandling_thenTokenShouldBeStoredAndEmailShouldBeSent() {
        // given
        var request = new GenerateTokenRequest("test@email.com");
        when(randomGenerator.nextInt(anyInt())).thenReturn(123456);

        // when
        var result = service.handle(request);

        // then
        assertThat(result).isNotNull();
        verify(randomGenerator).nextInt(100_000_000);
        verify(sendTokenViaEmail).sendEmailViaSes("test@email.com", "00123456");
        verify(loginRequests).createOrUpdateLoginRequest("test@email.com", "00123456");
        verifyNoMoreInteractions(loginRequests, sendTokenViaEmail, randomGenerator);
    }
}