package pl.software2.awsblocks.lambda.routes.content;

import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static pl.software2.awsblocks.lambda.test.ContentUtils.encode;
import static pl.software2.awsblocks.lambda.test.ContentUtils.zip;

class PayloadDecoderTest {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final PayloadDecoder payloadDecoder = new PayloadDecoder(objectMapper);

    @Test
    void givenBasicPayload_whenDecoderExtractsBody_thenObjectWithProperFieldIsReturned() {
        // given

        var request = APIGatewayV2HTTPEvent.builder()
                .withBody("{\"field\":\"value\"}")
                .build();

        // when
        var body = payloadDecoder.extractBody(request, ResponseClass.class);

        // then
        assertThat(body.field()).isEqualTo("value");
    }

    @Test
    void givenEncodedPayload_whenDecoderExtractsBody_thenObjectWithProperFieldIsReturned() {
        // given
        var request = APIGatewayV2HTTPEvent.builder()
                .withBody(encode("{\"field\":\"value\"}"))
                .withHeaders(Map.of())
                .withIsBase64Encoded(true)
                .build();

        // when
        var body = payloadDecoder.extractBody(request, ResponseClass.class);

        // then
        assertThat(body.field()).isEqualTo("value");
    }

    @Test
    void givenEncodedCompressedPayload_whenDecoderExtractsBody_thenObjectWithProperFieldIsReturned() throws Exception {
        // given
        var request = APIGatewayV2HTTPEvent.builder()
                .withBody(encode(zip("{\"field\":\"value\"}")))
                .withHeaders(Map.of("content-encoding", "gzip"))
                .withIsBase64Encoded(true)
                .build();

        // when
        var body = payloadDecoder.extractBody(request, ResponseClass.class);

        // then
        assertThat(body.field()).isEqualTo("value");
    }

}