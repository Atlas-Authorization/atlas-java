package net.atlasauth.atlas.resources;

import net.atlasauth.atlas.models.UserSearchParams;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.ApprovalRequest;
import net.atlasauth.atlas.models.ApprovalRequestCreated;
import net.atlasauth.atlas.models.Consent;
import net.atlasauth.atlas.models.CreateApprovalRequestBody;
import net.atlasauth.atlas.models.CreateUserBody;
import net.atlasauth.atlas.models.CursorPage;
import net.atlasauth.atlas.models.DataList;
import net.atlasauth.atlas.models.DeletedObject;
import net.atlasauth.atlas.models.Directive;
import net.atlasauth.atlas.models.DirectiveLifted;
import net.atlasauth.atlas.models.EmailAddress;
import net.atlasauth.atlas.models.ErasureAckBody;
import net.atlasauth.atlas.models.FindUsersByEmailsBody;
import net.atlasauth.atlas.models.Grant;
import net.atlasauth.atlas.models.Identity;
import net.atlasauth.atlas.models.IdentityRemoved;
import net.atlasauth.atlas.models.ImportIdentityBody;
import net.atlasauth.atlas.models.ListPage;
import net.atlasauth.atlas.models.NotificationPreferences;
import net.atlasauth.atlas.models.OAuthAccessToken;
import net.atlasauth.atlas.models.PhoneNumber;
import net.atlasauth.atlas.models.ProgressionEvent;
import net.atlasauth.atlas.models.ProgressionEventBody;
import net.atlasauth.atlas.models.RecordConsentBody;
import net.atlasauth.atlas.models.ReplaceUserMetadataBody;
import net.atlasauth.atlas.models.ResolvedUser;
import net.atlasauth.atlas.models.SetUserDirectiveBody;
import net.atlasauth.atlas.models.SetUserPasswordBody;
import net.atlasauth.atlas.models.SignInHistoryEntry;
import net.atlasauth.atlas.models.UpdateNotificationPreferencesBody;
import net.atlasauth.atlas.models.UpdateUserBody;
import net.atlasauth.atlas.models.User;
import net.atlasauth.atlas.models.UserEmailMatch;
import net.atlasauth.atlas.models.UserErasureAck;
import net.atlasauth.atlas.models.UserErasureExport;
import net.atlasauth.atlas.models.UserOrganizationMembership;
import net.atlasauth.atlas.models.UserReferralFlag;
import net.atlasauth.atlas.models.UserSession;
import net.atlasauth.atlas.models.UserState;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.atlasauth.atlas.models.UsersCreateLinkBody;
import net.atlasauth.atlas.models.UsersCreateLinkResponse;
import net.atlasauth.atlas.models.UsersDeleteDeviceKeyResponse;
import net.atlasauth.atlas.models.UsersHistoryResponse;
import net.atlasauth.atlas.models.UsersListDeviceKeysResponse;
import net.atlasauth.atlas.models.UsersMinorStatusResponse;

import static net.atlasauth.atlas.http.Transport.enc;

/** The {@code /v1/users} resource. */
public final class Users {

    private final Transport t;

    public Users(Transport t) {
        this.t = t;
    }

    /** {@code GET /v1/users} — cursor-paginated. */
    public CursorPage<User> list() {
        return list(null, null);
    }

    public CursorPage<User> list(Integer limit, String startingAfter) {
        Map<String, Object> q = Params.of().set("limit", limit).set("starting_after", startingAfter).map();
        return t.get("/v1/users", q, new TypeReference<>() {
        });
    }

    public User get(String id) {
        return t.get("/v1/users/" + enc(id), new TypeReference<>() {
        });
    }

    public User create(CreateUserBody body) {
        return create(body, null);
    }

    public User create(CreateUserBody body, String idempotencyKey) {
        return t.post("/v1/users", body, idempotencyKey, new TypeReference<>() {
        });
    }

    public User update(String id, UpdateUserBody body) {
        return t.patch("/v1/users/" + enc(id), body, new TypeReference<>() {
        });
    }

    /**
     * {@code PATCH /v1/users/:id} — set (or clear with {@code null}) the user's external id. With
     * referral attribution on, Atlas forwards the change to the referral platform as an alias (old
     * id, or the Atlas user id, to the new one).
     */
    public User setExternalId(String id, String externalId) {
        UpdateUserBody body = new UpdateUserBody();
        if (externalId == null) {
            body.clearExternalId();
        } else {
            body.externalId(externalId);
        }
        return update(id, body);
    }

    /** {@code PUT /v1/users/:id/metadata} — replaces the named bags wholesale. */
    public User replaceMetadata(String id, ReplaceUserMetadataBody body) {
        return t.put("/v1/users/" + enc(id) + "/metadata", body, new TypeReference<>() {
        });
    }

    public User ban(String id) {
        return t.post("/v1/users/" + enc(id) + "/ban", null, new TypeReference<>() {
        });
    }

    /**
     * {@code POST /v1/users/:id/ban} with a reason, note or expiry. A non-null {@code expiresInSeconds}
     * or {@code expiresAt} (epoch ms) makes a timed ban that lifts itself.
     */
    public User ban(String id, String reason, String note, Long expiresInSeconds, Long expiresAt) {
        Map<String, Object> body = Params.of().set("reason", reason).set("note", note)
                .set("expires_in_seconds", expiresInSeconds).set("expires_at", expiresAt).map();
        return t.post("/v1/users/" + enc(id) + "/ban", body.isEmpty() ? null : body, new TypeReference<>() {
        });
    }

    /** {@code GET /v1/users} with search filters and sort ({@link UserSearchParams}). */
    public CursorPage<User> list(UserSearchParams params) {
        return t.get("/v1/users", params.toQuery(), new TypeReference<>() {
        });
    }

    public User unban(String id) {
        return t.post("/v1/users/" + enc(id) + "/unban", null, new TypeReference<>() {
        });
    }

    public User lock(String id) {
        return lock(id, null);
    }

    public User lock(String id, Integer durationInSeconds) {
        Map<String, Object> body = Params.of().set("duration_in_seconds", durationInSeconds).map();
        return t.post("/v1/users/" + enc(id) + "/lock", body, new TypeReference<>() {
        });
    }

    public User unlock(String id) {
        return t.post("/v1/users/" + enc(id) + "/unlock", null, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/users/:id/referral/flag} without a reason. */
    public UserReferralFlag flagReferral(String id) {
        return flagReferral(id, null, null);
    }

    /**
     * {@code POST /v1/users/:id/referral/flag} — mark the user's referral fraudulent. The
     * referral reads {@code flagged}, the referral platform is sent a {@code fraud_confirmed}
     * signal (when referrals are on) and the action is audited; the account is not changed.
     * 404 when the user has no referral. {@code reason} (at most 500 characters, or null) is
     * sent to the platform.
     */
    public UserReferralFlag flagReferral(String id, String reason, String idempotencyKey) {
        Map<String, Object> body = new LinkedHashMap<>();
        if (reason != null) {
            body.put("reason", reason);
        }
        return t.post("/v1/users/" + enc(id) + "/referral/flag", body, idempotencyKey, new TypeReference<>() {
        });
    }

    public DeletedObject delete(String id) {
        return t.delete("/v1/users/" + enc(id), new TypeReference<>() {
        });
    }

    public JsonNode resetMfa(String id) {
        return t.post("/v1/users/" + enc(id) + "/reset_mfa", null, new TypeReference<>() {
        });
    }

    public JsonNode deleteMfaFactor(String id, String factorId) {
        return t.delete("/v1/users/" + enc(id) + "/mfa/" + enc(factorId), new TypeReference<>() {
        });
    }

    public ListPage<UserSession> listSessions(String id) {
        return t.get("/v1/users/" + enc(id) + "/sessions", new TypeReference<>() {
        });
    }

    public JsonNode revokeSessions(String id) {
        return t.post("/v1/users/" + enc(id) + "/sessions/revoke", null, new TypeReference<>() {
        });
    }

    public EmailAddress addEmail(String id, String emailAddress) {
        Map<String, Object> body = Params.of("email_address", emailAddress).map();
        return t.post("/v1/users/" + enc(id) + "/email_addresses", body, new TypeReference<>() {
        });
    }

    public EmailAddress verifyEmail(String id, String emailId) {
        return t.post("/v1/users/" + enc(id) + "/email_addresses/" + enc(emailId) + "/verify", null,
                new TypeReference<>() {
                });
    }

    public EmailAddress setPrimaryEmail(String id, String emailId) {
        return t.post("/v1/users/" + enc(id) + "/email_addresses/" + enc(emailId) + "/primary", null,
                new TypeReference<>() {
                });
    }

    /** {@code GET /v1/users/:id/oauth_access_tokens/:provider} — a live provider credential. */
    public OAuthAccessToken getOAuthAccessToken(String id, String provider) {
        return t.get("/v1/users/" + enc(id) + "/oauth_access_tokens/" + enc(provider), new TypeReference<>() {
        });
    }

    /** {@code GET /v1/users/:id/identities} — base Atlas identity plus linked providers. */
    public ListPage<Identity> listIdentities(String id) {
        return t.get("/v1/users/" + enc(id) + "/identities", new TypeReference<>() {
        });
    }

    /** {@code POST /v1/users/:id/identities} — merge a secondary user INTO this one. */
    public JsonNode linkIdentity(String id, String secondaryUserId, String idempotencyKey) {
        Map<String, Object> body = Params.of("secondary_user_id", secondaryUserId).map();
        return t.post("/v1/users/" + enc(id) + "/identities", body, idempotencyKey, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/users/:id/external_accounts/connect} — backend-initiated provider link. */
    public JsonNode connectExternalAccount(String id, String provider, String redirectUrl,
                                           List<String> additionalScopes, String idempotencyKey) {
        Map<String, Object> body = Params.of()
                .set("provider", provider)
                .set("redirect_url", redirectUrl)
                .set("additional_scopes", additionalScopes)
                .map();
        return t.post("/v1/users/" + enc(id) + "/external_accounts/connect", body, idempotencyKey,
                new TypeReference<>() {
                });
    }

    /** {@code DELETE /v1/users/:id/identities/:identityId} — extract a linked identity into a new user. */
    public JsonNode unlinkIdentity(String id, String identityId) {
        return t.delete("/v1/users/" + enc(id) + "/identities/" + enc(identityId), new TypeReference<>() {
        });
    }

    /** {@code GET /v1/users/:id/grants} — the OAuth clients this user has authorized. */
    public ListPage<Grant> listGrants(String id) {
        return t.get("/v1/users/" + enc(id) + "/grants", new TypeReference<>() {
        });
    }

    /** {@code DELETE /v1/users/:id/grants} — revoke every consent grant the user holds. */
    public JsonNode revokeAllGrants(String id) {
        return t.delete("/v1/users/" + enc(id) + "/grants", new TypeReference<>() {
        });
    }

    /** {@code DELETE /v1/grants/:id} — revoke ONE consent grant (instance-scoped). */
    public JsonNode revokeGrant(String grantId) {
        return t.delete("/v1/grants/" + enc(grantId), new TypeReference<>() {
        });
    }

    /** {@code POST /v1/users/{id}/password} — Set (reset) a user's password from the backend. */
    public User setPassword(String id, SetUserPasswordBody body) {
        return t.post("/v1/users/" + enc(id) + "/password", body, new TypeReference<>() {
        });
    }

    /** As above, with an {@code Idempotency-Key} so a retry replays the first response. */
    public User setPassword(String id, SetUserPasswordBody body, String idempotencyKey) {
        return t.post("/v1/users/" + enc(id) + "/password", body, idempotencyKey, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/users/{id}/directives} — Impose a cross-property directive on a user (e.g. */
    public Directive setDirective(String id, SetUserDirectiveBody body) {
        return t.post("/v1/users/" + enc(id) + "/directives", body, new TypeReference<>() {
        });
    }

    /** As above, with an {@code Idempotency-Key} so a retry replays the first response. */
    public Directive setDirective(String id, SetUserDirectiveBody body, String idempotencyKey) {
        return t.post("/v1/users/" + enc(id) + "/directives", body, idempotencyKey, new TypeReference<>() {
        });
    }

    /** {@code DELETE /v1/users/{id}/directives} — Lift a previously imposed directive from a user. */
    public DirectiveLifted liftDirective(String id) {
        return t.delete("/v1/users/" + enc(id) + "/directives", new TypeReference<>() {
        });
    }

    /** {@code DELETE /v1/users/{id}/passkeys/{passkeyId}} — Remove one of a user’s passkeys (Clerk deleteUserPasskey). */
    public DeletedObject deletePasskey(String id, String passkeyId) {
        return t.delete("/v1/users/" + enc(id) + "/passkeys/" + enc(passkeyId), new TypeReference<>() {
        });
    }

    /** {@code DELETE /v1/users/{id}/profile_image} — Clear a user’s profile image (Clerk deleteUserProfileImage). */
    public User deleteProfileImage(String id) {
        return t.delete("/v1/users/" + enc(id) + "/profile_image", new TypeReference<>() {
        });
    }

    /** {@code POST /v1/users/{id}/phone_numbers} — Add a phone number to a user. */
    public PhoneNumber addPhoneNumber(String id, String phoneNumber) {
        Map<String, Object> payload = Params.of().set("phone_number", phoneNumber).map();
        return t.post("/v1/users/" + enc(id) + "/phone_numbers", payload, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/users/{id}/phone_numbers/{pid}/verify} — Mark a phone number verified on the backend authority. */
    public PhoneNumber verifyPhoneNumber(String id, String pid) {
        return t.post("/v1/users/" + enc(id) + "/phone_numbers/" + enc(pid) + "/verify", null, new TypeReference<>() {
        });
    }

    /** {@code DELETE /v1/users/{id}/phone_numbers/{pid}} — Remove a phone number from a user. */
    public DeletedObject deletePhoneNumber(String id, String pid) {
        return t.delete("/v1/users/" + enc(id) + "/phone_numbers/" + enc(pid), new TypeReference<>() {
        });
    }

    /** {@code POST /v1/users/{id}/progression/events} — Append an idempotent progression event (XP / level / state) for a user, scoped to the calling source. */
    public ProgressionEvent recordProgressionEvent(String id, ProgressionEventBody body) {
        return t.post("/v1/users/" + enc(id) + "/progression/events", body, new TypeReference<>() {
        });
    }

    /** As above, with an {@code Idempotency-Key} so a retry replays the first response. */
    public ProgressionEvent recordProgressionEvent(String id, ProgressionEventBody body, String idempotencyKey) {
        return t.post("/v1/users/" + enc(id) + "/progression/events", body, idempotencyKey, new TypeReference<>() {
        });
    }

    /** {@code GET /v1/users/{id}/state} — Read a user's portable state — progression, tier, flair, entitlements, directives and roles. */
    public UserState getState(String id) {
        return t.get("/v1/users/" + enc(id) + "/state", new TypeReference<>() {
        });
    }

    /** {@code POST /v1/users/{id}/consents} — Record a versioned consent grant (or withdrawal, `status: "withdrawn"`) for a user. */
    public Consent recordConsent(String id, RecordConsentBody body) {
        return t.post("/v1/users/" + enc(id) + "/consents", body, new TypeReference<>() {
        });
    }

    /** As above, with an {@code Idempotency-Key} so a retry replays the first response. */
    public Consent recordConsent(String id, RecordConsentBody body, String idempotencyKey) {
        return t.post("/v1/users/" + enc(id) + "/consents", body, idempotencyKey, new TypeReference<>() {
        });
    }

    /** {@code GET /v1/users/{id}/consents} — List a user's recorded consents with their versions and timestamps. */
    public DataList<Consent> listConsents(String id) {
        return t.get("/v1/users/" + enc(id) + "/consents", new TypeReference<>() {
        });
    }

    public CursorPage<UserOrganizationMembership> listOrganizationMemberships(String id) {
        return listOrganizationMemberships(id, null, null);
    }

    /** {@code GET /v1/users/{id}/organization_memberships} — List the organizations a user belongs to with each membership's role, the org's resolved plan, and the join time. */
    public CursorPage<UserOrganizationMembership> listOrganizationMemberships(String id, Long limit, String startingAfter) {
        Map<String, Object> q = Params.of().set("limit", limit).set("starting_after", startingAfter).map();
        return t.get("/v1/users/" + enc(id) + "/organization_memberships", q, new TypeReference<>() {
        });
    }

    public User scheduleDeletion(String id) {
        return scheduleDeletion(id, null);
    }

    /** {@code POST /v1/users/{id}/deletion} — Schedule a GRACEFUL deletion of a user: opens a cancellation grace window (grace_seconds, default the instance erasure grace of 7 days) and emits user.deletion_scheduled. */
    public User scheduleDeletion(String id, Double graceSeconds) {
        Map<String, Object> payload = Params.of().set("grace_seconds", graceSeconds).map();
        return t.post("/v1/users/" + enc(id) + "/deletion", payload, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/users/{id}/deletion/cancel} — Cancel a scheduled deletion inside its grace window, aborting the purge so the account is NOT erased; emits user.deletion_canceled. */
    public User cancelDeletion(String id) {
        return t.post("/v1/users/" + enc(id) + "/deletion/cancel", null, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/users/{id}/deletion/finalize} — Finalize a scheduled deletion whose grace window has lapsed — the idempotent purge trigger the worker calls on schedule (a cron/operator may too). */
    public DeletedObject finalizeDeletion(String id) {
        return t.post("/v1/users/" + enc(id) + "/deletion/finalize", null, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/users/{id}/erasure_acks} — An integrating app acknowledges it has purged its copy of the user data during a scheduled deletion, identified by source (or the app_id alias), and may contribute a JSON payload Atlas folds into the DSAR erasure export. */
    public UserErasureAck acknowledgeErasure(String id, ErasureAckBody body) {
        return t.post("/v1/users/" + enc(id) + "/erasure_acks", body, new TypeReference<>() {
        });
    }

    /** As above, with an {@code Idempotency-Key} so a retry replays the first response. */
    public UserErasureAck acknowledgeErasure(String id, ErasureAckBody body, String idempotencyKey) {
        return t.post("/v1/users/" + enc(id) + "/erasure_acks", body, idempotencyKey, new TypeReference<>() {
        });
    }

    /** {@code GET /v1/users/{id}/erasure_export} — The DSAR erasure export: Atlas's own data about the subject (the same no-secret bundle the self-service export produces) PLUS every app's contributed blob from its erasure ack, aggregated into one package. */
    public UserErasureExport getErasureExport(String id) {
        return t.get("/v1/users/" + enc(id) + "/erasure_export", new TypeReference<>() {
        });
    }

    /** {@code POST /v1/users/{id}/approval_requests} — Request a transaction approval from a user: push a number-matching "approve this action" prompt (carrying a human-readable description and optional context) to the user's registered push devices. */
    public ApprovalRequestCreated createApprovalRequest(String id, CreateApprovalRequestBody body) {
        return t.post("/v1/users/" + enc(id) + "/approval_requests", body, new TypeReference<>() {
        });
    }

    /** As above, with an {@code Idempotency-Key} so a retry replays the first response. */
    public ApprovalRequestCreated createApprovalRequest(String id, CreateApprovalRequestBody body, String idempotencyKey) {
        return t.post("/v1/users/" + enc(id) + "/approval_requests", body, idempotencyKey, new TypeReference<>() {
        });
    }

    /** {@code GET /v1/users/{id}/approval_requests/{approvalId}} — Poll a transaction-approval request: pending | approved | denied | expired, with the approving device and resolution timestamp. */
    public ApprovalRequest getApprovalRequest(String id, String approvalId) {
        return t.get("/v1/users/" + enc(id) + "/approval_requests/" + enc(approvalId), new TypeReference<>() {
        });
    }

    /** {@code POST /v1/users/by_emails} — Resolve a batch of email addresses to user ids in one call. */
    public DataList<UserEmailMatch> findByEmails(FindUsersByEmailsBody body) {
        return t.post("/v1/users/by_emails", body, new TypeReference<>() {
        });
    }

    /** As above, with an {@code Idempotency-Key} so a retry replays the first response. */
    public DataList<UserEmailMatch> findByEmails(FindUsersByEmailsBody body, String idempotencyKey) {
        return t.post("/v1/users/by_emails", body, idempotencyKey, new TypeReference<>() {
        });
    }

    /** {@code GET /v1/users/{id}/resolved} — Resolve a user to a FLAT identity in one call: the real primary email (never the @no-email.invalid placeholder), email_verified, name, image_url, and the linked provider identities (provider + provider_user_id). */
    public ResolvedUser resolve(String id) {
        return t.get("/v1/users/" + enc(id) + "/resolved", new TypeReference<>() {
        });
    }

    /** {@code POST /v1/users/{id}/identities/import} — Import a KNOWN provider identity (migration) without an interactive OAuth flow — link { provider, provider_user_id, email_address?, verified? */
    public Identity importIdentity(String id, ImportIdentityBody body) {
        return t.post("/v1/users/" + enc(id) + "/identities/import", body, new TypeReference<>() {
        });
    }

    /** As above, with an {@code Idempotency-Key} so a retry replays the first response. */
    public Identity importIdentity(String id, ImportIdentityBody body, String idempotencyKey) {
        return t.post("/v1/users/" + enc(id) + "/identities/import", body, idempotencyKey, new TypeReference<>() {
        });
    }

    /** {@code DELETE /v1/users/{id}/identities/by-provider/{provider}} — Hard-remove a provider identity from a user (undo a mis-import). */
    public IdentityRemoved removeIdentityByProvider(String id, String provider) {
        return t.delete("/v1/users/" + enc(id) + "/identities/by-provider/" + enc(provider), new TypeReference<>() {
        });
    }

    /** {@code GET /v1/users/{id}/notification_preferences} — Read an end-user's security-notification preferences: every category with whether it is enabled and whether it is optional (security-critical categories are non-optional and always enabled). */
    public NotificationPreferences getNotificationPreferences(String id) {
        return t.get("/v1/users/" + enc(id) + "/notification_preferences", new TypeReference<>() {
        });
    }

    /** {@code PATCH /v1/users/{id}/notification_preferences} — Change an end-user's security-notification preferences with a { preferences: { category: enabled } } patch. */
    public NotificationPreferences updateNotificationPreferences(String id, UpdateNotificationPreferencesBody body) {
        return t.patch("/v1/users/" + enc(id) + "/notification_preferences", body, new TypeReference<>() {
        });
    }

    public CursorPage<SignInHistoryEntry> listSignInHistory(String id) {
        return listSignInHistory(id, null, null);
    }

    /** {@code GET /v1/users/{id}/sign_in_history} — A user's successful sign-ins, newest first, keyset-paginated. */
    public CursorPage<SignInHistoryEntry> listSignInHistory(String id, Long limit, String startingAfter) {
        Map<String, Object> q = Params.of().set("limit", limit).set("starting_after", startingAfter).map();
        return t.get("/v1/users/" + enc(id) + "/sign_in_history", q, new TypeReference<>() {
        });
    }

    /** {@code GET /v1/users/{id}/history} - The user change history, newest first: every profile, metadata and status change (ban, lock, directives and their reversal) with who made it, the reason and expiry, and each changed field with its value before and after. A password change and private_metadata values are recorded as redacted (the field name only). Pages with starting_after. Scope: {@code users:read}. */
    public UsersHistoryResponse history(String id) {
        return history(id, null, null, null);
    }

    /** As above, with every optional parameter. */
    public UsersHistoryResponse history(String id, Long limit, String startingAfter, String action) {
        Map<String, Object> q = Params.of().set("limit", limit).set("starting_after", startingAfter).set("action", action).map();
        return t.request("GET", "/v1/users/" + enc(id) + "/history", q, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code GET /v1/users/{id}/minor_status} - A user's parental-consent state (not_required for anyone at or above the age of digital consent) and their guardian links, newest first. Scope: {@code users:read}. */
    public UsersMinorStatusResponse minorStatus(String id) {
        return t.request("GET", "/v1/users/" + enc(id) + "/minor_status", null, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/users/{id}/minor_status/assess} - Re-assess a user from their date-of-birth trait now (for users created before age gating was turned on). 400 when compliance.minors is off. Scope: {@code users:write}. */
    public UsersMinorStatusResponse assessMinor(String id) {
        return assessMinor(id, null);
    }

    /** As above, with every optional parameter and an {@code Idempotency-Key} so a retry replays the first response. */
    public UsersMinorStatusResponse assessMinor(String id, String idempotencyKey) {
        return t.request("POST", "/v1/users/" + enc(id) + "/minor_status/assess", null, null, idempotencyKey, false, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/users/{id}/links} - Mint an email-verification or password-reset link (and code) for a user WITHOUT sending it, for support tools and invite flows that deliver it themselves. type "email_verification" needs an unverified address (default: the primary); the url opens GET /v1/client/email_addresses/verify and the code works wherever the address is verified by code. type "password_reset" needs a verified address and returns the url and/or code the instance's password-reset setting would have mailed, plus the reset attempt_id; redeeming it is the ordinary reset (it still asks for the second factor and sets a new password). Single-use, expires_in_seconds 60 to 86400 (default 3600). Audited as user.link_generated, never with the secret. Treat the response as a credential. -&gt; 201. Scope: {@code users:write}. */
    public UsersCreateLinkResponse createLink(String id, UsersCreateLinkBody body) {
        return createLink(id, body, null);
    }

    /** As above, with every optional parameter and an {@code Idempotency-Key} so a retry replays the first response. */
    public UsersCreateLinkResponse createLink(String id, UsersCreateLinkBody body, String idempotencyKey) {
        return t.request("POST", "/v1/users/" + enc(id) + "/links", null, body, idempotencyKey, false, new TypeReference<>() {
        });
    }

    /** {@code GET /v1/users/{id}/device_keys} - List a user's device keys (native biometric sign-in). Never returns key material. Scope: {@code users:read}. */
    public UsersListDeviceKeysResponse listDeviceKeys(String id) {
        return listDeviceKeys(id, null, null);
    }

    /** As above, with every optional parameter. */
    public UsersListDeviceKeysResponse listDeviceKeys(String id, Long limit, String startingAfter) {
        Map<String, Object> q = Params.of().set("limit", limit).set("starting_after", startingAfter).map();
        return t.request("GET", "/v1/users/" + enc(id) + "/device_keys", q, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code DELETE /v1/users/{id}/device_keys/{keyId}} - Revoke one of a user's device keys so that device can no longer sign in. Scoped to the user; the account holder is notified when account-change notices are on. Scope: {@code users:write}. */
    public UsersDeleteDeviceKeyResponse deleteDeviceKey(String id, String keyId) {
        return t.request("DELETE", "/v1/users/" + enc(id) + "/device_keys/" + enc(keyId), null, null, null, false, new TypeReference<>() {
        });
    }

    /** The {@code requiredActions} sub-resource. */
    public UsersRequiredActions requiredActions() {
        return new UsersRequiredActions(t);
    }

    /** The {@code notes} sub-resource. */
    public UsersNotes notes() {
        return new UsersNotes(t);
    }

    /** The {@code guardians} sub-resource. */
    public UsersGuardians guardians() {
        return new UsersGuardians(t);
    }

    /** The {@code search} sub-resource. */
    public UsersSearch search() {
        return new UsersSearch(t);
    }
}
