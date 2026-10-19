package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** The body of an origin move: {@code from} and {@code to} are {@code scheme://host[:port]} origins. */
@JsonIgnoreProperties(ignoreUnknown = true)
public final class OriginMoveBody {
    /** The old origin, e.g. https://old.example.com. */
    public String from;
    /** The new origin: https, or http only for localhost. */
    public String to;
    /** true reports the diff and any problems and writes nothing. */
    public Boolean dryRun;

    @JsonIgnore
    private final Map<String, Object> additionalFields = new LinkedHashMap<>();

    public OriginMoveBody() {
    }

    public OriginMoveBody from(String v) { this.from = v; return this; }
    public OriginMoveBody to(String v) { this.to = v; return this; }
    public OriginMoveBody dryRun(Boolean v) { this.dryRun = v; return this; }

    /**
     * Set any field by its wire name, including an explicit {@code null} (which
     * clears a stored value on a partial update) or a field this SDK does not model yet.
     */
    public OriginMoveBody set(String key, Object value) { this.additionalFields.put(key, value); return this; }

    @JsonAnyGetter
    public Map<String, Object> additionalFields() { return additionalFields; }
}
