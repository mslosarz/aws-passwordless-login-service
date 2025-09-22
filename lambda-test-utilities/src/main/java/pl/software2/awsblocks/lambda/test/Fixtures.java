package pl.software2.awsblocks.lambda.test;

import lombok.experimental.UtilityClass;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

@UtilityClass
public class Fixtures {
    public static Clock fixedClock() {
        return fixedClock("2025-01-01T12:00:00.00Z");
    }

    public static Clock fixedClock(String date) {
        return Clock.fixed(Instant.parse(date), ZoneId.of("UTC"));
    }
}
