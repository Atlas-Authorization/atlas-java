package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/** One subscribable webhook event, described. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record WebhookEventCatalogEntry(
        String name,
        String description,
        /** Who should act on it (rp_sessions, rp_accounts, dsar, …). */ List<String> relevantFor,
        /** Set on a superseded spelling: subscribe to this event instead. */ String deprecatedAliasOf) {
}
