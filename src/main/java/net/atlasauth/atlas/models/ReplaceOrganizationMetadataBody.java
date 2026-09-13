package net.atlasauth.atlas.models;

import java.util.Map;

/** Body for {@code PUT /v1/organizations/:id/metadata} — replaces the named bags wholesale. */
public final class ReplaceOrganizationMetadataBody {
    public Map<String, Object> publicMetadata;
    public Map<String, Object> privateMetadata;

    public ReplaceOrganizationMetadataBody publicMetadata(Map<String, Object> v) { this.publicMetadata = v; return this; }
    public ReplaceOrganizationMetadataBody privateMetadata(Map<String, Object> v) { this.privateMetadata = v; return this; }
}
