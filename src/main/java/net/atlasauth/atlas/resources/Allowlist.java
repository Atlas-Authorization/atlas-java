package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.AllowlistIdentifier;
import net.atlasauth.atlas.models.DeletedObject;
import net.atlasauth.atlas.models.ListPage;

import static net.atlasauth.atlas.http.Transport.enc;

/** The {@code /v1/allowlist_identifiers} resource. */
public final class Allowlist {

    private final Transport t;

    public Allowlist(Transport t) {
        this.t = t;
    }

    public ListPage<AllowlistIdentifier> list() {
        return t.get("/v1/allowlist_identifiers", new TypeReference<>() {
        });
    }

    public AllowlistIdentifier add(String identifier) {
        return t.post("/v1/allowlist_identifiers", Params.of("identifier", identifier).map(),
                new TypeReference<>() {
                });
    }

    public DeletedObject remove(String id) {
        return t.delete("/v1/allowlist_identifiers/" + enc(id), new TypeReference<>() {
        });
    }
}
