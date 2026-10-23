package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Body for {@code POST /v1/policies/:id/versions}: a draft, or published at once with {@code publish}.
 */
public final class CreatePolicyVersionBody {
    /** A label such as 2026-10-24 or 1.2.0, unique per policy. */ public String version;
    /** Rendered and sanitised by Atlas; or contentHtml. */ public String contentMarkdown;
    public String contentHtml;
    public String contentUrl;
    public String summaryOfChanges;
    public String locale;
    /** Publish immediately instead of saving a draft. */ public Boolean publish;
    /** With publish: when the gate starts asking for it (epoch ms Long or ISO 8601 String; default now). */ public Object effectiveAt;

    @JsonIgnore
    private final Map<String, Object> additionalFields = new LinkedHashMap<>();

    public CreatePolicyVersionBody() {
    }

    public CreatePolicyVersionBody version(String v) { this.version = v; return this; }
    public CreatePolicyVersionBody contentMarkdown(String v) { this.contentMarkdown = v; return this; }
    public CreatePolicyVersionBody contentHtml(String v) { this.contentHtml = v; return this; }
    public CreatePolicyVersionBody contentUrl(String v) { this.contentUrl = v; return this; }
    public CreatePolicyVersionBody summaryOfChanges(String v) { this.summaryOfChanges = v; return this; }
    public CreatePolicyVersionBody locale(String v) { this.locale = v; return this; }
    public CreatePolicyVersionBody publish(Boolean v) { this.publish = v; return this; }
    public CreatePolicyVersionBody effectiveAt(Object v) { this.effectiveAt = v; return this; }

    /** Clear a nullable field by its wire name ({@code content_url}, {@code summary_of_changes}, {@code locale}): it is sent as {@code null}. */
    public CreatePolicyVersionBody clear(String key) { this.additionalFields.put(key, null); return this; }

    /** Set any field by its wire name, including one this SDK does not model yet. */
    public CreatePolicyVersionBody set(String key, Object value) { this.additionalFields.put(key, value); return this; }

    @JsonAnyGetter
    public Map<String, Object> additionalFields() { return additionalFields; }
}
