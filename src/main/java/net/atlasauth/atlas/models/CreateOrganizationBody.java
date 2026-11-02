package net.atlasauth.atlas.models;

import java.util.Map;

/** Body for {@code POST /v1/organizations}. */
public final class CreateOrganizationBody {
    public String name;
    public String slug;
    /** Seated with the creator role; omit for a backend-created organization with no members yet. */
    public String createdBy;
    public Integer maxAllowedMemberships;
    /** Your own id for this customer; unique per instance (409 when taken). */
    public String externalId;
    public Map<String, Object> publicMetadata;
    public Map<String, Object> privateMetadata;
    /** Invites this address with the creator role; the response carries {@code ownerInvitation}. */
    public String ownerEmail;
    /** Role key to your product's name for it, e.g. {@code {"org:admin": "owner"}}. */
    public Map<String, String> roleMappings;

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
    public CreateOrganizationBody externalId(String v) { this.externalId = v; return this; }
    public CreateOrganizationBody publicMetadata(Map<String, Object> v) { this.publicMetadata = v; return this; }
    public CreateOrganizationBody privateMetadata(Map<String, Object> v) { this.privateMetadata = v; return this; }
    public CreateOrganizationBody ownerEmail(String v) { this.ownerEmail = v; return this; }
    public CreateOrganizationBody roleMappings(Map<String, String> v) { this.roleMappings = v; return this; }
}
