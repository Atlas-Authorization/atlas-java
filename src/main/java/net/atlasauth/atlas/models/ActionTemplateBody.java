package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Body for creating ({@code slug}, {@code name}, {@code authorName} and {@code trigger}
 * required) or updating a template listing. On create the first version may come in the
 * same call ({@code version}, {@code code}, {@code secrets}, {@code changelog},
 * {@code submit}).
 */
public final class ActionTemplateBody {
    public String slug;
    public String name;
    public String authorName;
    public String summary;
    /** Markdown. */
    public String description;
    public String category;
    public List<String> tags;
    public String trigger;
    /** SPDX id. */
    public String license;
    public String repositoryUrl;
    public String version;
    public String code;
    public List<ActionTemplateSecret> secrets;
    public String changelog;
    public Boolean submit;

    @JsonIgnore
    private final Map<String, Object> additionalFields = new LinkedHashMap<>();

    public ActionTemplateBody() {
    }

    public ActionTemplateBody slug(String v) { this.slug = v; return this; }
    public ActionTemplateBody name(String v) { this.name = v; return this; }
    public ActionTemplateBody authorName(String v) { this.authorName = v; return this; }
    public ActionTemplateBody summary(String v) { this.summary = v; return this; }
    public ActionTemplateBody description(String v) { this.description = v; return this; }
    public ActionTemplateBody category(String v) { this.category = v; return this; }
    public ActionTemplateBody tags(List<String> v) { this.tags = v; return this; }
    public ActionTemplateBody trigger(String v) { this.trigger = v; return this; }
    public ActionTemplateBody license(String v) { this.license = v; return this; }
    public ActionTemplateBody repositoryUrl(String v) { this.repositoryUrl = v; return this; }
    public ActionTemplateBody version(String v) { this.version = v; return this; }
    public ActionTemplateBody code(String v) { this.code = v; return this; }
    public ActionTemplateBody secrets(List<ActionTemplateSecret> v) { this.secrets = v; return this; }
    public ActionTemplateBody changelog(String v) { this.changelog = v; return this; }
    public ActionTemplateBody submit(Boolean v) { this.submit = v; return this; }

    /** Set any field by its wire name, including an explicit {@code null} (clears it). */
    public ActionTemplateBody set(String key, Object value) { this.additionalFields.put(key, value); return this; }

    @JsonAnyGetter
    public Map<String, Object> additionalFields() { return additionalFields; }
}
