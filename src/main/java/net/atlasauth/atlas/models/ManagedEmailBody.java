package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** The body of {@code POST /v1/messaging_providers/email/managed}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public final class ManagedEmailBody {
    /** Bare or "Name <address>"; its domain becomes the sending domain. */
    public String fromAddress;
    /** Your own provider account Atlas sets the domain up on: resend or brevo. With {@link #apiKey(String)}, unless {@code providerId} is set. */
    public String transport;
    /** {@code api_key}: your Resend or Brevo API key (write-only). */
    public Map<String, Object> config;
    /** Instead of transport + config: your configured Resend or Brevo email provider. */
    public String providerId;
    /** us-east-1, eu-west-1, sa-east-1 or ap-northeast-1 (Resend only). */
    public String region;
    /** Let Atlas write the DNS records: provider ("cloudflare"), api_token (write-only), overwrite. */
    public Map<String, Object> dns;

    @JsonIgnore
    private final Map<String, Object> additionalFields = new LinkedHashMap<>();

    public ManagedEmailBody() {
    }

    public ManagedEmailBody fromAddress(String v) { this.fromAddress = v; return this; }
    public ManagedEmailBody transport(String v) { this.transport = v; return this; }
    /** Sets {@code config.api_key}. */
    public ManagedEmailBody apiKey(String v) { this.config = Map.of("api_key", v); return this; }
    public ManagedEmailBody providerId(String v) { this.providerId = v; return this; }
    public ManagedEmailBody region(String v) { this.region = v; return this; }
    public ManagedEmailBody dns(Map<String, Object> v) { this.dns = v; return this; }

    /**
     * Set any field by its wire name, including an explicit {@code null} (which
     * clears a stored value on a partial update) or a field this SDK does not model yet.
     */
    public ManagedEmailBody set(String key, Object value) { this.additionalFields.put(key, value); return this; }

    @JsonAnyGetter
    public Map<String, Object> additionalFields() { return additionalFields; }
}
