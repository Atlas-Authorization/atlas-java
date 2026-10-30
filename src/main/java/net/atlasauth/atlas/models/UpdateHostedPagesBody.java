package net.atlasauth.atlas.models;

/**
 * Body for {@code PATCH /v1/instance/hosted_pages}. Each URL is https on one of the instance's allowed
 * origins; the {@code clear...} methods send {@code null}, which serves the hosted page again.
 */
public final class UpdateHostedPagesBody extends ExtensibleBody {
    public String signInUrl;
    public String signUpUrl;
    public String userUrl;

    public UpdateHostedPagesBody() {
    }

    public UpdateHostedPagesBody signInUrl(String v) { this.signInUrl = v; additionalFields().remove("sign_in_url"); return this; }
    public UpdateHostedPagesBody signUpUrl(String v) { this.signUpUrl = v; additionalFields().remove("sign_up_url"); return this; }
    public UpdateHostedPagesBody userUrl(String v) { this.userUrl = v; additionalFields().remove("user_url"); return this; }
    public UpdateHostedPagesBody clearSignInUrl() { this.signInUrl = null; putNulls("sign_in_url"); return this; }
    public UpdateHostedPagesBody clearSignUpUrl() { this.signUpUrl = null; putNulls("sign_up_url"); return this; }
    public UpdateHostedPagesBody clearUserUrl() { this.userUrl = null; putNulls("user_url"); return this; }
    public UpdateHostedPagesBody set(String key, Object value) { putField(key, value); return this; }
}
