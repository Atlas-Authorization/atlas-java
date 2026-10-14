package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.LinkedHashMap;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class RedeemTicketBody {
    /** The opaque `tkt_…` secret to redeem. */
    public String ticket;
    /** The user this redemption is on behalf of, matched against bound_user. */
    public String forUser;
    /** The organization this redemption is for, matched against bound_org. */
    public String forOrg;

    @JsonIgnore
    private final Map<String, Object> additionalFields = new LinkedHashMap<>();

    public RedeemTicketBody() {
    }

    public RedeemTicketBody ticket(String v) { this.ticket = v; return this; }
    public RedeemTicketBody forUser(String v) { this.forUser = v; return this; }
    public RedeemTicketBody forOrg(String v) { this.forOrg = v; return this; }

    /**
     * Set any field by its wire name, including an explicit {@code null} (which
     * clears a stored value on a partial update) or a field this SDK does not model yet.
     */
    public RedeemTicketBody set(String key, Object value) { this.additionalFields.put(key, value); return this; }

    @JsonAnyGetter
    public Map<String, Object> additionalFields() { return additionalFields; }
}
