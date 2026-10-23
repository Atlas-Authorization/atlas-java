package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Body for {@code PATCH /v1/policies/:id/versions/:versionId} (drafts only).
 */
public final class UpdatePolicyVersionBody {
    public String version;
    public String contentMarkdown;
    public String contentHtml;
    public String contentUrl;
    public String summaryOfChanges;
    public String locale;

    @JsonIgnore
    private final Map<String, Object> additionalFields = new LinkedHashMap<>();

    public UpdatePolicyVersionBody() {
    }

    public UpdatePolicyVersionBody version(String v) { this.version = v; return this; }
    public UpdatePolicyVersionBody contentMarkdown(String v) { this.contentMarkdown = v; return this; }
    public UpdatePolicyVersionBody contentHtml(String v) { this.contentHtml = v; return this; }
    public UpdatePolicyVersionBody contentUrl(String v) { this.contentUrl = v; return this; }
    public UpdatePolicyVersionBody summaryOfChanges(String v) { this.summaryOfChanges = v; return this; }
    public UpdatePolicyVersionBody locale(String v) { this.locale = v; return this; }

    /** Clear a nullable field by its wire name ({@code content_url}, {@code summary_of_changes}, {@code locale}): it is sent as {@code null}. */
    public UpdatePolicyVersionBody clear(String key) { this.additionalFields.put(key, null); return this; }

    /** Set any field by its wire name, including one this SDK does not model yet. */
    public UpdatePolicyVersionBody set(String key, Object value) { this.additionalFields.put(key, value); return this; }

    @JsonAnyGetter
    public Map<String, Object> additionalFields() { return additionalFields; }
}
