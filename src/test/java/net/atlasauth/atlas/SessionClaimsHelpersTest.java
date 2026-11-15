package net.atlasauth.atlas;

import net.atlasauth.atlas.verify.SessionClaims;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class SessionClaimsHelpersTest {

    @Test
    void authenticatedWithinUsesAuthTime() {
        long now = 1_000_000L;
        SessionClaims c = new SessionClaims(Map.of("auth_time", now - 100));
        assertTrue(c.authenticatedWithin(300, now * 1000));
        assertFalse(c.authenticatedWithin(50, now * 1000));
        assertFalse(new SessionClaims(Map.of()).authenticatedWithin(300, now * 1000));
    }

    @Test
    void multiFactorFollowsAcrAmrAndTheServerVerdict() {
        assertTrue(new SessionClaims(Map.of("acr", "urn:atlas:acr:mfa")).usedMultiFactor());
        assertTrue(new SessionClaims(Map.of("amr", List.of("pwd", "mfa"))).usedMultiFactor());
        assertFalse(new SessionClaims(Map.of("acr", "urn:atlas:acr:basic")).usedMultiFactor());
        assertFalse(new SessionClaims(Map.of("mfa_satisfied", false, "mfa", true)).mfaSatisfied());
        assertTrue(new SessionClaims(Map.of("sso_connection_id", "sso_1")).mfaSatisfied());
        assertFalse(new SessionClaims(Map.of("org_id", "o1", "org_mfa_required", true)).mfaOrgSatisfied("o1"));
        assertTrue(new SessionClaims(Map.of("org_id", "o1", "org_mfa_required", true, "org_mfa_ok", true)).mfaOrgSatisfied("o1"));
    }
}
