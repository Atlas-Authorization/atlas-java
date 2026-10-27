package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code PUT /v1/migrations/{source}/bridge}. */
public final class MigrationsSetBridgeBody {
    /** Whether the exchange accepts old sessions. Default false. */
    public Boolean enabled;
    /** End of the migration window (ISO-8601 or Unix ms); null = open until disabled. */
    public Object endsAt;
    /** jwt: the old session is a vendor JWT. session_table: an opaque token matched against imported sessions. remote: the vendor is asked about the token with the preset's call (vendors whose sessions only they can check; needs a preset with a remote check). Default: the preset's. */
    public String mode;
    /** A bridge preset (GET /v1/migrations/sources lists them): fills issuer, keys URL, audience, algorithms, claims and cookie names from preset_vars. */
    public String preset;
    /** The preset's variables, e.g. &#123;"domain": "acme.us.auth0.com"&#125; or &#123;"region": "us-east-1", "user_pool_id": "...", "client_id": "..."&#125;. */
    public Map<String, Object> presetVars;
    /** Override: the `iss` the old tokens carry. */
    public String issuer;
    /** Override: where the old signing keys are (public https on production). */
    public String jwksUrl;
    /** Override: accepted `aud` values; null = unchecked. */
    public List<String> audience;
    public List<String> algorithms;
    public Map<String, Object> claimMap;
    /** Accept a validly signed token this long past exp (still single use). For vendors whose cookie token is refreshed by their client SDK only. */
    public Long expiredGraceSeconds;
    /** Where the SDK middleware finds the token (* wildcards). Default: the preset's. */
    public List<String> cookieNames;
    /** Create an Atlas user for a valid token whose subject was never imported (needs a verified email claim). Default false. */
    public Boolean createUsers;
    /** Write-only HS256 secret, for vendors that sign with one (Supabase legacy JWT secret). Omit to keep, null to clear. */
    public String sharedSecret;

    public MigrationsSetBridgeBody() {
    }

    public MigrationsSetBridgeBody enabled(Boolean v) { this.enabled = v; return this; }
    public MigrationsSetBridgeBody endsAt(Object v) { this.endsAt = v; return this; }
    public MigrationsSetBridgeBody mode(String v) { this.mode = v; return this; }
    public MigrationsSetBridgeBody preset(String v) { this.preset = v; return this; }
    public MigrationsSetBridgeBody presetVars(Map<String, Object> v) { this.presetVars = v; return this; }
    public MigrationsSetBridgeBody issuer(String v) { this.issuer = v; return this; }
    public MigrationsSetBridgeBody jwksUrl(String v) { this.jwksUrl = v; return this; }
    public MigrationsSetBridgeBody audience(List<String> v) { this.audience = v; return this; }
    public MigrationsSetBridgeBody algorithms(List<String> v) { this.algorithms = v; return this; }
    public MigrationsSetBridgeBody claimMap(Map<String, Object> v) { this.claimMap = v; return this; }
    public MigrationsSetBridgeBody expiredGraceSeconds(Long v) { this.expiredGraceSeconds = v; return this; }
    public MigrationsSetBridgeBody cookieNames(List<String> v) { this.cookieNames = v; return this; }
    public MigrationsSetBridgeBody createUsers(Boolean v) { this.createUsers = v; return this; }
    public MigrationsSetBridgeBody sharedSecret(String v) { this.sharedSecret = v; return this; }
}
