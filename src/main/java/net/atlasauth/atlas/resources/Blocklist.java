package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.BlocklistIdentifier;
import net.atlasauth.atlas.models.DeletedObject;
import net.atlasauth.atlas.models.ListPage;

import static net.atlasauth.atlas.http.Transport.enc;

/** The {@code /v1/blocklist_identifiers} resource. */
public final class Blocklist {

    private final Transport t;

    public Blocklist(Transport t) {
        this.t = t;
    }

    public ListPage<BlocklistIdentifier> list() {
        return t.get("/v1/blocklist_identifiers", new TypeReference<>() {
        });
    }

    public BlocklistIdentifier add(String identifier) {
        return t.post("/v1/blocklist_identifiers", Params.of("identifier", identifier).map(),
                new TypeReference<>() {
                });
    }

    public DeletedObject remove(String id) {
        return t.delete("/v1/blocklist_identifiers/" + enc(id), new TypeReference<>() {
        });
    }
}
