package pl.software2.awsblocks.lambda.model.jwt;

import lombok.experimental.UtilityClass;

@UtilityClass
public class JwtTokenSharedFields {
    public static final String USER_ROLE = "user";
    public static final String USER_ID = "userId";
    public static final String CLAIM_WITH_ROLES = "roles";
    public static final String CLAIM_WITH_EMAIL = "email";
    public static final String CLAIM_WITH_SESSION_FINGERPRINT = "sessionFingerprint";
}
