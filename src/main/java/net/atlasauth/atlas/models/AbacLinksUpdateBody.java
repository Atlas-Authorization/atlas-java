package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code PATCH /v1/abac_policy_links/{id}}. */
public final class AbacLinksUpdateBody {
    public String templateId;
    /** Shown as the deciding policy in decisions. */
    public String name;
    /** An entity such as `&#123; "type": "User", "id": "alice" &#125;`. */
    public Map<String, Object> principal;
    /** An entity such as `&#123; "type": "User", "id": "alice" &#125;`. */
    public Map<String, Object> resource;
    public Boolean enabled;

    public AbacLinksUpdateBody() {
    }

    public AbacLinksUpdateBody templateId(String v) { this.templateId = v; return this; }
    public AbacLinksUpdateBody name(String v) { this.name = v; return this; }
    public AbacLinksUpdateBody principal(Map<String, Object> v) { this.principal = v; return this; }
    public AbacLinksUpdateBody resource(Map<String, Object> v) { this.resource = v; return this; }
    public AbacLinksUpdateBody enabled(Boolean v) { this.enabled = v; return this; }
}
