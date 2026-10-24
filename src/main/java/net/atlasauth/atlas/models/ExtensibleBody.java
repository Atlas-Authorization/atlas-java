package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Base of the request bodies that accept any wire field: {@link #putField} sets one by its wire name,
 * including an explicit {@code null} (which clears a stored value on a partial update) or a field this
 * SDK does not model yet.
 */
public abstract class ExtensibleBody {

    @JsonIgnore
    private final Map<String, Object> additionalFields = new LinkedHashMap<>();

    protected final void putField(String key, Object value) {
        this.additionalFields.put(key, value);
    }

    /** Send the named nullable fields (wire names) as {@code null}, which clears the stored value. */
    protected final void putNulls(String... keys) {
        for (String k : keys) {
            this.additionalFields.put(k, null);
        }
    }

    /** The extra wire fields set so far (serialized flat into the body). */
    @JsonAnyGetter
    public Map<String, Object> additionalFields() {
        return additionalFields;
    }
}
