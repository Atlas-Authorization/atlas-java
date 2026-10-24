package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AtlasIdAppWithSecret(
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
        String clientSecret,
        String note,
        /** The per-app webhook URL, or null. */ String webhookUrl,
        List<String> webhookEvents,
        Boolean webhookActive,
        /** The per-app webhook signing secret, returned exactly once. */ String webhookSecret,
        /** While a webhook secret rotation overlap is open: when the previous secret stops signing (epoch ms); else null. */ Long webhookPreviousSecretExpiresAt,
        /** After a client secret rotation with an overlap: when the previous secret stops working (epoch ms); else null. */ Long previousSecretExpiresAt,
        /** When the current client secret was issued (epoch ms); null for a public app. */ Long secretIssuedAt,
        /** On a rotation: the overlap applied, in seconds. */ Integer overlapSeconds,
        /** Provisioning tags (strings, numbers and booleans). */ Map<String, Object> metadata,
        /** Your idempotent-create key. */ String externalRef,
        /** True on a create that sent an {@code external_ref} and got the existing object back (200, no secret). */ Boolean idempotentReplay) {
    /** The shape before the provisioning fields. */
    public AtlasIdAppWithSecret(String object, String id, String clientId, String name, String logoUrl, String homepageUrl, AtlasIdAppBranding branding, List<String> redirectUris, List<String> postLogoutRedirectUris, String backchannelLogoutUri, boolean backchannelLogoutSessionRequired, String frontchannelLogoutUri, boolean frontchannelLogoutSessionRequired, List<String> allowedScopes, String type, String secretPrefix, String managedByInstanceId, long createdAt, long updatedAt, String clientSecret, String note, String webhookUrl, List<String> webhookEvents, Boolean webhookActive, String webhookSecret, Long webhookPreviousSecretExpiresAt, Long previousSecretExpiresAt, Long secretIssuedAt, Integer overlapSeconds) {
        this(object, id, clientId, name, logoUrl, homepageUrl, branding, redirectUris, postLogoutRedirectUris, backchannelLogoutUri, backchannelLogoutSessionRequired, frontchannelLogoutUri, frontchannelLogoutSessionRequired, allowedScopes, type, secretPrefix, managedByInstanceId, createdAt, updatedAt, clientSecret, note, webhookUrl, webhookEvents, webhookActive, webhookSecret, webhookPreviousSecretExpiresAt, previousSecretExpiresAt, secretIssuedAt, overlapSeconds, null, null, null);
    }


    /** The 1.5 shape, without the rotation fields. */
    public AtlasIdAppWithSecret(String object, String id, String clientId, String name, String logoUrl, String homepageUrl, AtlasIdAppBranding branding, List<String> redirectUris, List<String> postLogoutRedirectUris, String backchannelLogoutUri, boolean backchannelLogoutSessionRequired, String frontchannelLogoutUri, boolean frontchannelLogoutSessionRequired, List<String> allowedScopes, String type, String secretPrefix, String managedByInstanceId, long createdAt, long updatedAt, String clientSecret, String note, String webhookUrl, List<String> webhookEvents, Boolean webhookActive, String webhookSecret) {
        this(object, id, clientId, name, logoUrl, homepageUrl, branding, redirectUris, postLogoutRedirectUris, backchannelLogoutUri, backchannelLogoutSessionRequired, frontchannelLogoutUri, frontchannelLogoutSessionRequired, allowedScopes, type, secretPrefix, managedByInstanceId, createdAt, updatedAt, clientSecret, note, webhookUrl, webhookEvents, webhookActive, webhookSecret, null, null, null, null);
    }

    /** The previous shape, without {@code webhookUrl}, {@code webhookEvents}, {@code webhookActive}, {@code webhookSecret}. */
    public AtlasIdAppWithSecret(String object, String id, String clientId, String name, String logoUrl, String homepageUrl, AtlasIdAppBranding branding, List<String> redirectUris, List<String> postLogoutRedirectUris, String backchannelLogoutUri, boolean backchannelLogoutSessionRequired, String frontchannelLogoutUri, boolean frontchannelLogoutSessionRequired, List<String> allowedScopes, String type, String secretPrefix, String managedByInstanceId, long createdAt, long updatedAt, String clientSecret, String note) {
        this(object, id, clientId, name, logoUrl, homepageUrl, branding, redirectUris, postLogoutRedirectUris, backchannelLogoutUri, backchannelLogoutSessionRequired, frontchannelLogoutUri, frontchannelLogoutSessionRequired, allowedScopes, type, secretPrefix, managedByInstanceId, createdAt, updatedAt, clientSecret, note, null, null, null, null, null, null, null, null);
    }
}
