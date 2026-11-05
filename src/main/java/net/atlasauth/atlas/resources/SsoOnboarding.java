package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.CreateSsoOnboardingProfileBody;
import net.atlasauth.atlas.models.CreateSsoOnboardingTicketBody;
import net.atlasauth.atlas.models.DeletedObject;
import net.atlasauth.atlas.models.IssuedSsoOnboardingTicket;
import net.atlasauth.atlas.models.ListPage;
import net.atlasauth.atlas.models.SsoOnboardingProfile;

import java.util.Map;

import static net.atlasauth.atlas.http.Transport.enc;

/** The self-service SSO onboarding profiles and tickets resource. */
public final class SsoOnboarding {

    private final Transport t;

    public SsoOnboarding(Transport t) {
        this.t = t;
    }

    public ListPage<SsoOnboardingProfile> list() {
        return t.get("/v1/sso_onboarding_profiles", new TypeReference<>() {
        });
    }

    /** Only the profiles named exactly {@code name} ("find or create"); null lists all. */
    public ListPage<SsoOnboardingProfile> list(String name) {
        return t.get("/v1/sso_onboarding_profiles", Params.of().set("name", name).map(), new TypeReference<>() {
        });
    }

    public SsoOnboardingProfile get(String id) {
        return t.get("/v1/sso_onboarding_profiles/" + enc(id), new TypeReference<>() {
        });
    }

    public SsoOnboardingProfile create(CreateSsoOnboardingProfileBody body) {
        return create(body, null);
    }

    public SsoOnboardingProfile create(CreateSsoOnboardingProfileBody body, String idempotencyKey) {
        return t.post("/v1/sso_onboarding_profiles", body, idempotencyKey, new TypeReference<>() {
        });
    }

    public DeletedObject delete(String id) {
        return t.delete("/v1/sso_onboarding_profiles/" + enc(id), new TypeReference<>() {
        });
    }

    /** Issue a one-time ticket for a profile. The token is returned once, here. */
    public IssuedSsoOnboardingTicket createTicket(String profileId) {
        return createTicket(profileId, new CreateSsoOnboardingTicketBody(), null);
    }

    public IssuedSsoOnboardingTicket createTicket(String profileId, CreateSsoOnboardingTicketBody body) {
        return createTicket(profileId, body, null);
    }

    public IssuedSsoOnboardingTicket createTicket(String profileId, CreateSsoOnboardingTicketBody body, String idempotencyKey) {
        return t.post("/v1/sso_onboarding_profiles/" + enc(profileId) + "/tickets", body, idempotencyKey,
                new TypeReference<>() {
                });
    }

    /** Kill a still-live ticket. 404 once it is used, expired, or already revoked. */
    public JsonNode revokeTicket(String ticketId) {
        return t.post("/v1/sso_onboarding_tickets/" + enc(ticketId) + "/revoke", null, new TypeReference<>() {
        });
    }

    /** {@code PATCH /v1/sso_onboarding_profiles/{id}} — only the fields sent change. */
    public SsoOnboardingProfile update(String id, Map<String, Object> body) {
        return t.patch("/v1/sso_onboarding_profiles/" + enc(id), body, new TypeReference<>() {
        });
    }

    /**
     * {@code PUT /v1/sso_onboarding_profiles/by_name/{name}} — create or update the profile with this exact name:
     * 201 when created, else 200. 409 {@code IDENTIFIER_EXISTS} when two profiles already share the name.
     */
    public SsoOnboardingProfile upsertByName(String name, Map<String, Object> body) {
        return t.put("/v1/sso_onboarding_profiles/by_name/" + enc(name), body, new TypeReference<>() {
        });
    }
}
