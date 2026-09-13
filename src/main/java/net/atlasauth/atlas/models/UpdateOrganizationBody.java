package net.atlasauth.atlas.models;

import java.util.Map;

/** Body for {@code PATCH /v1/organizations/:id}. */
public final class UpdateOrganizationBody {
    public String name;
    public String slug;
    public String imageUrl;
    public Integer maxAllowedMemberships;
    public Map<String, Object> publicMetadata;
    public Map<String, Object> privateMetadata;

    public UpdateOrganizationBody name(String v) { this.name = v; return this; }
    public UpdateOrganizationBody slug(String v) { this.slug = v; return this; }
    public UpdateOrganizationBody imageUrl(String v) { this.imageUrl = v; return this; }
    public UpdateOrganizationBody maxAllowedMemberships(Integer v) { this.maxAllowedMemberships = v; return this; }
    public UpdateOrganizationBody publicMetadata(Map<String, Object> v) { this.publicMetadata = v; return this; }
    public UpdateOrganizationBody privateMetadata(Map<String, Object> v) { this.privateMetadata = v; return this; }
}
