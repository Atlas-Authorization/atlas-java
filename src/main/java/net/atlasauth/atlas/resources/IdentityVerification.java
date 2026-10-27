package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.IdentityVerificationDecideBody;
import net.atlasauth.atlas.models.IdentityVerificationListResponse;
import net.atlasauth.atlas.models.IdentityVerificationListResponseDataItem;
import net.atlasauth.atlas.models.IdentityVerificationSettingsResponse;
import net.atlasauth.atlas.models.IdentityVerificationStartBody;
import net.atlasauth.atlas.models.IdentityVerificationUpdateSettingsBody;

import java.util.List;
import java.util.Map;

import static net.atlasauth.atlas.http.Transport.enc;

/** The {@code /v1/identity_verifications} resource. */
public final class IdentityVerification {

    private final Transport t;

    public IdentityVerification(Transport t) {
        this.t = t;
    }

    /** {@code GET /v1/identity_verification/settings} - Whether identity verification is on, and which connector does the checking. Scope: {@code identity_verification:read}. */
    public IdentityVerificationSettingsResponse settings() {
        return t.request("GET", "/v1/identity_verification/settings", null, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code PUT /v1/identity_verification/settings} - Turn identity verification on or off and choose the connector (a Persona, Onfido, Veriff or Stripe Identity service connector; its credentials stay in the vault). `as_challenge` offers a verified identity as the custom second step of sign-in (the custom challenge factor must be enabled). Scope: {@code identity_verification:write}. */
    public IdentityVerificationSettingsResponse updateSettings(IdentityVerificationUpdateSettingsBody body) {
        return t.request("PUT", "/v1/identity_verification/settings", null, body, null, false, new TypeReference<>() {
        });
    }

    /** {@code GET /v1/identity_verifications} - List checks by status (default `needs_review`, the review queue), newest first. Scope: {@code identity_verification:read}. */
    public IdentityVerificationListResponse list() {
        return list(null, null, null);
    }

    /** As above, with every optional parameter. */
    public IdentityVerificationListResponse list(String status, Long limit, String startingAfter) {
        Map<String, Object> q = Params.of().set("status", status).set("limit", limit).set("starting_after", startingAfter).map();
        return t.request("GET", "/v1/identity_verifications", q, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code GET /v1/identity_verifications/{id}} - Read one check. A pending check is refreshed from the verification service first. Scope: {@code identity_verification:read}. */
    public IdentityVerificationListResponseDataItem get(String id) {
        return t.request("GET", "/v1/identity_verifications/" + enc(id), null, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/identity_verifications/{id}/decide} - Decide a check that is waiting for review (or override a pending one) as `approved` or `declined`. The outcome is written to the user as `verification.identity`. 409 when the check is already approved or declined. Scope: {@code identity_verification:write}. */
    public IdentityVerificationListResponseDataItem decide(String id, IdentityVerificationDecideBody body) {
        return decide(id, body, null);
    }

    /** As above, with every optional parameter and an {@code Idempotency-Key} so a retry replays the first response. */
    public IdentityVerificationListResponseDataItem decide(String id, IdentityVerificationDecideBody body, String idempotencyKey) {
        return t.request("POST", "/v1/identity_verifications/" + enc(id) + "/decide", null, body, idempotencyKey, false, new TypeReference<>() {
        });
    }

    /** {@code GET /v1/users/{id}/identity_verifications} - A user's identity checks, newest first. Scope: {@code identity_verification:read}. */
    public IdentityVerificationListResponse forUser(String id) {
        return forUser(id, null, null);
    }

    /** As above, with every optional parameter. */
    public IdentityVerificationListResponse forUser(String id, Long limit, String startingAfter) {
        Map<String, Object> q = Params.of().set("limit", limit).set("starting_after", startingAfter).map();
        return t.request("GET", "/v1/users/" + enc(id) + "/identity_verifications", q, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/users/{id}/identity_verifications} - Start an identity check for a user and get the verification service's hosted page (`url`) to send them to. A check started in the last 30 minutes and still pending is returned instead of a new one; at most 5 checks per user per hour. 409 when identity verification is not turned on. Scope: {@code identity_verification:write}. */
    public IdentityVerificationListResponseDataItem start(String id, IdentityVerificationStartBody body) {
        return start(id, body, null);
    }

    /** As above, with every optional parameter and an {@code Idempotency-Key} so a retry replays the first response. */
    public IdentityVerificationListResponseDataItem start(String id, IdentityVerificationStartBody body, String idempotencyKey) {
        return t.request("POST", "/v1/users/" + enc(id) + "/identity_verifications", null, body, idempotencyKey, false, new TypeReference<>() {
        });
    }
}
