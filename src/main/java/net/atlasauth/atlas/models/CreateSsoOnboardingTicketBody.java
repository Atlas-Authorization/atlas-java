package net.atlasauth.atlas.models;

/** Body for {@code POST /v1/sso_onboarding_profiles/:id/tickets}. */
public final class CreateSsoOnboardingTicketBody {
    /** Required for an unbound profile; must match (or be omitted) for a bound one. */
    public String organizationId;
    /** Clamped to a 5-minute…30-day band; defaults to 3 days. */
    public Long expiresInSeconds;

    public CreateSsoOnboardingTicketBody() {
    }

    public CreateSsoOnboardingTicketBody organizationId(String v) { this.organizationId = v; return this; }
    public CreateSsoOnboardingTicketBody expiresInSeconds(Long v) { this.expiresInSeconds = v; return this; }
}
