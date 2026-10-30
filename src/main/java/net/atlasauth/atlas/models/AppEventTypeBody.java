package net.atlasauth.atlas.models;

import java.util.Map;

/**
 * Body for {@code POST /v1/app_event_types} (with {@code type}) and {@code PATCH /v1/app_event_types/{idOrType}}
 * (without: a type cannot be renamed). {@code set("description", null)} or {@code set("schema", null)} clears.
 */
public final class AppEventTypeBody extends ExtensibleBody {
    /** Lowercase dotted name, e.g. {@code call.completed}. Create only. */
    public String type;
    /** Up to 500 characters; shown in the endpoint picker. */
    public String description;
    /** Optional JSON Schema for {@code data} (at most 32 KB). */
    public Map<String, Object> schema;
    public Boolean deprecated;

    public AppEventTypeBody() {
    }

    public AppEventTypeBody(String type) {
        this.type = type;
    }

    public AppEventTypeBody type(String v) { this.type = v; return this; }
    public AppEventTypeBody description(String v) { this.description = v; return this; }
    public AppEventTypeBody schema(Map<String, Object> v) { this.schema = v; return this; }
    public AppEventTypeBody deprecated(Boolean v) { this.deprecated = v; return this; }
    public AppEventTypeBody set(String key, Object value) { putField(key, value); return this; }
}
