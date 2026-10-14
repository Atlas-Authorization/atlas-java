package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ConfigChangelogEntry(
        String id,
        long at,
        AuditEventActor actor,
        String action,
        String ip,
        List<ConfigChangelogEntryChanges> changes,
        List<String> fields) {
}
