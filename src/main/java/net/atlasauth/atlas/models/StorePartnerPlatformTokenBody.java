package net.atlasauth.atlas.models;

/**
 * Body for {@code POST /v1/partner_platforms/{partner}/token}: the token the partner handed over. Write-only:
 * it is stored encrypted and is never returned, and this class does not print it.
 */
public final class StorePartnerPlatformTokenBody extends ExtensibleBody {
    /** Write-only. */
    public String token;
    /** true replaces a token that is already stored. */
    public Boolean rotate;
    public String apiBaseUrl;

    public StorePartnerPlatformTokenBody() {
    }

    public StorePartnerPlatformTokenBody token(String v) { this.token = v; return this; }
    public StorePartnerPlatformTokenBody rotate(Boolean v) { this.rotate = v; return this; }
    public StorePartnerPlatformTokenBody apiBaseUrl(String v) { this.apiBaseUrl = v; return this; }

    /** Set any field by its wire name, including one this SDK does not model yet. */
    public StorePartnerPlatformTokenBody set(String key, Object value) { putField(key, value); return this; }

    @Override
    public String toString() {
        return "StorePartnerPlatformTokenBody[token=" + (token == null ? "unset" : "***") + ", rotate=" + rotate + "]";
    }
}
