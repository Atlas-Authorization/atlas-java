package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** A DNS record to publish for the managed sending domain. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ManagedEmailRecord(
        /** dkim, spf, return_path or other. */ String purpose,
        /** TXT, MX or CNAME. */ String type,
        String name,
        String value,
        Integer priority,
        String status) {
}
