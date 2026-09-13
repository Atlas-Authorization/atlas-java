package net.atlasauth.atlas.models;

import java.util.List;

/**
 * Body for {@code POST /v1/tokens/verify}. {@code token} is required;
 * {@code authorizedParties} holds the expected {@code azp} values when the token
 * carries an authorized-party claim. Fluent setters return {@code this}.
 */
public final class VerifyTokenBody {
    public String token;
    public List<String> authorizedParties;

    public VerifyTokenBody() {
    }

    public VerifyTokenBody(String token) {
        this.token = token;
    }

    public VerifyTokenBody token(String v) { this.token = v; return this; }
    public VerifyTokenBody authorizedParties(List<String> v) { this.authorizedParties = v; return this; }
}
