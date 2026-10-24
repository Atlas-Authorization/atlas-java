package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code POST /v1/partner_links}: a credential an integrated partner service holds on this instance. */
public final class CreatePartnerLinkBody extends ExtensibleBody {
    /** Any lowercase id; cookiemunch, chatly, forumulate and referalli are presets. */
    public String partner;
    public String name;
    public List<String> scopes;
    /** Unix ms. */
    public Long expiresAt;

    public CreatePartnerLinkBody() {
    }

    public CreatePartnerLinkBody partner(String v) { this.partner = v; return this; }
    public CreatePartnerLinkBody name(String v) { this.name = v; return this; }
    public CreatePartnerLinkBody scopes(List<String> v) { this.scopes = v; return this; }
    public CreatePartnerLinkBody expiresAt(Long v) { this.expiresAt = v; return this; }

    /** Set any field by its wire name, including one this SDK does not model yet. */
    public CreatePartnerLinkBody set(String key, Object value) { putField(key, value); return this; }

}
