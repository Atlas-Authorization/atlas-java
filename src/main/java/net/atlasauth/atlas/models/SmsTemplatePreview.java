package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** A rendered body with a fixed SAMPLE code ({@code 123456}), never a live OTP. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record SmsTemplatePreview(
        String object,
        String name,
        String body,
        boolean customised) {
}
