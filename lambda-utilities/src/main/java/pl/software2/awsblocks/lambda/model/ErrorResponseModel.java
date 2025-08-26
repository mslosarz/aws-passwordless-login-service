package pl.software2.awsblocks.lambda.model;

public record ErrorResponseModel(int statusCode, String error, String message) {
    public static ErrorResponseModel notFound(String message) {
        return new ErrorResponseModel(404, "Not Found", message);
    }

    public static ErrorResponseModel badRequest(String message) {
        return new ErrorResponseModel(400, "Bad Request", message);
    }

    public static ErrorResponseModel internalServerError(String message) {
        return new ErrorResponseModel(500, "Internal Server Error", message);
    }
}
