package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.LinkedHashMap;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class CreateSessionExchangeAppBody {
    /** A human label for the downstream app. */
    public String name;
    /** Public https endpoint Atlas POSTs the signed logout_token to (application/x-www-form-urlencoded: logout_token=&lt;jwt&gt;) when a session ends. */
    public String backchannelLogoutUri;
    /** Optional override for the logout_token `aud` claim. Defaults to the app id when omitted. */
    public String audience;

    @JsonIgnore
    private final Map<String, Object> additionalFields = new LinkedHashMap<>();

    public CreateSessionExchangeAppBody() {
    }

    public CreateSessionExchangeAppBody name(String v) { this.name = v; return this; }
    public CreateSessionExchangeAppBody backchannelLogoutUri(String v) { this.backchannelLogoutUri = v; return this; }
    public CreateSessionExchangeAppBody audience(String v) { this.audience = v; return this; }

    /**
     * Set any field by its wire name, including an explicit {@code null} (which
     * clears a stored value on a partial update) or a field this SDK does not model yet.
     */
    public CreateSessionExchangeAppBody set(String key, Object value) { this.additionalFields.put(key, value); return this; }

    @JsonAnyGetter
    public Map<String, Object> additionalFields() { return additionalFields; }
}
