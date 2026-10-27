package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code PATCH /v1/feature_flags/{key}}. */
public final class FlagsUpdateBody {
    public String description;
    public Boolean enabled;
    /** A boolean, string, integer or JSON value matching the flag `type`. */
    public Object defaultValue;
    /** A boolean, string, integer or JSON value matching the flag `type`. */
    public Object offValue;
    /** Ordered targeting rules; the first match wins. */
    public List<Map<String, Object>> rules;

    public FlagsUpdateBody() {
    }

    public FlagsUpdateBody description(String v) { this.description = v; return this; }
    public FlagsUpdateBody enabled(Boolean v) { this.enabled = v; return this; }
    public FlagsUpdateBody defaultValue(Object v) { this.defaultValue = v; return this; }
    public FlagsUpdateBody offValue(Object v) { this.offValue = v; return this; }
    public FlagsUpdateBody rules(List<Map<String, Object>> v) { this.rules = v; return this; }
}
