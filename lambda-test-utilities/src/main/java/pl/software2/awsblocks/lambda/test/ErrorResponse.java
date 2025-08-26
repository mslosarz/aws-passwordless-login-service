package pl.software2.awsblocks.lambda.test;

public record ErrorResponse(int statusCode, String error, String message) {
}
