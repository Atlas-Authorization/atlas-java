package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.LinkedHashMap;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class LegacyAuthConnectionBody {
    /** Whether sign-in consults the legacy store. Default false. */
    public Boolean enabled;
    /** The tenant's HTTPS verify endpoint. Atlas POSTs an HMAC-signed {identifier, password}; a 2xx with {user:{email,...}} authenticates. Public https required on a production instance. */
    public String verifyUrl;
    /** HMAC signing secret for the verify webhook. Write-only; required on create, omit on edit to keep the stored one. Never returned. */
    public String signingSecret;
    /** Create the user locally (hashing the verified password) on a successful verify — the drain. Default true. False mirrors the profile but keeps the password in the legacy store. */
    public Boolean importOnLogin;
    /** Fail-closed call budget, 1–30000. Default 5000. */
    public Long timeoutMs;
    /** Only "password" today. */
    public String strategy;

    @JsonIgnore
    private final Map<String, Object> additionalFields = new LinkedHashMap<>();

    public LegacyAuthConnectionBody() {
    }

    public LegacyAuthConnectionBody enabled(Boolean v) { this.enabled = v; return this; }
    public LegacyAuthConnectionBody verifyUrl(String v) { this.verifyUrl = v; return this; }
    public LegacyAuthConnectionBody signingSecret(String v) { this.signingSecret = v; return this; }
    public LegacyAuthConnectionBody importOnLogin(Boolean v) { this.importOnLogin = v; return this; }
    public LegacyAuthConnectionBody timeoutMs(Long v) { this.timeoutMs = v; return this; }
    public LegacyAuthConnectionBody strategy(String v) { this.strategy = v; return this; }

    /**
     * Set any field by its wire name, including an explicit {@code null} (which
     * clears a stored value on a partial update) or a field this SDK does not model yet.
     */
    public LegacyAuthConnectionBody set(String key, Object value) { this.additionalFields.put(key, value); return this; }

    @JsonAnyGetter
    public Map<String, Object> additionalFields() { return additionalFields; }
}
