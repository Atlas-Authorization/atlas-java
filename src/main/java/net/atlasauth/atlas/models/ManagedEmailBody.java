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
    /** us-east-1, eu-west-1, sa-east-1 or ap-northeast-1. */
    public String region;
    /** Let Atlas write the DNS records: provider ("cloudflare"), api_token (write-only), overwrite. */
    public Map<String, Object> dns;

    @JsonIgnore
    private final Map<String, Object> additionalFields = new LinkedHashMap<>();

    public ManagedEmailBody() {
    }

    public ManagedEmailBody fromAddress(String v) { this.fromAddress = v; return this; }
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
