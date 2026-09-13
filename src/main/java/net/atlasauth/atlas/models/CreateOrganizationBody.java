package net.atlasauth.atlas.models;

/** Body for {@code POST /v1/organizations}. */
public final class CreateOrganizationBody {
    public String name;
    public String slug;
    public String createdBy;
    public Integer maxAllowedMemberships;

    public CreateOrganizationBody() {
    }

    public CreateOrganizationBody(String name, String slug, String createdBy) {
        this.name = name;
        this.slug = slug;
        this.createdBy = createdBy;
    }

    public CreateOrganizationBody name(String v) { this.name = v; return this; }
    public CreateOrganizationBody slug(String v) { this.slug = v; return this; }
    public CreateOrganizationBody createdBy(String v) { this.createdBy = v; return this; }
    public CreateOrganizationBody maxAllowedMemberships(Integer v) { this.maxAllowedMemberships = v; return this; }
}
