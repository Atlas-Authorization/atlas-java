package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AtlasIdApp(
        String object,
        String id,
        String clientId,
        String name,
        String logoUrl,
        String homepageUrl,
        AtlasIdAppBranding branding,
        List<String> redirectUris,
        List<String> postLogoutRedirectUris,
        String backchannelLogoutUri,
        boolean backchannelLogoutSessionRequired,
        String frontchannelLogoutUri,
        boolean frontchannelLogoutSessionRequired,
        List<String> allowedScopes,
        String type,
        String secretPrefix,
        String managedByInstanceId,
        long createdAt,
        long updatedAt,
        /** The per-app webhook URL, or null. */ String webhookUrl,
        List<String> webhookEvents,
        /** false once auto-disabled after repeated failures; null without a webhook. */ Boolean webhookActive,
        /** While a webhook secret rotation overlap is open: when the previous secret stops signing (epoch ms); else null. */ Long webhookPreviousSecretExpiresAt,
        /** After a client secret rotation with an overlap: when the previous secret stops working (epoch ms); else null. */ Long previousSecretExpiresAt,
        /** When the current client secret was issued (epoch ms); null for a public app. */ Long secretIssuedAt,
        /** Provisioning tags (strings, numbers and booleans). */ Map<String, Object> metadata,
        /** Your idempotent-create key. */ String externalRef,
        /** True on a create that sent an {@code external_ref} and got the existing object back (200, no secret). */ Boolean idempotentReplay,
        /** instance_toggle or adopted (handed to an instance with setManagedBy); null otherwise. */ String managedVia,
        /** The page origins the Sign in with Atlas button script ran on in the last 7 days. */ List<AtlasIdOriginSeen> buttonRenderedOrigins) {
    /** The shape before managedVia, buttonRenderedOrigins. */
    public AtlasIdApp(String object, String id, String clientId, String name, String logoUrl, String homepageUrl, AtlasIdAppBranding branding, List<String> redirectUris, List<String> postLogoutRedirectUris, String backchannelLogoutUri, boolean backchannelLogoutSessionRequired, String frontchannelLogoutUri, boolean frontchannelLogoutSessionRequired, List<String> allowedScopes, String type, String secretPrefix, String managedByInstanceId, long createdAt, long updatedAt, String webhookUrl, List<String> webhookEvents, Boolean webhookActive, Long webhookPreviousSecretExpiresAt, Long previousSecretExpiresAt, Long secretIssuedAt, Map<String, Object> metadata, String externalRef, Boolean idempotentReplay) {
        this(object, id, clientId, name, logoUrl, homepageUrl, branding, redirectUris, postLogoutRedirectUris, backchannelLogoutUri, backchannelLogoutSessionRequired, frontchannelLogoutUri, frontchannelLogoutSessionRequired, allowedScopes, type, secretPrefix, managedByInstanceId, createdAt, updatedAt, webhookUrl, webhookEvents, webhookActive, webhookPreviousSecretExpiresAt, previousSecretExpiresAt, secretIssuedAt, metadata, externalRef, idempotentReplay, null, null);
    }

    /** The shape before the provisioning fields ({@code metadata}, {@code externalRef}, {@code idempotentReplay}). */
    public AtlasIdApp(String object, String id, String clientId, String name, String logoUrl, String homepageUrl, AtlasIdAppBranding branding, List<String> redirectUris, List<String> postLogoutRedirectUris, String backchannelLogoutUri, boolean backchannelLogoutSessionRequired, String frontchannelLogoutUri, boolean frontchannelLogoutSessionRequired, List<String> allowedScopes, String type, String secretPrefix, String managedByInstanceId, long createdAt, long updatedAt, String webhookUrl, List<String> webhookEvents, Boolean webhookActive, Long webhookPreviousSecretExpiresAt, Long previousSecretExpiresAt, Long secretIssuedAt) {
        this(object, id, clientId, name, logoUrl, homepageUrl, branding, redirectUris, postLogoutRedirectUris, backchannelLogoutUri, backchannelLogoutSessionRequired, frontchannelLogoutUri, frontchannelLogoutSessionRequired, allowedScopes, type, secretPrefix, managedByInstanceId, createdAt, updatedAt, webhookUrl, webhookEvents, webhookActive, webhookPreviousSecretExpiresAt, previousSecretExpiresAt, secretIssuedAt, null, null, null);
    }


    /** The 1.5 shape, without {@code webhookPreviousSecretExpiresAt}, {@code previousSecretExpiresAt}, {@code secretIssuedAt}. */
    public AtlasIdApp(String object, String id, String clientId, String name, String logoUrl, String homepageUrl, AtlasIdAppBranding branding, List<String> redirectUris, List<String> postLogoutRedirectUris, String backchannelLogoutUri, boolean backchannelLogoutSessionRequired, String frontchannelLogoutUri, boolean frontchannelLogoutSessionRequired, List<String> allowedScopes, String type, String secretPrefix, String managedByInstanceId, long createdAt, long updatedAt, String webhookUrl, List<String> webhookEvents, Boolean webhookActive) {
        this(object, id, clientId, name, logoUrl, homepageUrl, branding, redirectUris, postLogoutRedirectUris, backchannelLogoutUri, backchannelLogoutSessionRequired, frontchannelLogoutUri, frontchannelLogoutSessionRequired, allowedScopes, type, secretPrefix, managedByInstanceId, createdAt, updatedAt, webhookUrl, webhookEvents, webhookActive, null, null, null);
    }

    /** The previous shape, without {@code webhookUrl}, {@code webhookEvents}, {@code webhookActive}. */
    public AtlasIdApp(String object, String id, String clientId, String name, String logoUrl, String homepageUrl, AtlasIdAppBranding branding, List<String> redirectUris, List<String> postLogoutRedirectUris, String backchannelLogoutUri, boolean backchannelLogoutSessionRequired, String frontchannelLogoutUri, boolean frontchannelLogoutSessionRequired, List<String> allowedScopes, String type, String secretPrefix, String managedByInstanceId, long createdAt, long updatedAt) {
        this(object, id, clientId, name, logoUrl, homepageUrl, branding, redirectUris, postLogoutRedirectUris, backchannelLogoutUri, backchannelLogoutSessionRequired, frontchannelLogoutUri, frontchannelLogoutSessionRequired, allowedScopes, type, secretPrefix, managedByInstanceId, createdAt, updatedAt, null, null, null, null, null, null);
    }
}
