package pl.software2.awsblocks.service;

import org.junit.jupiter.api.Test;
import pl.software2.awsblocks.model.GenerateTokenRequest;

import java.util.random.RandomGenerator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ProcessGenerateLoginRequestServiceTest {
    private final SendTokenViaEmailService sendTokenViaEmailService = mock(SendTokenViaEmailService.class);
    private final StoreLoginRequestService storeLoginRequestService = mock(StoreLoginRequestService.class);
    private final RandomGenerator randomGenerator = mock(RandomGenerator.class);
    private final ProcessGenerateLoginRequestService service = new ProcessGenerateLoginRequestService(
            randomGenerator,
            sendTokenViaEmailService,
            storeLoginRequestService
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
        verify(sendTokenViaEmailService).sendEmailViaSes("test@email.com", "00123456");
        verify(storeLoginRequestService).createOrUpdateLoginRequest("test@email.com", "00123456");
        verifyNoMoreInteractions(storeLoginRequestService, sendTokenViaEmailService, randomGenerator);
    }
}