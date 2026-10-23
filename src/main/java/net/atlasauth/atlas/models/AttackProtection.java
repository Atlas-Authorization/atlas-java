package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** {@code GET /v1/attack_protection} — the instance's attack-protection config. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record AttackProtection(
        String object,
        AttackProtectionBruteForce bruteForce,
        AttackProtectionBreachedPassword breachedPassword,
        AttackProtectionSuspiciousIp suspiciousIp,
        AttackProtectionCaptcha captcha,
        /** Dormant-account reactivation alerts (writable). */ DormantAccountsSettings dormantAccounts) {

    /** The 1.11 shape, before the custom-data, add-on, enterprise and token-format fields. */
    public AttackProtection(String object, AttackProtectionBruteForce bruteForce, AttackProtectionBreachedPassword breachedPassword, AttackProtectionSuspiciousIp suspiciousIp, AttackProtectionCaptcha captcha) {
        this(object, bruteForce, breachedPassword, suspiciousIp, captcha, null);
    }
}
