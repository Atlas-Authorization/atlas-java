package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.AcceptedPlatformApp;
import net.atlasauth.atlas.models.AccountlessApp;
import net.atlasauth.atlas.models.AtlasDesign;
import net.atlasauth.atlas.models.AtlasIdApp;
import net.atlasauth.atlas.models.AtlasIdAppManagedBy;
import net.atlasauth.atlas.models.AtlasIdAppPreflight;
import net.atlasauth.atlas.models.AtlasIdAppUser;
import net.atlasauth.atlas.models.AtlasIdAppWithSecret;
import net.atlasauth.atlas.models.AtlasIdGrantRevocation;
import net.atlasauth.atlas.models.AtlasIdPublisher;
import net.atlasauth.atlas.models.AtlasIdPublisherBody;
import net.atlasauth.atlas.models.BackchannelLogoutDelivery;
import net.atlasauth.atlas.models.BackchannelLogoutTest;
import net.atlasauth.atlas.models.ConfigChangelogEntry;
import net.atlasauth.atlas.models.CreateAccountlessAppBody;
import net.atlasauth.atlas.models.CreateAtlasIdAppBody;
import net.atlasauth.atlas.models.CreatePlatformAppBody;
import net.atlasauth.atlas.models.CreatePlatformInstanceBody;
import net.atlasauth.atlas.models.CursorPage;
import net.atlasauth.atlas.models.DataList;
import net.atlasauth.atlas.models.DeletedDesignAsset;
import net.atlasauth.atlas.models.DeletedObject;
import net.atlasauth.atlas.models.DesignAsset;
import net.atlasauth.atlas.models.DesignBody;
import net.atlasauth.atlas.models.DesignPreview;
import net.atlasauth.atlas.models.ListConfigChangelogParams;
import net.atlasauth.atlas.models.ListPage;
import net.atlasauth.atlas.models.ListPlatformAppsParams;
import net.atlasauth.atlas.models.OriginMove;
import net.atlasauth.atlas.models.OriginMoveBody;
import net.atlasauth.atlas.models.PlatformApp;
import net.atlasauth.atlas.models.PlatformAppBulkDeleteResult;
import net.atlasauth.atlas.models.PlatformAppDisabled;
import net.atlasauth.atlas.models.PlatformAppProvisioned;
import net.atlasauth.atlas.models.PlatformAppSecretRotation;
import net.atlasauth.atlas.models.PlatformAppTransfer;
import net.atlasauth.atlas.models.PlatformAppWithSecret;
import net.atlasauth.atlas.models.PlatformInstanceSecret;
import net.atlasauth.atlas.models.PlatformSecretKey;
import net.atlasauth.atlas.models.RotateAtlasIdAppSecretBody;
import net.atlasauth.atlas.models.RotatePlatformSecretBody;
import net.atlasauth.atlas.models.UpdateAtlasIdAppBody;
import net.atlasauth.atlas.models.UpdatePlatformAppBody;
import net.atlasauth.atlas.models.Whoami;
import net.atlasauth.atlas.models.StandardKey;
import net.atlasauth.atlas.models.UpdateSecretKeyBody;

import java.util.List;
import java.util.Map;

import static net.atlasauth.atlas.http.Transport.enc;

/** The platform (management-plane) API, authenticated by a platform key ({@code psk_...}): provision apps, register Sign in with Atlas apps, and manage account-owned design images. */
public final class Platform {

    private final Transport t;
    private final DesignAssets designAssets;
    private final AtlasIdApps atlasIdApps;
    private final Apps apps;
    private final AtlasIdPublisherResource atlasIdPublisher;
    private final PlatformKeys keys;
    private final PlatformAudit audit;

    public Platform(Transport t) {
        this.keys = new PlatformKeys(t);
        this.audit = new PlatformAudit(t);
        this.t = t;
        this.designAssets = new DesignAssets(t);
        this.atlasIdApps = new AtlasIdApps(t);
        this.apps = new Apps(t);
        this.atlasIdPublisher = new AtlasIdPublisherResource(t);
    }

    /** {@code GET /v1/platform/whoami} — What the platform key (or one-hour access token) in use is, its scopes and the account it belongs to. */
    public Whoami whoami() {
        return t.get("/v1/platform/whoami", new TypeReference<>() {
        });
    }

    /** Every credential on the account, and minting / revoking platform keys ({@code /v1/platform/keys}). */
    public PlatformKeys keys() {
        return keys;
    }

    /** The account audit log and its streams ({@code /v1/platform/audit_logs}, {@code /v1/platform/audit_streams}). */
    public PlatformAudit audit() {
        return audit;
    }

    /** The account's Sign in with Atlas publisher ({@code /v1/platform/atlas_id_publisher}). */
    public AtlasIdPublisherResource atlasIdPublisher() {
        return atlasIdPublisher;
    }

    public CursorPage<ConfigChangelogEntry> configChangelog() {
        return configChangelog(null);
    }

    /** {@code GET /v1/platform/config_changelog} — Config change history for the account's Sign in with Atlas apps. */
    public CursorPage<ConfigChangelogEntry> configChangelog(ListConfigChangelogParams params) {
        return t.get("/v1/platform/config_changelog", params == null ? null : params.toQuery(), new TypeReference<>() {
        });
    }

    // ---- AtlasIdPublisher ---------------------------------------------

    /** The account's Sign in with Atlas publisher name and DNS-verified domain. */
    public static final class AtlasIdPublisherResource {

        private final Transport t;

        public AtlasIdPublisherResource(Transport t) {
            this.t = t;
        }

        /** {@code GET /v1/platform/atlas_id_publisher} — The publisher (404 until claimed). */
        public AtlasIdPublisher get() {
            return t.get("/v1/platform/atlas_id_publisher", new TypeReference<>() {
            });
        }

        /** {@code PUT /v1/platform/atlas_id_publisher} — Claim a publisher name and domain; returns the TXT record to publish. */
        public AtlasIdPublisher set(AtlasIdPublisherBody body) {
            return t.put("/v1/platform/atlas_id_publisher", body, new TypeReference<>() {
            });
        }

        /** {@code PATCH /v1/platform/atlas_id_publisher} — Turn publisher-wide consent on or off. */
        public AtlasIdPublisher setSkipConsent(boolean skipConsent) {
            return t.patch("/v1/platform/atlas_id_publisher", Map.of("skip_consent", skipConsent), new TypeReference<>() {
            });
        }

        /** {@code DELETE /v1/platform/atlas_id_publisher} — Remove the publisher claim. */
        public JsonNode delete() {
            return t.delete("/v1/platform/atlas_id_publisher", new TypeReference<>() {
            });
        }

        /** {@code POST /v1/platform/atlas_id_publisher/verify} — Look up the TXT record now. */
        public AtlasIdPublisher verify() {
            return t.post("/v1/platform/atlas_id_publisher/verify", null, new TypeReference<>() {
            });
        }
    }

    /** Design images owned by the platform account ({@code /v1/platform/design/assets}). */
    public DesignAssets designAssets() {
        return designAssets;
    }

    /** Sign in with Atlas apps ({@code /v1/platform/atlas_id_apps}). */
    public AtlasIdApps atlasIdApps() {
        return atlasIdApps;
    }

    /** Apps (instances) owned by the platform ({@code /v1/platform/apps}). */
    public Apps apps() {
        return apps;
    }

    // ---- DesignAssets ------------------------------------------------

    /** Design images owned by the platform account ({@code /v1/platform/design/assets}). */
    public static final class DesignAssets {
        private final Transport t;

        DesignAssets(Transport t) {
            this.t = t;
        }

        /** Upload raw image bytes ({@code contentType} e.g. {@code image/png}) as a background. */
        public DesignAsset upload(byte[] data, String contentType) {
            return upload(data, contentType, null);
        }

        /** {@code POST /v1/platform/design/assets} — Upload a design image owned by the platform's account, for Sign in with Atlas app designs. */
        public DesignAsset upload(byte[] data, String contentType, String kind) {
            Map<String, Object> q = Params.of().set("kind", kind).map();
            return t.requestBytes("POST", "/v1/platform/design/assets", q, data, contentType, new TypeReference<>() {
            });
        }

        /** {@code GET /v1/platform/design/assets} — List the design images owned by the platform's account, newest first. */
        public DataList<DesignAsset> list() {
            return t.get("/v1/platform/design/assets", new TypeReference<>() {
            });
        }

        /** {@code DELETE /v1/platform/design/assets/{id}} — Delete a design image owned by the platform's account. */
        public DeletedDesignAsset delete(String id) {
            return t.delete("/v1/platform/design/assets/" + enc(id), new TypeReference<>() {
            });
        }
    }

    // ---- AtlasIdApps -------------------------------------------------

    /** Sign in with Atlas apps ({@code /v1/platform/atlas_id_apps}). */
    public static final class AtlasIdApps {
        private final Transport t;

        AtlasIdApps(Transport t) {
            this.t = t;
        }

        /** {@code POST /v1/platform/atlas_id_apps/design/preview} — Preview a Sign in with Atlas app design without saving it: the compiled bundle plus the rendered Sign in with Atlas page (the Atlas trust strip, the destination line and "Secured by Atlas" are always there). */
        public DesignPreview previewDesign(DesignBody body) {
            return t.post("/v1/platform/atlas_id_apps/design/preview", body, new TypeReference<>() {
            });
        }

        /** Shorthand: wraps {@code design} in a {@link DesignBody}. */
        public DesignPreview previewDesign(AtlasDesign design) {
            return previewDesign(new DesignBody().design(design));
        }

        /** As above, with an {@code Idempotency-Key} so a retry replays the first response. */
        public DesignPreview previewDesign(DesignBody body, String idempotencyKey) {
            return t.post("/v1/platform/atlas_id_apps/design/preview", body, idempotencyKey, new TypeReference<>() {
            });
        }

        /** {@code GET /v1/platform/atlas_id_apps} — List the Sign in with Atlas apps registered to this platform's account, with the issuer and discovery URL. */
        public DataList<AtlasIdApp> list() {
            return t.get("/v1/platform/atlas_id_apps", new TypeReference<>() {
            });
        }

        /** {@code GET /v1/platform/atlas_id_apps} — The apps whose provisioning metadata matches every pair of {@code metadata}. */
        public DataList<AtlasIdApp> list(Map<String, Object> metadata) {
            return t.get("/v1/platform/atlas_id_apps", Params.putMetadata(new java.util.LinkedHashMap<>(), metadata), new TypeReference<>() {
            });
        }

        /** {@code POST /v1/platform/atlas_id_apps} — Register a Sign in with Atlas app (an OpenID Connect client on https://atlasauth.net) for this platform's account. */
        public AtlasIdAppWithSecret create(CreateAtlasIdAppBody body) {
            return t.post("/v1/platform/atlas_id_apps", body, new TypeReference<>() {
            });
        }

        /** As above, with an {@code Idempotency-Key} so a retry replays the first response. */
        public AtlasIdAppWithSecret create(CreateAtlasIdAppBody body, String idempotencyKey) {
            return t.post("/v1/platform/atlas_id_apps", body, idempotencyKey, new TypeReference<>() {
            });
        }

        /** {@code GET /v1/platform/atlas_id_apps/{id}} — Fetch one Sign in with Atlas app, including user_count (people who have approved it). */
        public AtlasIdApp get(String id) {
            return t.get("/v1/platform/atlas_id_apps/" + enc(id), new TypeReference<>() {
            });
        }

        /** {@code PATCH /v1/platform/atlas_id_apps/{id}} — Update a Sign in with Atlas app: name, redirect_uris, post_logout_redirect_uris ([] clears them), backchannel_logout_uri / frontchannel_logout_uri (empty or null clears) and their *_session_required flags, allowed_scopes, logo_url, homepage_url,… */
        public AtlasIdApp update(String id, UpdateAtlasIdAppBody body) {
            return t.patch("/v1/platform/atlas_id_apps/" + enc(id), body, new TypeReference<>() {
            });
        }

        /** {@code DELETE /v1/platform/atlas_id_apps/{id}} — Delete a Sign in with Atlas app. */
        public DeletedObject delete(String id) {
            return t.delete("/v1/platform/atlas_id_apps/" + enc(id), new TypeReference<>() {
            });
        }

        /** {@code POST /v1/platform/atlas_id_apps/{id}/rotate_secret} — Rotate a confidential app's client_secret. */
        public CursorPage<BackchannelLogoutDelivery> listBackchannelDeliveries(String id) {
            return listBackchannelDeliveries(id, null, null);
        }

        /** {@code GET /v1/platform/atlas_id_apps/{id}/backchannel_deliveries} — The app's back-channel logout delivery log, newest first. */
        public CursorPage<BackchannelLogoutDelivery> listBackchannelDeliveries(String id, Integer limit, String startingAfter) {
            return t.get("/v1/platform/atlas_id_apps/" + enc(id) + "/backchannel_deliveries",
                    Params.of().set("limit", limit).set("starting_after", startingAfter).map(), new TypeReference<>() {
                    });
        }

        /** {@code POST /v1/platform/atlas_id_apps/{id}/backchannel_logout/test} — Send a test back-channel logout to the app now. */
        public BackchannelLogoutTest testBackchannelLogout(String id) {
            return t.post("/v1/platform/atlas_id_apps/" + enc(id) + "/backchannel_logout/test", null, new TypeReference<>() {
            });
        }

        /** {@code GET /v1/platform/atlas_id_apps/{id}/preflight} — Check one app's setup. */
        public AtlasIdAppPreflight preflight(String id) {
            return t.get("/v1/platform/atlas_id_apps/" + enc(id) + "/preflight", new TypeReference<>() {
            });
        }

        /** {@code GET /v1/platform/atlas_id_apps/{id}/users} — Privacy requests: is this person (verified email, exact match) one of the app's users? Anyone else gets an empty list. */
        public ListPage<AtlasIdAppUser> findUsers(String id, String email) {
            return t.get("/v1/platform/atlas_id_apps/" + enc(id) + "/users", Params.of().set("email", email).map(),
                    new TypeReference<>() {
                    });
        }

        /** {@code DELETE /v1/platform/atlas_id_apps/{id}/grants/{grantId}} — Revoke one user's grant; the next sign-in shows the consent screen again. */
        public AtlasIdGrantRevocation revokeGrant(String id, String grantId) {
            return t.delete("/v1/platform/atlas_id_apps/" + enc(id) + "/grants/" + enc(grantId), new TypeReference<>() {
            });
        }

        /** {@code POST /v1/platform/atlas_id_apps/{id}/move_origin} — Move the app's URLs from one origin to another. */
        public OriginMove moveOrigin(String id, OriginMoveBody body) {
            return t.post("/v1/platform/atlas_id_apps/" + enc(id) + "/move_origin", body, new TypeReference<>() {
            });
        }

        /**
         * {@code PUT /v1/platform/atlas_id_apps/{id}/managed_by} — Hand the app's grant management to an instance of the same
         * account ({@code managed_via: "adopted"}), or take it back with {@code null}. That instance's secret key, scoped to
         * {@code privacy:grants:revoke} or {@code atlas_id_apps:*}, then lists and revokes the app's grants, so a data-rights
         * tool never needs the platform key.
         */
        public AtlasIdAppManagedBy setManagedBy(String id, String instanceId) {
            return t.put("/v1/platform/atlas_id_apps/" + enc(id) + "/managed_by", java.util.Collections.singletonMap("instance_id", instanceId), new TypeReference<>() {
            });
        }

        /** {@code POST /v1/platform/atlas_id_apps/{id}/webhook/rotate_secret} — Rotate the app's webhook signing secret (returned once). */
        public AtlasIdAppWithSecret rotateWebhookSecret(String id) {
            return t.post("/v1/platform/atlas_id_apps/" + enc(id) + "/webhook/rotate_secret", null, new TypeReference<>() {
            });
        }

        /**
         * {@code POST /v1/platform/atlas_id_apps/{id}/webhook/rotate_secret} — Rotate the webhook
         * signing secret with a chosen overlap: the previous secret signs each delivery too until
         * {@code webhookPreviousSecretExpiresAt}. Default 0 (it stops at once).
         */
        public AtlasIdAppWithSecret rotateWebhookSecret(String id, RotateAtlasIdAppSecretBody body) {
            return t.post("/v1/platform/atlas_id_apps/" + enc(id) + "/webhook/rotate_secret", body, new TypeReference<>() {
            });
        }

        /** {@code POST /v1/platform/atlas_id_apps/{id}/rotate_secret} — Rotate a confidential app's client_secret; the previous one keeps working for 24 hours. */
        public AtlasIdAppWithSecret rotateSecret(String id) {
            return t.post("/v1/platform/atlas_id_apps/" + enc(id) + "/rotate_secret", null, new TypeReference<>() {
            });
        }

        /**
         * {@code POST /v1/platform/atlas_id_apps/{id}/rotate_secret} — Rotate a confidential app's
         * client_secret with a chosen overlap (default 86400; 0 revokes the previous one now).
         */
        public AtlasIdAppWithSecret rotateSecret(String id, RotateAtlasIdAppSecretBody body) {
            return t.post("/v1/platform/atlas_id_apps/" + enc(id) + "/rotate_secret", body, new TypeReference<>() {
            });
        }
    }

    // ---- Apps --------------------------------------------------------

    /** Apps (instances) owned by the platform ({@code /v1/platform/apps}). */
    public static final class Apps {
        private final Transport t;

        Apps(Transport t) {
            this.t = t;
        }

        /** {@code POST /v1/platform/apps} — Provision a new app (instance) owned by the platform: mints its publishable key, signing key and first secret key. */
        public PlatformAppProvisioned create(CreatePlatformAppBody body) {
            return t.post("/v1/platform/apps", body, new TypeReference<>() {
            });
        }

        /** As above, with an {@code Idempotency-Key} so a retry replays the first response. */
        public PlatformAppProvisioned create(CreatePlatformAppBody body, String idempotencyKey) {
            return t.post("/v1/platform/apps", body, idempotencyKey, new TypeReference<>() {
            });
        }

        public CursorPage<PlatformApp> list() {
            return list(null);
        }

        /** {@code GET /v1/platform/apps} — List the apps (instances) this platform owns. */
        public CursorPage<PlatformApp> list(ListPlatformAppsParams params) {
            return t.get("/v1/platform/apps", params == null ? null : params.toQuery(), new TypeReference<>() {
            });
        }

        /** {@code GET /v1/platform/apps/{id}} — Fetch one owned app. */
        public PlatformApp get(String id) {
            return t.get("/v1/platform/apps/" + enc(id), new TypeReference<>() {
            });
        }

        /** {@code PATCH /v1/platform/apps/{id}} — Update an owned app's safe config subset (name, allowed_origins, and X8 housekeeping tags). */
        public PlatformApp update(String id, UpdatePlatformAppBody body) {
            return t.patch("/v1/platform/apps/" + enc(id), body, new TypeReference<>() {
            });
        }

        /** {@code DELETE /v1/platform/apps/{id}} — Disable an owned app: refuse sign-ins and sign-ups via its kill switches. */
        public PlatformAppDisabled disable(String id) {
            return t.delete("/v1/platform/apps/" + enc(id), new TypeReference<>() {
            });
        }

        /** {@code POST /v1/platform/apps/{id}/archive} — X8 housekeeping: archive an owned app so it drops out of the default app listing. */
        public PlatformApp archive(String id) {
            return t.post("/v1/platform/apps/" + enc(id) + "/archive", null, new TypeReference<>() {
            });
        }

        /** {@code POST /v1/platform/apps/{id}/unarchive} — X8 housekeeping: clear an owned app's archive flag, returning it to the default app listing. */
        public PlatformApp unarchive(String id) {
            return t.post("/v1/platform/apps/" + enc(id) + "/unarchive", null, new TypeReference<>() {
            });
        }

        /** {@code POST /v1/platform/apps/bulk_delete} — X8 housekeeping: delete up to 100 owned apps in one call, for tidying throwaway probes. */
        public PlatformAppBulkDeleteResult bulkDelete(List<String> ids) {
            Map<String, Object> payload = Params.of().set("ids", ids).map();
            return t.post("/v1/platform/apps/bulk_delete", payload, new TypeReference<>() {
            });
        }

        public PlatformAppSecretRotation rotateSecret(String id) {
            return rotateSecret(id, null);
        }

        /** {@code POST /v1/platform/apps/{id}/rotate_secret} — Rotate an owned app's secret key WITH OVERLAP (S3): mint a new sk_ (revealed exactly once) while the prior keys stay valid for a grace window so servers roll over without downtime. */
        public PlatformAppSecretRotation rotateSecret(String id, Double overlapSeconds) {
            Map<String, Object> payload = Params.of().set("overlap_seconds", overlapSeconds).map();
            return t.post("/v1/platform/apps/" + enc(id) + "/rotate_secret", payload, new TypeReference<>() {
            });
        }

        /** {@code GET /v1/platform/apps/{id}/secret_keys} — List an owned app's currently-valid secret keys (S3) — the primary key plus any prior key still inside its rotation overlap window. */
        public CursorPage<PlatformSecretKey> listSecretKeys(String id) {
            return t.get("/v1/platform/apps/" + enc(id) + "/secret_keys", new TypeReference<>() {
            });
        }

        /** {@code POST /v1/platform/apps/{id}/secret_keys} — Mint an additional named, taggable secret key for an app (shown once; an {@code external_ref} makes it idempotent). {@code body}: {@code name}, {@code instance_id}, {@code external_ref}, {@code metadata}. */
        public PlatformSecretKey createSecretKey(String id, Map<String, Object> body) {
            return createSecretKey(id, body, null);
        }

        /** As above, with an {@code Idempotency-Key}. */
        public PlatformSecretKey createSecretKey(String id, Map<String, Object> body, String idempotencyKey) {
            return t.post("/v1/platform/apps/" + enc(id) + "/secret_keys", body, idempotencyKey, new TypeReference<>() {
            });
        }

        /** {@code GET /v1/platform/apps/{id}/secret_keys} — The app's valid secret keys whose provisioning metadata matches every pair of {@code metadata}. */
        public CursorPage<PlatformSecretKey> listSecretKeys(String id, Map<String, Object> metadata) {
            return t.get("/v1/platform/apps/" + enc(id) + "/secret_keys", Params.putMetadata(new java.util.LinkedHashMap<>(), metadata), new TypeReference<>() {
            });
        }

        /**
         * {@code POST /v1/platform/apps/{id}/clone_config} — Copy an app's configuration into its development (or
         * another) instance, secrets excluded. {@code body}: {@code to_environment}, {@code target_instance_id},
         * {@code sections}, {@code include_origins}, {@code dry_run}.
         */
        public JsonNode cloneConfig(String id, Map<String, Object> body) {
            return cloneConfig(id, body, null);
        }

        /** As above, with an {@code Idempotency-Key}. */
        public JsonNode cloneConfig(String id, Map<String, Object> body, String idempotencyKey) {
            return t.post("/v1/platform/apps/" + enc(id) + "/clone_config", body, idempotencyKey, new TypeReference<>() {
            });
        }

        /**
         * {@code POST /v1/platform/apps/bulk_delete} — Hard-delete every sandbox-tagged app and resource of this
         * platform. {@code metadata} must carry {@code sandbox: true} (null = {@code {"sandbox": true}});
         * {@code dryRun} lists what would go.
         */
        public PlatformAppBulkDeleteResult sandboxTeardown(Map<String, Object> metadata, boolean dryRun) {
            Map<String, Object> payload = new java.util.LinkedHashMap<>();
            payload.put("metadata", metadata != null ? metadata : Map.of("sandbox", true));
            if (dryRun) {
                payload.put("dry_run", true);
            }
            return t.post("/v1/platform/apps/bulk_delete", payload, new TypeReference<>() {
            });
        }

        /** {@code POST /v1/platform/apps/{id}/instances} — Provision an additional instance (e.g. */
        public PlatformAppWithSecret createInstance(String id, CreatePlatformInstanceBody body) {
            return t.post("/v1/platform/apps/" + enc(id) + "/instances", body, new TypeReference<>() {
            });
        }

        /** As above, with an {@code Idempotency-Key} so a retry replays the first response. */
        public PlatformAppWithSecret createInstance(String id, CreatePlatformInstanceBody body, String idempotencyKey) {
            return t.post("/v1/platform/apps/" + enc(id) + "/instances", body, idempotencyKey, new TypeReference<>() {
            });
        }

        /** {@code GET /v1/platform/apps/{id}/instances} — List every instance under an owned app — its development/production siblings. */
        public CursorPage<PlatformApp> listInstances(String id) {
            return t.get("/v1/platform/apps/" + enc(id) + "/instances", new TypeReference<>() {
            });
        }

        /** {@code POST /v1/platform/apps/{id}/instances/{instanceId}/rotate_secret} — Mint a new secret key for one instance under an owned app, revealed once; the prior keys stay valid for the default overlap (72 hours). */
        public PlatformInstanceSecret rotateInstanceSecret(String id, String instanceId) {
            return t.post("/v1/platform/apps/" + enc(id) + "/instances/" + enc(instanceId) + "/rotate_secret", null, new TypeReference<>() {
            });
        }

        /**
         * {@code POST /v1/platform/apps/{id}/instances/{instanceId}/rotate_secret} — Rotate one
         * instance's secret key with a chosen overlap: the prior keys stay valid for
         * {@code overlapSeconds} (default 259200, at most 604800; 0 revokes them now).
         *
         * @param idempotencyKey optional Idempotency-Key (null for none)
         */
        public PlatformInstanceSecret rotateInstanceSecret(String id, String instanceId, RotatePlatformSecretBody body, String idempotencyKey) {
            return t.post("/v1/platform/apps/" + enc(id) + "/instances/" + enc(instanceId) + "/rotate_secret", body, idempotencyKey, new TypeReference<>() {
            });
        }

        /** {@code POST /v1/platform/accountless_apps} — Mint an app owned by no platform and return a one-time claim_token (hashed at rest, single-use, expiring). */
        public AccountlessApp createAccountless(CreateAccountlessAppBody body) {
            return t.post("/v1/platform/accountless_apps", body, new TypeReference<>() {
            });
        }

        /** As above, with an {@code Idempotency-Key} so a retry replays the first response. */
        public AccountlessApp createAccountless(CreateAccountlessAppBody body, String idempotencyKey) {
            return t.post("/v1/platform/accountless_apps", body, idempotencyKey, new TypeReference<>() {
            });
        }

        /** {@code POST /v1/platform/apps/claim} — Claim an accountless app into the calling platform by its claim_token. */
        public PlatformAppWithSecret claim(String claimToken) {
            Map<String, Object> payload = Params.of().set("claim_token", claimToken).map();
            return t.post("/v1/platform/apps/claim", payload, new TypeReference<>() {
            });
        }

        /** {@code POST /v1/platform/apps/{id}/transfer} — Initiate a transfer of an owned app: returns a one-time transfer_token bound to it (hashed, single-use, expiring). */
        public PlatformAppTransfer startTransfer(String id) {
            return t.post("/v1/platform/apps/" + enc(id) + "/transfer", null, new TypeReference<>() {
            });
        }

        /** {@code POST /v1/platform/apps/accept_transfer} — Accept a transfer by transfer_token: move the app's ownership to the calling platform and re-parent it to its account. */
        public AcceptedPlatformApp acceptTransfer(String transferToken) {
            Map<String, Object> payload = Params.of().set("transfer_token", transferToken).map();
            return t.post("/v1/platform/apps/accept_transfer", payload, new TypeReference<>() {
            });
        }
    }

    /** Every credential on the account, and minting / revoking platform keys ({@code /v1/platform/keys}). */
    public static final class PlatformKeys {
        private final Transport t;

        PlatformKeys(Transport t) {
            this.t = t;
        }

        /** {@code GET /v1/platform/keys} — Every credential on the account, revoked ones included. */
        public ListPage<StandardKey> list() {
            return list(null, null, null);
        }

        /**
         * {@code GET /v1/platform/keys} — {@code kind} (secret_key, platform_key, account_key, webhook_secret,
         * partner_link, access_token), {@code status} (active, expiring, expired, revoked) and
         * {@code instanceId} filter the list; each may be null.
         */
        public ListPage<StandardKey> list(String kind, String status, String instanceId) {
            return t.get("/v1/platform/keys", Params.of("kind", kind).set("status", status).set("instance_id", instanceId).map(), new TypeReference<>() {
            });
        }

        /**
         * {@code POST /v1/platform/keys} — Mint a platform key; the secret is in this response only. {@code body}:
         * {@code scopes} (a subset of the caller's; per-app grant scopes {@code atlas_id_apps:<app id>:grants:read|write}
         * are allowed), {@code name}, {@code application_ids}, {@code expires_at}, {@code ip_allowlist}, {@code owner},
         * {@code key_format} ({@code atlas} or {@code legacy}).
         */
        public JsonNode create(Map<String, Object> body) {
            return create(body, null);
        }

        /** As above, with an {@code Idempotency-Key}. */
        public JsonNode create(Map<String, Object> body, String idempotencyKey) {
            return t.post("/v1/platform/keys", body, idempotencyKey, new TypeReference<>() {
            });
        }

        /** {@code PATCH /v1/platform/keys/{id}} — Change a key's name, owner or expiry. */
        public JsonNode update(String id, UpdateSecretKeyBody body) {
            return t.patch("/v1/platform/keys/" + enc(id), body, new TypeReference<>() {
            });
        }

        /** {@code POST /v1/platform/keys/{id}/revoke} — Revoke a key at once. */
        public JsonNode revoke(String id) {
            return t.post("/v1/platform/keys/" + enc(id) + "/revoke", null, new TypeReference<>() {
            });
        }
    }

    /** The account audit log and its streams. */
    public static final class PlatformAudit {
        private final Transport t;
        private final Streams streams;

        PlatformAudit(Transport t) {
            this.t = t;
            this.streams = new Streams(t);
        }

        /** The account audit streams ({@code /v1/platform/audit_streams}). */
        public Streams streams() {
            return streams;
        }

        /** {@code GET /v1/platform/audit_logs} — The account audit log, newest first. */
        public JsonNode list() {
            return list(null, null, null);
        }

        /** {@code GET /v1/platform/audit_logs} — Filter by {@code action}; page with {@code limit} / {@code startingAfter} (each may be null). */
        public JsonNode list(String action, Integer limit, String startingAfter) {
            return t.get("/v1/platform/audit_logs", Params.of("action", action).set("limit", limit).set("starting_after", startingAfter).map(), new TypeReference<>() {
            });
        }

        /** Streams that send the account audit log to your SIEM. A body takes {@code url}, {@code format} (for example {@code cloudevents}) and {@code active}. */
        public static final class Streams {
            private final Transport t;

            Streams(Transport t) {
                this.t = t;
            }

            /** {@code GET /v1/platform/audit_streams} */
            public JsonNode list() {
                return t.get("/v1/platform/audit_streams", new TypeReference<>() {
                });
            }

            /** {@code POST /v1/platform/audit_streams} */
            public JsonNode create(Map<String, Object> body) {
                return t.post("/v1/platform/audit_streams", body, new TypeReference<>() {
                });
            }

            /** {@code PATCH /v1/platform/audit_streams/{id}} */
            public JsonNode update(String id, Map<String, Object> body) {
                return t.patch("/v1/platform/audit_streams/" + enc(id), body, new TypeReference<>() {
                });
            }

            /** {@code DELETE /v1/platform/audit_streams/{id}} */
            public JsonNode delete(String id) {
                return t.delete("/v1/platform/audit_streams/" + enc(id), new TypeReference<>() {
                });
            }

            /** {@code POST /v1/platform/audit_streams/{id}/test} — Send a test event. */
            public JsonNode test(String id) {
                return t.post("/v1/platform/audit_streams/" + enc(id) + "/test", null, new TypeReference<>() {
                });
            }
        }
    }
}
