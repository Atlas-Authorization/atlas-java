package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

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
        /** false once auto-disabled after repeated failures; null without a webhook. */ Boolean webhookActive) {

    /** The previous shape, without {@code webhookUrl}, {@code webhookEvents}, {@code webhookActive}. */
    public AtlasIdApp(String object, String id, String clientId, String name, String logoUrl, String homepageUrl, AtlasIdAppBranding branding, List<String> redirectUris, List<String> postLogoutRedirectUris, String backchannelLogoutUri, boolean backchannelLogoutSessionRequired, String frontchannelLogoutUri, boolean frontchannelLogoutSessionRequired, List<String> allowedScopes, String type, String secretPrefix, String managedByInstanceId, long createdAt, long updatedAt) {
        this(object, id, clientId, name, logoUrl, homepageUrl, branding, redirectUris, postLogoutRedirectUris, backchannelLogoutUri, backchannelLogoutSessionRequired, frontchannelLogoutUri, frontchannelLogoutSessionRequired, allowedScopes, type, secretPrefix, managedByInstanceId, createdAt, updatedAt, null, null, null);
    }
}
