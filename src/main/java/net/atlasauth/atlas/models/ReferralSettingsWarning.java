package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * What a referral settings write stored differently from what was sent: {@code renamed} (a retired provider
 * name stored under its new one; {@code from} and {@code to} are strings) or {@code defaulted} (a newly picked
 * platform's tracking cookies filled in; {@code to} is a list of strings).
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ReferralSettingsWarning(String code, String field, String from, Object to, String message) {
}
