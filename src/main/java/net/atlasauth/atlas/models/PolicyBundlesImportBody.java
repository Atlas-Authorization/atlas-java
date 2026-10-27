package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code POST /v1/policy_bundles/import}. Required: bundle, signature. */
public final class PolicyBundlesImportBody {
    /** The bundle (tar.gz), base64-encoded. Required. */
    public String bundle;
    /** The detached compact JWS (`header..signature`) over the archive bytes. Required. */
    public String signature;
    /** Public keys to trust IN ADDITION to this instance's own, as a JWKS - for a bundle signed by another instance (promote staging to production). Private key material is rejected. */
    public Map<String, Object> trustedJwks;
    /** Return the diff and apply nothing. */
    public Boolean dryRun;
    /** Also delete ABAC policies absent from the bundle. */
    public Boolean prune;

    public PolicyBundlesImportBody() {
    }

    public PolicyBundlesImportBody bundle(String v) { this.bundle = v; return this; }
    public PolicyBundlesImportBody signature(String v) { this.signature = v; return this; }
    public PolicyBundlesImportBody trustedJwks(Map<String, Object> v) { this.trustedJwks = v; return this; }
    public PolicyBundlesImportBody dryRun(Boolean v) { this.dryRun = v; return this; }
    public PolicyBundlesImportBody prune(Boolean v) { this.prune = v; return this; }
}
