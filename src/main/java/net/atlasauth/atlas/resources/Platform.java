package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.AcceptedPlatformApp;
import net.atlasauth.atlas.models.AccountlessApp;
import net.atlasauth.atlas.models.AtlasDesign;
import net.atlasauth.atlas.models.AtlasIdApp;
import net.atlasauth.atlas.models.AtlasIdAppWithSecret;
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
import net.atlasauth.atlas.models.ListPlatformAppsParams;
import net.atlasauth.atlas.models.PlatformApp;
import net.atlasauth.atlas.models.PlatformAppBulkDeleteResult;
import net.atlasauth.atlas.models.PlatformAppDisabled;
import net.atlasauth.atlas.models.PlatformAppProvisioned;
import net.atlasauth.atlas.models.PlatformAppSecretRotation;
import net.atlasauth.atlas.models.PlatformAppTransfer;
import net.atlasauth.atlas.models.PlatformAppWithSecret;
import net.atlasauth.atlas.models.PlatformInstanceSecret;
import net.atlasauth.atlas.models.PlatformSecretKey;
import net.atlasauth.atlas.models.UpdateAtlasIdAppBody;
import net.atlasauth.atlas.models.UpdatePlatformAppBody;

import java.util.List;
import java.util.Map;

import static net.atlasauth.atlas.http.Transport.enc;

/** The platform (management-plane) API, authenticated by a platform key ({@code psk_...}): provision apps, register Sign in with Atlas apps, and manage account-owned design images. */
public final class Platform {

    private final Transport t;
    private final DesignAssets designAssets;
    private final AtlasIdApps atlasIdApps;
    private final Apps apps;

    public Platform(Transport t) {
        this.t = t;
        this.designAssets = new DesignAssets(t);
        this.atlasIdApps = new AtlasIdApps(t);
        this.apps = new Apps(t);
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
        public AtlasIdAppWithSecret rotateSecret(String id) {
            return t.post("/v1/platform/atlas_id_apps/" + enc(id) + "/rotate_secret", null, new TypeReference<>() {
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

        /** {@code POST /v1/platform/apps/{id}/instances/{instanceId}/rotate_secret} — Rotate one specific instance's secret under an owned app: revoke its prior keys and mint a new one, revealed once. */
        public PlatformInstanceSecret rotateInstanceSecret(String id, String instanceId) {
            return t.post("/v1/platform/apps/" + enc(id) + "/instances/" + enc(instanceId) + "/rotate_secret", null, new TypeReference<>() {
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
}
