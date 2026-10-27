package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code POST /v1/oauth_clients/{id}/trusted_issuers}. Required: issuer. */
public final class OAuthClientsTrustedIssuersCreateBody {
    /** Required. */
    public String issuer;
    /** Inline JWK Set. Provide exactly one of jwks or jwks_uri. */
    public Map<String, Object> jwks;
    /** https URL of the issuer JWK Set. Provide exactly one of jwks or jwks_uri. */
    public String jwksUri;
    public String subjectMapping;
    /** Fills in issuer and jwks_uri for github_actions, gcp and azure when you omit them. */
    public String workloadKind;
    /** Required for workload_kind azure (without an explicit issuer). */
    public String tenantId;
    public String purpose;
    /** Required when purpose is client_auth or both. A glob with * wildcards; a bare * is refused. */
    public String subjectPattern;
    /** Extra claims that must match exactly. */
    public Map<String, Object> requiredClaims;
    /** Accepted aud values. Defaults to the token endpoint, the PAR endpoint and the issuer. */
    public List<String> audiences;

    public OAuthClientsTrustedIssuersCreateBody() {
    }

    public OAuthClientsTrustedIssuersCreateBody issuer(String v) { this.issuer = v; return this; }
    public OAuthClientsTrustedIssuersCreateBody jwks(Map<String, Object> v) { this.jwks = v; return this; }
    public OAuthClientsTrustedIssuersCreateBody jwksUri(String v) { this.jwksUri = v; return this; }
    public OAuthClientsTrustedIssuersCreateBody subjectMapping(String v) { this.subjectMapping = v; return this; }
    public OAuthClientsTrustedIssuersCreateBody workloadKind(String v) { this.workloadKind = v; return this; }
    public OAuthClientsTrustedIssuersCreateBody tenantId(String v) { this.tenantId = v; return this; }
    public OAuthClientsTrustedIssuersCreateBody purpose(String v) { this.purpose = v; return this; }
    public OAuthClientsTrustedIssuersCreateBody subjectPattern(String v) { this.subjectPattern = v; return this; }
    public OAuthClientsTrustedIssuersCreateBody requiredClaims(Map<String, Object> v) { this.requiredClaims = v; return this; }
    public OAuthClientsTrustedIssuersCreateBody audiences(List<String> v) { this.audiences = v; return this; }
}
