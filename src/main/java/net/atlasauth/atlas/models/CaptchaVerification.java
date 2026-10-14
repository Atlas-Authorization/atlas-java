package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CaptchaVerification(
        String object,
        boolean success,
        String action,
        String provider,
        Double score,
        String verdict,
        String challengeTs,
        Map<String, Object> meta) {
}
