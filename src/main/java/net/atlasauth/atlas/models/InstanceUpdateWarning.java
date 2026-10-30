package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * One entry of {@link InstanceUpdateResult#warnings()}: what the write did not do as sent.
 * {@code origin_still_referenced} (a removed origin URLs still point at; {@code origin}) or, on a lenient
 * write, {@code unknown_field} (an auth_config key that was ignored; {@code field} is its full path,
 * {@code hint} names the endpoint for a setting that has its own).
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record InstanceUpdateWarning(String code, String message, String field, String hint, String didYouMean, String origin) {
}
