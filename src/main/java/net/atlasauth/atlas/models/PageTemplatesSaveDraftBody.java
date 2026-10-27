package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code PUT /v1/instance/page_templates/{page}}. */
public final class PageTemplatesSaveDraftBody {
    public Map<String, Object> definition;

    public PageTemplatesSaveDraftBody() {
    }

    public PageTemplatesSaveDraftBody definition(Map<String, Object> v) { this.definition = v; return this; }
}
