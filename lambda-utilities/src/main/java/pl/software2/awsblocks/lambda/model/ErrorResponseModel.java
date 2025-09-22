package pl.software2.awsblocks.lambda.model;

import static org.apache.hc.core5.http.HttpStatus.*;

public record ErrorResponseModel(int statusCode, String error, String message) {
    public static ErrorResponseModel notFound(String message) {
        return new ErrorResponseModel(SC_NOT_FOUND, "Not Found", message);
    }

    public static ErrorResponseModel badRequest(String message) {
        return new ErrorResponseModel(SC_BAD_REQUEST, "Bad Request", message);
    }

    public static ErrorResponseModel unauthorized() {
        return new ErrorResponseModel(SC_UNAUTHORIZED, "Unauthorized", "Unauthorized");
    }

    public static ErrorResponseModel internalServerError(String message) {
        return new ErrorResponseModel(SC_INTERNAL_SERVER_ERROR, "Internal Server Error", message);
    }
}
