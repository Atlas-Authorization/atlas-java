package net.atlasauth.atlas.models;

import java.util.Map;

/** Body for {@code PUT /v1/users/:id/metadata} — replaces the named bags wholesale. */
public final class ReplaceUserMetadataBody {
    public Map<String, Object> publicMetadata;
    public Map<String, Object> privateMetadata;
    public Map<String, Object> unsafeMetadata;

    public ReplaceUserMetadataBody publicMetadata(Map<String, Object> v) { this.publicMetadata = v; return this; }
    public ReplaceUserMetadataBody privateMetadata(Map<String, Object> v) { this.privateMetadata = v; return this; }
    public ReplaceUserMetadataBody unsafeMetadata(Map<String, Object> v) { this.unsafeMetadata = v; return this; }
}
