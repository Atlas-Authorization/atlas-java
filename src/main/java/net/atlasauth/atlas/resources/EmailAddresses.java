package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.EmailAddress;

import java.util.List;
import java.util.Map;

import static net.atlasauth.atlas.http.Transport.enc;

/** The {@code /v1/email_addresses} resource. */
public final class EmailAddresses {

    private final Transport t;

    public EmailAddresses(Transport t) {
        this.t = t;
    }

    /** {@code POST /v1/email_addresses/{eid}/verify} — Mark an address verified — flat Clerk-shape alias (user derived from the address). */
    public EmailAddress verify(String eid) {
        return t.post("/v1/email_addresses/" + enc(eid) + "/verify", null, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/email_addresses/{eid}/primary} — Make a verified address primary — flat Clerk-shape alias (user derived from the address). */
    public EmailAddress setPrimary(String eid) {
        return t.post("/v1/email_addresses/" + enc(eid) + "/primary", null, new TypeReference<>() {
        });
    }
}
