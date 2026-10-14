package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.LinkedHashMap;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class SendNotificationBody {
    /** The end-user to notify. */
    public String userId;
    /** A tenant-defined template name, or a built-in (new_device, unattended_access, password_changed, mfa_changed). */
    public String template;
    /** Variables interpolated into the template (e.g. device, location, when, url). */
    public Map<String, String> data;

    @JsonIgnore
    private final Map<String, Object> additionalFields = new LinkedHashMap<>();

    public SendNotificationBody() {
    }

    public SendNotificationBody userId(String v) { this.userId = v; return this; }
    public SendNotificationBody template(String v) { this.template = v; return this; }
    public SendNotificationBody data(Map<String, String> v) { this.data = v; return this; }

    /**
     * Set any field by its wire name, including an explicit {@code null} (which
     * clears a stored value on a partial update) or a field this SDK does not model yet.
     */
    public SendNotificationBody set(String key, Object value) { this.additionalFields.put(key, value); return this; }

    @JsonAnyGetter
    public Map<String, Object> additionalFields() { return additionalFields; }
}
