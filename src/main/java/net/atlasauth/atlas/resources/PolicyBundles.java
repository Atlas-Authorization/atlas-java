package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.PolicyBundlesCurrentResponse;
import net.atlasauth.atlas.models.PolicyBundlesImportBody;
import net.atlasauth.atlas.models.PolicyBundlesImportResponse;
import net.atlasauth.atlas.models.PolicyBundlesListResponse;
import net.atlasauth.atlas.models.PolicyBundlesListResponseDataItem;
import net.atlasauth.atlas.models.PolicyBundlesVerifyBody;
import net.atlasauth.atlas.models.PolicyBundlesVerifyResponse;

import java.util.List;
import java.util.Map;

import static net.atlasauth.atlas.http.Transport.enc;

/** The {@code /v1/policy_bundles} resource. */
public final class PolicyBundles {

    private final Transport t;

    public PolicyBundles(Transport t) {
        this.t = t;
    }

    /** {@code GET /v1/policy_bundles/current} - The bundle revision the instance policy is at right now. Compare it with a deployed bundle's revision to detect drift. Scope: {@code policy_bundles:read}. */
    public PolicyBundlesCurrentResponse current() {
        return t.request("GET", "/v1/policy_bundles/current", null, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code GET /v1/policy_bundles} - List the signed policy bundles exported or imported on this instance, newest first (without the archives). Scope: {@code policy_bundles:read}. */
    public PolicyBundlesListResponse list() {
        return list(null, null);
    }

    /** As above, with every optional parameter. */
    public PolicyBundlesListResponse list(Long limit, String startingAfter) {
        Map<String, Object> q = Params.of().set("limit", limit).set("starting_after", startingAfter).map();
        return t.request("GET", "/v1/policy_bundles", q, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/policy_bundles} - Export the instance policy - every ABAC policy (current version), the ABAC entity schema, templates and template links, and every FGA store's latest authorization model - as a versioned tar.gz with a manifest of file digests, signed with the instance signing key (detached JWS). The same policy always has the same revision. Scope: {@code policy_bundles:write}. */
    public PolicyBundlesListResponseDataItem export() {
        return export(null);
    }

    /** As above, with every optional parameter and an {@code Idempotency-Key} so a retry replays the first response. */
    public PolicyBundlesListResponseDataItem export(String idempotencyKey) {
        return t.request("POST", "/v1/policy_bundles", null, null, idempotencyKey, false, new TypeReference<>() {
        });
    }

    /** {@code GET /v1/policy_bundles/{revision}} - Read a stored bundle by revision, with the base64 archive and its signature. Scope: {@code policy_bundles:read}. */
    public PolicyBundlesListResponseDataItem get(String revision) {
        return t.request("GET", "/v1/policy_bundles/" + enc(revision), null, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code GET /v1/policy_bundles/{revision}/archive} - Download a stored bundle as the raw tar.gz (`application/gzip`). The detached signature is in the `Atlas-Bundle-Signature` header, with `Atlas-Bundle-Revision` and `Atlas-Bundle-Key-Id`. Scope: {@code policy_bundles:read}. */
    public byte[] archive(String revision) {
        return t.getBytes("/v1/policy_bundles/" + enc(revision) + "/archive", null);
    }

    /** {@code POST /v1/policy_bundles/verify} - Check a bundle without importing it: the detached signature against this instance's signing keys (plus any `trusted_jwks` you supply) and the archive's integrity. The SDK can do the same check offline. Scope: {@code policy_bundles:read}. */
    public PolicyBundlesVerifyResponse verify(PolicyBundlesVerifyBody body) {
        return verify(body, null);
    }

    /** As above, with every optional parameter and an {@code Idempotency-Key} so a retry replays the first response. */
    public PolicyBundlesVerifyResponse verify(PolicyBundlesVerifyBody body, String idempotencyKey) {
        return t.request("POST", "/v1/policy_bundles/verify", null, body, idempotencyKey, false, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/policy_bundles/import} - Import a signed bundle. The signature and the archive integrity must both check out or nothing is touched (422). With `dry_run: true` it returns the diff of what would change and applies nothing (needs only policy_bundles:read). Otherwise it applies everything in one transaction: stores and models first, then the ABAC schema, templates, links and policies; a changed policy appends a version. `prune` also deletes ABAC policies, templates and links the bundle does not name (and the schema when it has none); FGA stores are never deleted. Scope: {@code policy_bundles:write}. */
    public PolicyBundlesImportResponse importValue(PolicyBundlesImportBody body) {
        return importValue(body, null);
    }

    /** As above, with every optional parameter and an {@code Idempotency-Key} so a retry replays the first response. */
    public PolicyBundlesImportResponse importValue(PolicyBundlesImportBody body, String idempotencyKey) {
        return t.request("POST", "/v1/policy_bundles/import", null, body, idempotencyKey, false, new TypeReference<>() {
        });
    }
}
