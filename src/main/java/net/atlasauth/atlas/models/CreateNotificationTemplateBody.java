package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.LinkedHashMap;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class CreateNotificationTemplateBody {
    /** The template name a send passes; unique per instance. */
    public String name;
    public String subject;
    /** Email body with `{{variables}}`. */
    public String body;
    /** A built-in or tenant-defined notification category key. */
    public String category;

    @JsonIgnore
    private final Map<String, Object> additionalFields = new LinkedHashMap<>();

    public CreateNotificationTemplateBody() {
    }

    public CreateNotificationTemplateBody name(String v) { this.name = v; return this; }
    public CreateNotificationTemplateBody subject(String v) { this.subject = v; return this; }
    public CreateNotificationTemplateBody body(String v) { this.body = v; return this; }
    public CreateNotificationTemplateBody category(String v) { this.category = v; return this; }

    /**
     * Set any field by its wire name, including an explicit {@code null} (which
     * clears a stored value on a partial update) or a field this SDK does not model yet.
     */
    public CreateNotificationTemplateBody set(String key, Object value) { this.additionalFields.put(key, value); return this; }

    @JsonAnyGetter
    public Map<String, Object> additionalFields() { return additionalFields; }
}
