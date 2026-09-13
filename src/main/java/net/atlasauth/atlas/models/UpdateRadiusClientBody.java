package net.atlasauth.atlas.models;

/** Body for {@code PATCH /v1/radius_clients/:id}. */
public final class UpdateRadiusClientBody {
    public String name;
    public String nasIdentifier;
    /** Omit or send empty to keep the stored secret (write-only edit). */
    public String sharedSecret;
    public String ipAddress;
    public Boolean requireMessageAuthenticator;
    public Boolean enabled;

    public UpdateRadiusClientBody() {
    }

    public UpdateRadiusClientBody name(String v) { this.name = v; return this; }
    public UpdateRadiusClientBody nasIdentifier(String v) { this.nasIdentifier = v; return this; }
    public UpdateRadiusClientBody sharedSecret(String v) { this.sharedSecret = v; return this; }
    public UpdateRadiusClientBody ipAddress(String v) { this.ipAddress = v; return this; }
    public UpdateRadiusClientBody requireMessageAuthenticator(Boolean v) { this.requireMessageAuthenticator = v; return this; }
    public UpdateRadiusClientBody enabled(Boolean v) { this.enabled = v; return this; }
}
