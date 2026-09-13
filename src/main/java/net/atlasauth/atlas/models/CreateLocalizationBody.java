package net.atlasauth.atlas.models;

import java.util.Map;

/** Body for {@code POST /v1/localizations}. */
public final class CreateLocalizationBody {
    /** {@code email_template} | {@code sms_template} | {@code prompt_copy}. */
    public String resourceType;
    public String resourceName;
    public String locale;
    public Map<String, Object> content;
    public Boolean enabled;

    public CreateLocalizationBody() {
    }

    public CreateLocalizationBody(String resourceType, String resourceName, String locale, Map<String, Object> content) {
        this.resourceType = resourceType;
        this.resourceName = resourceName;
        this.locale = locale;
        this.content = content;
    }

    public CreateLocalizationBody resourceType(String v) { this.resourceType = v; return this; }
    public CreateLocalizationBody resourceName(String v) { this.resourceName = v; return this; }
    public CreateLocalizationBody locale(String v) { this.locale = v; return this; }
    public CreateLocalizationBody content(Map<String, Object> v) { this.content = v; return this; }
    public CreateLocalizationBody enabled(Boolean v) { this.enabled = v; return this; }
}
