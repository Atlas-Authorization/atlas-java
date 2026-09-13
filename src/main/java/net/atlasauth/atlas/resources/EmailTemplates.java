package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.EmailTemplate;
import net.atlasauth.atlas.models.EmailTemplateBody;
import net.atlasauth.atlas.models.EmailTemplatePreview;
import net.atlasauth.atlas.models.EmailTemplateResult;
import net.atlasauth.atlas.models.ListPage;

import static net.atlasauth.atlas.http.Transport.enc;

/** The {@code /v1/email_templates} resource. */
public final class EmailTemplates {

    private final Transport t;

    public EmailTemplates(Transport t) {
        this.t = t;
    }

    public ListPage<EmailTemplate> list() {
        return t.get("/v1/email_templates", new TypeReference<>() {
        });
    }

    /** Render the stored template with no draft override. */
    public EmailTemplatePreview preview(String name) {
        return preview(name, new EmailTemplateBody());
    }

    /** Render an unsaved draft (if a body is given) or the stored template. */
    public EmailTemplatePreview preview(String name, EmailTemplateBody body) {
        return t.post("/v1/email_templates/" + enc(name) + "/preview", body, new TypeReference<>() {
        });
    }

    /** Save an override. Keyed by template name, not an id. */
    public EmailTemplateResult update(String name, EmailTemplateBody body) {
        return t.put("/v1/email_templates/" + enc(name), body, new TypeReference<>() {
        });
    }

    /** Revert to the built-in. */
    public EmailTemplateResult delete(String name) {
        return t.delete("/v1/email_templates/" + enc(name), new TypeReference<>() {
        });
    }
}
