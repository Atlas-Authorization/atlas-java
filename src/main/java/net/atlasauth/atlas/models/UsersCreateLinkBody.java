package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code POST /v1/users/{id}/links}. Required: type. */
public final class UsersCreateLinkBody {
    /** Required. */
    public String type;
    /** One of the user's addresses. Default: the primary address. */
    public String emailAddressId;
    /** Default 3600. */
    public Long expiresInSeconds;

    public UsersCreateLinkBody() {
    }

    public UsersCreateLinkBody type(String v) { this.type = v; return this; }
    public UsersCreateLinkBody emailAddressId(String v) { this.emailAddressId = v; return this; }
    public UsersCreateLinkBody expiresInSeconds(Long v) { this.expiresInSeconds = v; return this; }
}
