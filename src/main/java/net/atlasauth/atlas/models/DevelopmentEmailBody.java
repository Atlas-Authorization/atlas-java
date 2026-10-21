package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The body of {@code POST /v1/messaging_providers/email/development}: {@code providerId} of your
 * configured email provider, or {@code transport} + {@code config} exactly as {@code setEmail}
 * takes them.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public final class DevelopmentEmailBody {
    public String providerId;
    /** resend, ses, smtp, brevo or mailgun. */
    public String transport;
    /** A sender your provider accepts without a verified domain (default for Resend: onboarding@resend.dev). */
    public String fromAddress;
    /** The transport's settings (secrets write-only). */
    public Map<String, Object> config;

    @JsonIgnore
    private final Map<String, Object> additionalFields = new LinkedHashMap<>();

    public DevelopmentEmailBody() {
    }

    public DevelopmentEmailBody providerId(String v) { this.providerId = v; return this; }
    public DevelopmentEmailBody transport(String v) { this.transport = v; return this; }
    public DevelopmentEmailBody fromAddress(String v) { this.fromAddress = v; return this; }
    public DevelopmentEmailBody config(Map<String, Object> v) { this.config = v; return this; }

    /** Set any field by its wire name, including one this SDK does not model yet. */
    public DevelopmentEmailBody set(String key, Object value) { this.additionalFields.put(key, value); return this; }

    @JsonAnyGetter
    public Map<String, Object> additionalFields() { return additionalFields; }
}
