package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

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
        /** The per-app webhook signing secret, returned exactly once. */ String webhookSecret) {

    /** The previous shape, without {@code webhookUrl}, {@code webhookEvents}, {@code webhookActive}, {@code webhookSecret}. */
    public AtlasIdAppWithSecret(String object, String id, String clientId, String name, String logoUrl, String homepageUrl, AtlasIdAppBranding branding, List<String> redirectUris, List<String> postLogoutRedirectUris, String backchannelLogoutUri, boolean backchannelLogoutSessionRequired, String frontchannelLogoutUri, boolean frontchannelLogoutSessionRequired, List<String> allowedScopes, String type, String secretPrefix, String managedByInstanceId, long createdAt, long updatedAt, String clientSecret, String note) {
        this(object, id, clientId, name, logoUrl, homepageUrl, branding, redirectUris, postLogoutRedirectUris, backchannelLogoutUri, backchannelLogoutSessionRequired, frontchannelLogoutUri, frontchannelLogoutSessionRequired, allowedScopes, type, secretPrefix, managedByInstanceId, createdAt, updatedAt, clientSecret, note, null, null, null, null);
    }
}
