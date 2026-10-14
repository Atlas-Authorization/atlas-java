package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class FindUsersByEmailsBody {
    /** Up to 1000 addresses, deduped. Case-insensitive. */
    public List<String> emailAddresses;
    /** Default false (verified only). Set true for rights fulfilment / DSAR to also match unverified addresses; each result is flagged with `verified`. */
    public Boolean includeUnverified;
    /** Default false (live users only). Set true to also match soft-deleted users; each such result is flagged with `deleted:true`. */
    public Boolean includeDeleted;

    @JsonIgnore
    private final Map<String, Object> additionalFields = new LinkedHashMap<>();

    public FindUsersByEmailsBody() {
    }

    public FindUsersByEmailsBody emailAddresses(List<String> v) { this.emailAddresses = v; return this; }
    public FindUsersByEmailsBody includeUnverified(Boolean v) { this.includeUnverified = v; return this; }
    public FindUsersByEmailsBody includeDeleted(Boolean v) { this.includeDeleted = v; return this; }

    /**
     * Set any field by its wire name, including an explicit {@code null} (which
     * clears a stored value on a partial update) or a field this SDK does not model yet.
     */
    public FindUsersByEmailsBody set(String key, Object value) { this.additionalFields.put(key, value); return this; }

    @JsonAnyGetter
    public Map<String, Object> additionalFields() { return additionalFields; }
}
