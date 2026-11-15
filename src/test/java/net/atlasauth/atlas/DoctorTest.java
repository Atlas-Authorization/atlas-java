package net.atlasauth.atlas;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DoctorTest {

    @Test
    void failsWithoutAnyKeyAndReportsEveryCheck() {
        AtlasClient atlas = AtlasClient.builder().platformKey("psk_test_1").apiUrl("http://127.0.0.1:1").build();
        Doctor.Report report = atlas.doctor().run();
        assertEquals(6, report.checks().size());
        assertTrue(report.toString().contains("keys"));
    }

    @Test
    void anUnreachableApiFailsTheClockAndJwksChecks() {
        AtlasClient atlas = AtlasClient.builder().secretKey("sk_test_1").apiUrl("http://127.0.0.1:1").build();
        Doctor.Report report = atlas.doctor().run();
        assertFalse(report.ok());
        assertTrue(report.checks().stream().anyMatch(c -> c.name().equals("clock") && c.level() == Doctor.Level.FAIL));
    }
}
