package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/** The connectivity test's honest verdict: what it proved, and what it did not. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record OAuthConnectionTest(
        String object,
        String provider,
        boolean ok,
        String reason,
        String message,
        /** The credential facts this check verified. */
        List<String> checked,
        /** What a pass deliberately does NOT prove (redirect registration, consent…). */
        List<String> notChecked) {
}
