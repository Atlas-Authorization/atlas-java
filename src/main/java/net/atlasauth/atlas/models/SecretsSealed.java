package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * Says a response was sealed to the key named in {@code X-Secret-Recipient}: each listed field arrives as
 * {@code sealed_<field>} instead of in plaintext. Open one with the SDK's sealed-box helper.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record SecretsSealed(String algorithm, String recipient, List<String> fields) {
}
