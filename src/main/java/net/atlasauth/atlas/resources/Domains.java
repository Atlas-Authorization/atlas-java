package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.CustomDomain;
import net.atlasauth.atlas.models.DeletedObject;
import net.atlasauth.atlas.models.ListDomainsResponse;

import static net.atlasauth.atlas.http.Transport.enc;

/**
 * The {@code /v1/domains} resource — instance custom domains. Distinct from the
 * per-organization domains under {@link Organizations.Domains}.
 */
public final class Domains {

    private final Transport t;

    public Domains(Transport t) {
        this.t = t;
    }

    public ListDomainsResponse list() {
        return t.get("/v1/domains", new TypeReference<>() {
        });
    }

    public CustomDomain create(String host) {
        return create(host, null, null);
    }

    /** @param role optional {@code fapi} or {@code accounts}. */
    public CustomDomain create(String host, String role, String idempotencyKey) {
        var body = Params.of().set("host", host).set("role", role).map();
        return t.post("/v1/domains", body, idempotencyKey, new TypeReference<>() {
        });
    }

    /**
     * @param dns optional DNS automation: {@code provider} ("cloudflare"), {@code api_token}
     *            (write-only) and {@code overwrite}; Atlas then writes the record itself.
     */
    public CustomDomain create(String host, String role, java.util.Map<String, Object> dns, String idempotencyKey) {
        var body = Params.of().set("host", host).set("role", role).set("dns", dns).map();
        return t.post("/v1/domains", body, idempotencyKey, new TypeReference<>() {
        });
    }

    public CustomDomain verify(String id) {
        return t.post("/v1/domains/" + enc(id) + "/verify", null, new TypeReference<>() {
        });
    }

    public DeletedObject delete(String id) {
        return t.delete("/v1/domains/" + enc(id), new TypeReference<>() {
        });
    }
}
