package pl.software2.awsblocks.lambda.routes.content;

import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import pl.software2.awsblocks.lambda.model.ErrorResponseModel;

import static org.apache.hc.core5.http.HttpHeaders.CONTENT_ENCODING;
import static org.apache.hc.core5.http.HttpHeaders.CONTENT_TYPE;
import static org.apache.hc.core5.http.HttpStatus.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static pl.software2.awsblocks.lambda.test.ContentUtils.decode;
import static pl.software2.awsblocks.lambda.test.ContentUtils.unzip;

class ApiGatewayResponseProducerTest {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ApiGatewayResponseProducer responseProducer = new ApiGatewayResponseProducer(objectMapper);

    @Test
    void givenResponseClass_whenOkProduced_shouldReturnCompressedResponse() {
        // given
        var test = new ResponseClass("test");

        // when
        APIGatewayV2HTTPResponse result = responseProducer.ok(test);

        // then
        verifyStatusCodeAndHeaders(result, SC_OK);
        assertThat(result.getBody()).isNotEmpty();
    }

    @Test
    void givenNotFoundPathWithMessage_whenProducingResponse_shouldCreateErrorObject() throws Exception {
        // given & when
        APIGatewayV2HTTPResponse result = responseProducer.notFound("not/existing/path/", "msg");

        // then
        verifyStatusCodeAndHeaders(result, SC_NOT_FOUND);
        assertThat(result.getBody()).isNotEmpty();
        ErrorResponseModel errorResponseModel = objectMapper.readValue(unzip(decode(result.getBody())), ErrorResponseModel.class);
        assertThat(errorResponseModel).isEqualTo(new ErrorResponseModel(SC_NOT_FOUND, "Not Found", "not/existing/path/ msg"));
    }

    @Test
    void givenNotFoundPath_whenProducingResponse_shouldCreateErrorObject() throws Exception {
        // given & when
        APIGatewayV2HTTPResponse result = responseProducer.notFound("not/existing/path/");

        // then
        verifyStatusCodeAndHeaders(result, SC_NOT_FOUND);
        assertThat(result.getBody()).isNotEmpty();
        ErrorResponseModel errorResponseModel = objectMapper.readValue(unzip(decode(result.getBody())), ErrorResponseModel.class);
        assertThat(errorResponseModel).isEqualTo(new ErrorResponseModel(SC_NOT_FOUND, "Not Found", "not/existing/path/ was not found"));
    }

    @Test
    void givenBadRequest_whenProducingResponse_shouldCreateErrorObject() throws Exception {
        // given & when
        APIGatewayV2HTTPResponse result = responseProducer.badRequest("Wooops");

        // then
        verifyStatusCodeAndHeaders(result, SC_BAD_REQUEST);
        assertThat(result.getBody()).isNotEmpty();
        ErrorResponseModel errorResponseModel = objectMapper.readValue(unzip(decode(result.getBody())), ErrorResponseModel.class);
        assertThat(errorResponseModel).isEqualTo(new ErrorResponseModel(SC_BAD_REQUEST, "Bad Request", "Wooops"));
    }


    @Test
    void givenInternalServerError_whenProducingResponse_shouldCreateErrorObject() throws Exception {
        // given & when
        APIGatewayV2HTTPResponse result = responseProducer.internalServerError("Wooops");

        // then
        verifyStatusCodeAndHeaders(result, SC_INTERNAL_SERVER_ERROR);
        assertThat(result.getBody()).isNotEmpty();
        ErrorResponseModel errorResponseModel = objectMapper.readValue(unzip(decode(result.getBody())), ErrorResponseModel.class);
        assertThat(errorResponseModel).isEqualTo(new ErrorResponseModel(SC_INTERNAL_SERVER_ERROR, "Internal Server Error", "Wooops"));
    }


    private static void verifyStatusCodeAndHeaders(APIGatewayV2HTTPResponse result, int statusCode) {
        assertThat(result.getStatusCode()).isEqualTo(statusCode);
        assertThat(result.getHeaders()).contains(entry(CONTENT_TYPE, "application/json"), entry(CONTENT_ENCODING, "gzip"));
        assertThat(result.getIsBase64Encoded()).isTrue();
    }

}