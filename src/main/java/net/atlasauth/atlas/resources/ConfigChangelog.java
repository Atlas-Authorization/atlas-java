package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.ConfigChangelogEntry;
import net.atlasauth.atlas.models.CursorPage;
import net.atlasauth.atlas.models.ListConfigChangelogParams;

import java.util.List;
import java.util.Map;

import static net.atlasauth.atlas.http.Transport.enc;

/** Config change history ({@code /v1/config_changelog}). */
public final class ConfigChangelog {

    private final Transport t;

    public ConfigChangelog(Transport t) {
        this.t = t;
    }

    public CursorPage<ConfigChangelogEntry> list() {
        return list(null);
    }

    /** {@code GET /v1/config_changelog} — Config change history for this instance — who changed which config, when, with the per-section before/after diff where the source recorded one (else the changed field names). */
    public CursorPage<ConfigChangelogEntry> list(ListConfigChangelogParams params) {
        return t.get("/v1/config_changelog", params == null ? null : params.toQuery(), new TypeReference<>() {
        });
    }
}
