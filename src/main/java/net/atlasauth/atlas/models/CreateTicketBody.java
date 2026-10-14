package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.LinkedHashMap;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class CreateTicketBody {
    /** Arbitrary JSON returned to whoever redeems the ticket. Defaults to {}. */
    public Object payload;
    /** Ticket lifetime in seconds. Must be positive; clamped to a maximum of 86400 (24h). */
    public Long ttlSeconds;
    /** When true (default), the first successful redeem consumes the ticket and all later redeems are refused. */
    public Boolean singleUse;
    /** If set, only a redeemer acting as this user may redeem (FAPI: the session user; BAPI: for_user). */
    public String boundUser;
    /** If set, only a redeemer acting for this organization may redeem (FAPI: a proven member; BAPI: for_org). */
    public String boundOrg;

    @JsonIgnore
    private final Map<String, Object> additionalFields = new LinkedHashMap<>();

    public CreateTicketBody() {
    }

    public CreateTicketBody payload(Object v) { this.payload = v; return this; }
    public CreateTicketBody ttlSeconds(Long v) { this.ttlSeconds = v; return this; }
    public CreateTicketBody singleUse(Boolean v) { this.singleUse = v; return this; }
    public CreateTicketBody boundUser(String v) { this.boundUser = v; return this; }
    public CreateTicketBody boundOrg(String v) { this.boundOrg = v; return this; }

    /**
     * Set any field by its wire name, including an explicit {@code null} (which
     * clears a stored value on a partial update) or a field this SDK does not model yet.
     */
    public CreateTicketBody set(String key, Object value) { this.additionalFields.put(key, value); return this; }

    @JsonAnyGetter
    public Map<String, Object> additionalFields() { return additionalFields; }
}
