package pl.software2.awsblocks.lambda.exceptions;

public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException() {
        super();
    }
    public UnauthorizedException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnauthorizedException(String message) {
        super(message);
    }
}
