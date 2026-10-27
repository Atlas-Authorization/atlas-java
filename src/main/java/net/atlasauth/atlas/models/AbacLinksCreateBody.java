package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code POST /v1/abac_policy_links}. Required: templateId, name. */
public final class AbacLinksCreateBody {
    /** Required. */
    public String templateId;
    /** Shown as the deciding policy in decisions. Required. */
    public String name;
    /** An entity such as `&#123; "type": "User", "id": "alice" &#125;`. */
    public Map<String, Object> principal;
    /** An entity such as `&#123; "type": "User", "id": "alice" &#125;`. */
    public Map<String, Object> resource;
    public Boolean enabled;

    public AbacLinksCreateBody() {
    }

    public AbacLinksCreateBody templateId(String v) { this.templateId = v; return this; }
    public AbacLinksCreateBody name(String v) { this.name = v; return this; }
    public AbacLinksCreateBody principal(Map<String, Object> v) { this.principal = v; return this; }
    public AbacLinksCreateBody resource(Map<String, Object> v) { this.resource = v; return this; }
    public AbacLinksCreateBody enabled(Boolean v) { this.enabled = v; return this; }
}
