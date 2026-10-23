package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.CreatePolicyBody;
import net.atlasauth.atlas.models.CreatePolicyVersionBody;
import net.atlasauth.atlas.models.DeletedPolicy;
import net.atlasauth.atlas.models.DeletedPolicyVersion;
import net.atlasauth.atlas.models.ListPage;
import net.atlasauth.atlas.models.Policy;
import net.atlasauth.atlas.models.PolicyAcceptancePage;
import net.atlasauth.atlas.models.PolicySettings;
import net.atlasauth.atlas.models.PolicyVersion;
import net.atlasauth.atlas.models.UpdatePolicyBody;
import net.atlasauth.atlas.models.UpdatePolicyVersionBody;
import net.atlasauth.atlas.models.UserPolicies;

import java.util.Map;

import static net.atlasauth.atlas.http.Transport.enc;

/**
 * Policy acceptance ({@code /v1/policies}): the policies (terms of service, privacy policy, any
 * key) your users accept at sign-up and, per policy, again at sign-in when a new version takes
 * effect. A policy's versions go draft, published (with an {@code effective_at}), retired; a
 * published version is immutable and its {@code contentSha256} is the SHA-256 of the sanitised
 * HTML users are shown. Acceptances are consent records carrying that hash and the reading
 * evidence. A policy is addressed by id or by key, a version by id or by label.
 *
 * <p>Content can also come from any external system: see {@link PolicySourceConfig} and
 * {@link net.atlasauth.atlas.policy.PolicySourceProtocol}. The {@code pol} session claim
 * ({@link #updateSettings(boolean)}) is read with {@code SessionClaims.pol()}.
 */
public final class Policies {
    private final Transport t;

    public Policies(Transport t) {
        this.t = t;
    }

    /** {@code GET /v1/policies} — active policies. */
    public ListPage<Policy> list() {
        return list(null);
    }

    /** {@code GET /v1/policies} — {@code includeRetired} adds retired policies. */
    public ListPage<Policy> list(Boolean includeRetired) {
        return t.get("/v1/policies", Params.of().set("include_retired", includeRetired).map(), new TypeReference<>() {
        });
    }

    /** {@code POST /v1/policies}. */
    public Policy create(CreatePolicyBody body) {
        return create(body, null);
    }

    /** {@link #create(CreatePolicyBody)} with an {@code Idempotency-Key}. */
    public Policy create(CreatePolicyBody body, String idempotencyKey) {
        return t.request("POST", "/v1/policies", null, body, idempotencyKey, false, new TypeReference<>() {
        });
    }

    /** {@code GET /v1/policies/:id} — by id or key. */
    public Policy get(String idOrKey) {
        return t.get("/v1/policies/" + enc(idOrKey), new TypeReference<>() {
        });
    }

    /** {@code PATCH /v1/policies/:id}. */
    public Policy update(String idOrKey, UpdatePolicyBody body) {
        return t.patch("/v1/policies/" + enc(idOrKey), body, new TypeReference<>() {
        });
    }

    /** {@code DELETE /v1/policies/:id} — deletes a never-published policy, retires one with history. */
    public DeletedPolicy delete(String idOrKey) {
        return t.delete("/v1/policies/" + enc(idOrKey), new TypeReference<>() {
        });
    }

    /** {@code GET /v1/policies/:id/versions}. */
    public ListPage<PolicyVersion> listVersions(String idOrKey) {
        return t.get(versionsPath(idOrKey), new TypeReference<>() {
        });
    }

    /** {@code POST /v1/policies/:id/versions} — a draft, or published at once with {@code publish}. */
    public PolicyVersion createVersion(String idOrKey, CreatePolicyVersionBody body) {
        return t.post(versionsPath(idOrKey), body, new TypeReference<>() {
        });
    }

    /** {@code GET /v1/policies/:id/versions/:versionId} — by version id or label. */
    public PolicyVersion getVersion(String idOrKey, String versionIdOrLabel) {
        return t.get(versionPath(idOrKey, versionIdOrLabel), new TypeReference<>() {
        });
    }

    /** {@code PATCH /v1/policies/:id/versions/:versionId} — drafts only. */
    public PolicyVersion updateVersion(String idOrKey, String versionIdOrLabel, UpdatePolicyVersionBody body) {
        return t.patch(versionPath(idOrKey, versionIdOrLabel), body, new TypeReference<>() {
        });
    }

    /** {@code DELETE /v1/policies/:id/versions/:versionId} — drafts only. */
    public DeletedPolicyVersion deleteVersion(String idOrKey, String versionIdOrLabel) {
        return t.delete(versionPath(idOrKey, versionIdOrLabel), new TypeReference<>() {
        });
    }

    /** {@link #publishVersion(String, String, Long)} effective now. */
    public PolicyVersion publishVersion(String idOrKey, String versionIdOrLabel) {
        return publishVersion(idOrKey, versionIdOrLabel, (Long) null);
    }

    /**
     * {@code POST …/publish} — {@code effectiveAtMs} (epoch milliseconds; null is now) is when the
     * gate starts asking users for it; it is never earlier than the publish time.
     */
    public PolicyVersion publishVersion(String idOrKey, String versionIdOrLabel, Long effectiveAtMs) {
        return t.post(versionPath(idOrKey, versionIdOrLabel) + "/publish",
                Params.of().set("effective_at", effectiveAtMs).map(), new TypeReference<>() {
                });
    }

    /** {@link #publishVersion(String, String, Long)} with an ISO 8601 {@code effective_at}. */
    public PolicyVersion publishVersion(String idOrKey, String versionIdOrLabel, String effectiveAtIso) {
        return t.post(versionPath(idOrKey, versionIdOrLabel) + "/publish",
                Params.of().set("effective_at", effectiveAtIso).map(), new TypeReference<>() {
                });
    }

    /** {@code POST …/retire}. */
    public PolicyVersion retireVersion(String idOrKey, String versionIdOrLabel) {
        return t.post(versionPath(idOrKey, versionIdOrLabel) + "/retire", null, new TypeReference<>() {
        });
    }

    /** {@code GET /v1/policies/:id/acceptances} — who accepted, newest first. */
    public PolicyAcceptancePage listAcceptances(String idOrKey) {
        return listAcceptances(idOrKey, null, null, null, null);
    }

    /**
     * {@code GET /v1/policies/:id/acceptances}. Each filter may be null: {@code version} (id or
     * label), {@code status} ({@code accepted} (default), {@code withdrawn} or {@code all}),
     * {@code limit}, {@code startingAfter} (the previous page's {@code nextCursor}).
     */
    public PolicyAcceptancePage listAcceptances(String idOrKey, String version, String status, Integer limit, String startingAfter) {
        return t.get("/v1/policies/" + enc(idOrKey) + "/acceptances",
                Params.of().set("version", version).set("status", status).set("limit", limit)
                        .set("starting_after", startingAfter).map(),
                new TypeReference<>() {
                });
    }

    /** The same as CSV ({@code format=csv}, up to 100,000 rows); {@code version} and {@code status} may be null. */
    public String exportAcceptancesCsv(String idOrKey, String version, String status) {
        return t.getRaw("/v1/policies/" + enc(idOrKey) + "/acceptances",
                Params.of().set("version", version).set("status", status).set("format", "csv").map());
    }

    /** {@code GET /v1/users/:id/policies} — a user's standing on every policy. */
    public UserPolicies forUser(String userId) {
        return t.get("/v1/users/" + enc(userId) + "/policies", new TypeReference<>() {
        });
    }

    /** {@code GET /v1/instance/policy_settings}. */
    public PolicySettings getSettings() {
        return t.get("/v1/instance/policy_settings", new TypeReference<>() {
        });
    }

    /** {@code PATCH /v1/instance/policy_settings} — {@code sessionClaim} puts {@code pol} on session tokens. */
    public PolicySettings updateSettings(boolean sessionClaim) {
        return t.patch("/v1/instance/policy_settings", Map.of("session_claim", sessionClaim), new TypeReference<>() {
        });
    }

    private static String versionsPath(String idOrKey) {
        return "/v1/policies/" + enc(idOrKey) + "/versions";
    }

    private static String versionPath(String idOrKey, String versionIdOrLabel) {
        return versionsPath(idOrKey) + "/" + enc(versionIdOrLabel);
    }
}
