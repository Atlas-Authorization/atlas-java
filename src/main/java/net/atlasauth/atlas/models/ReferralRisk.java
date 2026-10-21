package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/** The risk verdict sent with a sign-up (camelCase on the wire, as on the referral protocol). */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ReferralRisk(
        /** low, medium or high. */ String level,
        /** 0-100. */ int score,
        /** e.g. self_referral, ip_velocity, disposable_email, datacenter_ip, captcha_not_passed. */ List<String> signals,
        /** passed, not_required or not_configured. */ String captcha,
        @JsonProperty("emailVerified") boolean emailVerified) {
}
