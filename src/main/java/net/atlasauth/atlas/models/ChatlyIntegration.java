package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Map;

/** The Chatly connection ({@code enabled: false} and nothing else when none is set). */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ChatlyIntegration(
        String object,
        boolean enabled,
        String channel,
        /** The name to pass to getToken({ template: 'chatly' }). */ String template,
        String audience,
        String issuer,
        String jwksUrl,
        Integer tokenTtlSeconds,
        Map<String, String> identityVerification) {
}
