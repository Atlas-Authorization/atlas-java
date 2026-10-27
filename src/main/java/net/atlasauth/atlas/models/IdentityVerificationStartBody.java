package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code POST /v1/users/{id}/identity_verifications}. */
public final class IdentityVerificationStartBody {
    public String redirectUrl;

    public IdentityVerificationStartBody() {
    }

    public IdentityVerificationStartBody redirectUrl(String v) { this.redirectUrl = v; return this; }
}
