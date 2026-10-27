package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code POST /v1/groups}. Required: slug, name. */
public final class GroupsCreateBody {
    /** Lowercase letters, digits, - and _ (max 63). Unique per instance; usable in place of the id in paths. Required. */
    public String slug;
    /** Required. */
    public String name;
    public String description;
    /** Nest under another group (up to 8 levels, no cycles). Members of a child are also members of its ancestors for the token claim and role mapping. */
    public String parentId;
    /** Free attributes (max 8 KB). */
    public Map<String, Object> attributes;
    /** App roles every member (and member of a descendant) holds, merged into app_roles / app_permissions on the token. */
    public List<String> appRoleKeys;
    /** Add the slug to the `groups` token claim (default true). */
    public Boolean includeInToken;
    /** A user-search filter tree. POST .../reapply (and each SSO sign-in) makes membership match it. */
    public Map<String, Object> dynamicFilter;
    /** &#123; claim, equals | contains | matches &#125;: join at SSO sign-in when the IdP claim (a string or list, e.g. "groups") matches; leave when it stops matching. */
    public Map<String, Object> ssoMatch;

    public GroupsCreateBody() {
    }

    public GroupsCreateBody slug(String v) { this.slug = v; return this; }
    public GroupsCreateBody name(String v) { this.name = v; return this; }
    public GroupsCreateBody description(String v) { this.description = v; return this; }
    public GroupsCreateBody parentId(String v) { this.parentId = v; return this; }
    public GroupsCreateBody attributes(Map<String, Object> v) { this.attributes = v; return this; }
    public GroupsCreateBody appRoleKeys(List<String> v) { this.appRoleKeys = v; return this; }
    public GroupsCreateBody includeInToken(Boolean v) { this.includeInToken = v; return this; }
    public GroupsCreateBody dynamicFilter(Map<String, Object> v) { this.dynamicFilter = v; return this; }
    public GroupsCreateBody ssoMatch(Map<String, Object> v) { this.ssoMatch = v; return this; }
}
