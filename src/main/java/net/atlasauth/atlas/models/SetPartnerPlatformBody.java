package net.atlasauth.atlas.models;

import java.util.List;

/** Body for {@code PUT /v1/partner_platforms/{partner}}: set up or change a partner platform (a preset fills the defaults). {@link #clear(String...)} sends {@code name} as null. */
public final class SetPartnerPlatformBody extends ExtensibleBody {
    public String name;
    public String platformId;
    public Boolean enabled;
    public String jwksUrl;
    public String signatureHeader;
    /** {@code t_kid_ed25519}. */
    public String signatureScheme;
    public Integer toleranceSeconds;
    public List<String> allowedSources;
    public Boolean recordConsents;
    public Boolean servesDeployment;

    public SetPartnerPlatformBody() {
    }

    public SetPartnerPlatformBody name(String v) { this.name = v; return this; }
    public SetPartnerPlatformBody platformId(String v) { this.platformId = v; return this; }
    public SetPartnerPlatformBody enabled(Boolean v) { this.enabled = v; return this; }
    public SetPartnerPlatformBody jwksUrl(String v) { this.jwksUrl = v; return this; }
    public SetPartnerPlatformBody signatureHeader(String v) { this.signatureHeader = v; return this; }
    public SetPartnerPlatformBody signatureScheme(String v) { this.signatureScheme = v; return this; }
    public SetPartnerPlatformBody toleranceSeconds(Integer v) { this.toleranceSeconds = v; return this; }
    public SetPartnerPlatformBody allowedSources(List<String> v) { this.allowedSources = v; return this; }
    public SetPartnerPlatformBody recordConsents(Boolean v) { this.recordConsents = v; return this; }
    public SetPartnerPlatformBody servesDeployment(Boolean v) { this.servesDeployment = v; return this; }

    /** Send the named nullable fields (wire names) as {@code null}, clearing the stored value. */
    public SetPartnerPlatformBody clear(String... keys) { putNulls(keys); return this; }

    /** Set any field by its wire name, including one this SDK does not model yet. */
    public SetPartnerPlatformBody set(String key, Object value) { putField(key, value); return this; }

}
