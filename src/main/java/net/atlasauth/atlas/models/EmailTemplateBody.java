package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** The customizable body of an email template: a subject line and a text body. */
@JsonIgnoreProperties(ignoreUnknown = true)
public final class EmailTemplateBody {
    public String subject;
    public String text;

    public EmailTemplateBody() {
    }

    public EmailTemplateBody subject(String v) { this.subject = v; return this; }
    public EmailTemplateBody text(String v) { this.text = v; return this; }
}
