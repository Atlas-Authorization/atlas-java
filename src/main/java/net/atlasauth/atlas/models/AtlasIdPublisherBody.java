package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** The body of {@code PUT /v1/platform/atlas_id_publisher}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public final class AtlasIdPublisherBody {
    /** Shown on consent screens once verified (up to 100 characters). */
    public String name;
    /** The host your apps' homepage_url lives on, e.g. example.com. */
    public String domain;
    /** Publisher-wide consent for verified apps. Default false. */
    public Boolean skipConsent;

    @JsonIgnore
    private final Map<String, Object> additionalFields = new LinkedHashMap<>();

    public AtlasIdPublisherBody() {
    }

    public AtlasIdPublisherBody name(String v) { this.name = v; return this; }
    public AtlasIdPublisherBody domain(String v) { this.domain = v; return this; }
    public AtlasIdPublisherBody skipConsent(Boolean v) { this.skipConsent = v; return this; }

    /**
     * Set any field by its wire name, including an explicit {@code null} (which
     * clears a stored value on a partial update) or a field this SDK does not model yet.
     */
    public AtlasIdPublisherBody set(String key, Object value) { this.additionalFields.put(key, value); return this; }

    @JsonAnyGetter
    public Map<String, Object> additionalFields() { return additionalFields; }
}
