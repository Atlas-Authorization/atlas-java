package net.atlasauth.atlas.models;

/** Body for {@code POST /v1/radius_clients}. */
public final class CreateRadiusClientBody {
    public String name;
    public String nasIdentifier;
    public String sharedSecret;
    /** An optional exact source-IP match — a single address, not a CIDR range. */
    public String ipAddress;
    public Boolean requireMessageAuthenticator;
    public Boolean enabled;

    public CreateRadiusClientBody() {
    }

    public CreateRadiusClientBody(String name, String nasIdentifier, String sharedSecret) {
        this.name = name;
        this.nasIdentifier = nasIdentifier;
        this.sharedSecret = sharedSecret;
    }

    public CreateRadiusClientBody name(String v) { this.name = v; return this; }
    public CreateRadiusClientBody nasIdentifier(String v) { this.nasIdentifier = v; return this; }
    public CreateRadiusClientBody sharedSecret(String v) { this.sharedSecret = v; return this; }
    public CreateRadiusClientBody ipAddress(String v) { this.ipAddress = v; return this; }
    public CreateRadiusClientBody requireMessageAuthenticator(Boolean v) { this.requireMessageAuthenticator = v; return this; }
    public CreateRadiusClientBody enabled(Boolean v) { this.enabled = v; return this; }
}
