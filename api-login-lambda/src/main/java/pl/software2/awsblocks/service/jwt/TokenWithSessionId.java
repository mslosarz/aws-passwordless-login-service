package pl.software2.awsblocks.service.jwt;

public record TokenWithSessionId(String token, String sessionId) {
}
