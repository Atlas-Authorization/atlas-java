package net.atlasauth.atlas.models;

/**
 * Body for {@code POST /v1/jwt_connections} and {@code PATCH /v1/jwt_connections/:id}.
 * {@link #clear(String)} sends a nullable field ({@code organization_id}, {@code default_role_id}) as {@code null}.
 */
public final class JwtConnectionBody extends ExtensibleBody {
    public String organizationId;
    /** draft, active or disabled. */
    public String status;
    /** An https URL serving your public signing keys (RS / PS / ES / EdDSA). */
    public String jwksUri;
    public String issuer;
    public String audience;
    /** Default {@code email}. */
    public String emailClaim;
    /** exp minus iat at most this; 30 to 3600, default 300. */
    public Integer maxAgeSeconds;
    public String defaultRoleId;

    public JwtConnectionBody() {
    }

    public JwtConnectionBody organizationId(String v) { this.organizationId = v; return this; }
    public JwtConnectionBody status(String v) { this.status = v; return this; }
    public JwtConnectionBody jwksUri(String v) { this.jwksUri = v; return this; }
    public JwtConnectionBody issuer(String v) { this.issuer = v; return this; }
    public JwtConnectionBody audience(String v) { this.audience = v; return this; }
    public JwtConnectionBody emailClaim(String v) { this.emailClaim = v; return this; }
    public JwtConnectionBody maxAgeSeconds(Integer v) { this.maxAgeSeconds = v; return this; }
    public JwtConnectionBody defaultRoleId(String v) { this.defaultRoleId = v; return this; }

    /** Clear a nullable field by its wire name: it is sent as {@code null}. */
    public JwtConnectionBody clear(String key) { putNulls(key); return this; }
}
