package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code PUT /v1/oauth_clients/{id}/profile}. Required: profile. */
public final class OAuthClientsSetProfileBody {
    /** A built-in or custom profile name; null clears. Required. */
    public String profile;

    public OAuthClientsSetProfileBody() {
    }

    public OAuthClientsSetProfileBody profile(String v) { this.profile = v; return this; }
}
