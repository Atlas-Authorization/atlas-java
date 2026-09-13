package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** {@code GET /v1/attack_protection} — the instance's attack-protection config. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record AttackProtection(
        String object,
        AttackProtectionBruteForce bruteForce,
        AttackProtectionBreachedPassword breachedPassword,
        AttackProtectionSuspiciousIp suspiciousIp,
        AttackProtectionCaptcha captcha) {
}
