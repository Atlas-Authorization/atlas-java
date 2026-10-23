package net.atlasauth.atlas.models;

/** Body for {@code POST /v1/scim_tokens}. */
public final class CreateScimTokenBody {
    /** ["read"] makes the token read-only. */
    public java.util.List<String> scopes;
    /** SCIM roles values to organization role keys: [{value, role}]. */
    public java.util.List<java.util.Map<String, String>> roleMappings;
    public String organizationId;
    public String name;
    public String connectionId;

    public CreateScimTokenBody() {
    }

    public CreateScimTokenBody(String organizationId) {
        this.organizationId = organizationId;
    }

    public CreateScimTokenBody organizationId(String v) { this.organizationId = v; return this; }
    public CreateScimTokenBody name(String v) { this.name = v; return this; }
    public CreateScimTokenBody connectionId(String v) { this.connectionId = v; return this; }

    public CreateScimTokenBody scopes(java.util.List<String> v) { this.scopes = v; return this; }
    public CreateScimTokenBody roleMappings(java.util.List<java.util.Map<String, String>> v) { this.roleMappings = v; return this; }
}
