package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.LinkedHashMap;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class UpdateSessionExchangeAppBody {
    public String name;
    /** Public https endpoint. */
    public String backchannelLogoutUri;
    public String audience;

    @JsonIgnore
    private final Map<String, Object> additionalFields = new LinkedHashMap<>();

    public UpdateSessionExchangeAppBody() {
    }

    public UpdateSessionExchangeAppBody name(String v) { this.name = v; return this; }
    public UpdateSessionExchangeAppBody backchannelLogoutUri(String v) { this.backchannelLogoutUri = v; return this; }
    public UpdateSessionExchangeAppBody audience(String v) { this.audience = v; return this; }

    /**
     * Set any field by its wire name, including an explicit {@code null} (which
     * clears a stored value on a partial update) or a field this SDK does not model yet.
     */
    public UpdateSessionExchangeAppBody set(String key, Object value) { this.additionalFields.put(key, value); return this; }

    @JsonAnyGetter
    public Map<String, Object> additionalFields() { return additionalFields; }
}
