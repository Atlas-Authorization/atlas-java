package net.atlasauth.atlas.models;

/** Body for {@code POST /v1/sms_templates} — upserts an override by name. */
public final class CreateSmsTemplateBody {
    public String name;
    public String body;
    public Boolean enabled;

    public CreateSmsTemplateBody() {
    }

    public CreateSmsTemplateBody(String name, String body) {
        this.name = name;
        this.body = body;
    }

    public CreateSmsTemplateBody name(String v) { this.name = v; return this; }
    public CreateSmsTemplateBody body(String v) { this.body = v; return this; }
    public CreateSmsTemplateBody enabled(Boolean v) { this.enabled = v; return this; }
}
