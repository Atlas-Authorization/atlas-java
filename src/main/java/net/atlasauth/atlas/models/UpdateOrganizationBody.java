package net.atlasauth.atlas.models;

import java.util.Map;

/** Body for {@code PATCH /v1/organizations/:id}. {@link #clear(String)} sends a nullable field as {@code null}. */
public final class UpdateOrganizationBody extends ExtensibleBody {
    public String name;
    public String slug;
    public String imageUrl;
    public Integer maxAllowedMemberships;
    public Map<String, Object> publicMetadata;
    public Map<String, Object> privateMetadata;
    /** Your own id for this customer; unique per instance. */
    public String externalId;
    /** Role key to your product's name for it. */
    public Map<String, String> roleMappings;

    public UpdateOrganizationBody name(String v) { this.name = v; return this; }
    public UpdateOrganizationBody slug(String v) { this.slug = v; return this; }
    public UpdateOrganizationBody imageUrl(String v) { this.imageUrl = v; return this; }
    public UpdateOrganizationBody maxAllowedMemberships(Integer v) { this.maxAllowedMemberships = v; return this; }
    public UpdateOrganizationBody publicMetadata(Map<String, Object> v) { this.publicMetadata = v; return this; }
    public UpdateOrganizationBody privateMetadata(Map<String, Object> v) { this.privateMetadata = v; return this; }
    public UpdateOrganizationBody externalId(String v) { this.externalId = v; additionalFields().remove("external_id"); return this; }
    public UpdateOrganizationBody roleMappings(Map<String, String> v) { this.roleMappings = v; additionalFields().remove("role_mappings"); return this; }

    /** Clear a nullable field by its wire name ({@code external_id}, {@code role_mappings}): it is sent as {@code null}. */
    public UpdateOrganizationBody clear(String key) { putNulls(key); return this; }
}
