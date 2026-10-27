package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code POST /v1/feature_flags}. Required: key, type, defaultValue. */
public final class FlagsCreateBody {
    /** Lowercase letters, digits, - and _. Required. */
    public String key;
    /** Required. */
    public String type;
    public String description;
    public Boolean enabled;
    /** A boolean, string, integer or JSON value matching the flag `type`. Required. */
    public Object defaultValue;
    /** A boolean, string, integer or JSON value matching the flag `type`. */
    public Object offValue;
    /** Ordered targeting rules; the first match wins. */
    public List<Map<String, Object>> rules;

    public FlagsCreateBody() {
    }

    public FlagsCreateBody key(String v) { this.key = v; return this; }
    public FlagsCreateBody type(String v) { this.type = v; return this; }
    public FlagsCreateBody description(String v) { this.description = v; return this; }
    public FlagsCreateBody enabled(Boolean v) { this.enabled = v; return this; }
    public FlagsCreateBody defaultValue(Object v) { this.defaultValue = v; return this; }
    public FlagsCreateBody offValue(Object v) { this.offValue = v; return this; }
    public FlagsCreateBody rules(List<Map<String, Object>> v) { this.rules = v; return this; }
}
