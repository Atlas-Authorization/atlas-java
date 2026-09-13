package net.atlasauth.atlas.models;

import java.util.List;

/** Body for {@code POST /v1/sso_onboarding_profiles}. */
public final class CreateSsoOnboardingProfileBody {
    public String name;
    /** Defaults to both {@code oidc} and {@code saml}. */
    public List<String> allowedConnectionTypes;
    /** Bind the profile to an org in this instance; omit for a per-ticket org. */
    public String organizationId;
    public String companyName;
    public Boolean allowScim;

    public CreateSsoOnboardingProfileBody() {
    }

    public CreateSsoOnboardingProfileBody(String name) {
        this.name = name;
    }

    public CreateSsoOnboardingProfileBody name(String v) { this.name = v; return this; }
    public CreateSsoOnboardingProfileBody allowedConnectionTypes(List<String> v) { this.allowedConnectionTypes = v; return this; }
    public CreateSsoOnboardingProfileBody organizationId(String v) { this.organizationId = v; return this; }
    public CreateSsoOnboardingProfileBody companyName(String v) { this.companyName = v; return this; }
    public CreateSsoOnboardingProfileBody allowScim(Boolean v) { this.allowScim = v; return this; }
}
