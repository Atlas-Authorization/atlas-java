package net.atlasauth.atlas.models;

/** Body for {@code POST /v1/scim_tokens}. */
public final class CreateScimTokenBody {
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
}
