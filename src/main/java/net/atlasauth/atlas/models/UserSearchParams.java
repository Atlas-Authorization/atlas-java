package net.atlasauth.atlas.models;

import net.atlasauth.atlas.Params;

import java.util.Map;

/**
 * Search and filters for {@code GET /v1/users}. Every filter combines with AND. Page by cursor
 * ({@link #startingAfter}) or, with {@link #offset}, by offset. Unset fields are not sent.
 */
public final class UserSearchParams {
    public Integer limit;
    public String startingAfter;
    public Integer offset;
    /** Free-text search over email, username, names, phone and ids. */
    public String query;
    public String emailAddress;
    public String username;
    public String phoneNumber;
    public String externalId;
    /** Banned right now (a lapsed timed ban does not count). */
    public Boolean banned;
    public Boolean emailVerified;
    public String organizationId;
    /** Epoch ms. */
    public Long createdAfter;
    /** Epoch ms. */
    public Long createdBefore;
    /** created_at, -created_at, last_sign_in_at or -last_sign_in_at. */
    public String orderBy;
    /** Adds {@code total_count} to the page. */
    public Boolean includeTotalCount;

    /** Epoch ms: only users updated after this moment (an incremental sync). */
    public Long updatedAfter;
    /** Epoch ms. */
    public Long updatedBefore;

    public UserSearchParams() {
    }

    public UserSearchParams limit(Integer v) { this.limit = v; return this; }
    public UserSearchParams startingAfter(String v) { this.startingAfter = v; return this; }
    public UserSearchParams offset(Integer v) { this.offset = v; return this; }
    public UserSearchParams query(String v) { this.query = v; return this; }
    public UserSearchParams emailAddress(String v) { this.emailAddress = v; return this; }
    public UserSearchParams username(String v) { this.username = v; return this; }
    public UserSearchParams phoneNumber(String v) { this.phoneNumber = v; return this; }
    public UserSearchParams externalId(String v) { this.externalId = v; return this; }
    public UserSearchParams banned(Boolean v) { this.banned = v; return this; }
    public UserSearchParams emailVerified(Boolean v) { this.emailVerified = v; return this; }
    public UserSearchParams organizationId(String v) { this.organizationId = v; return this; }
    public UserSearchParams createdAfter(Long v) { this.createdAfter = v; return this; }
    public UserSearchParams createdBefore(Long v) { this.createdBefore = v; return this; }
    public UserSearchParams orderBy(String v) { this.orderBy = v; return this; }
    public UserSearchParams includeTotalCount(Boolean v) { this.includeTotalCount = v; return this; }

    public UserSearchParams updatedAfter(Long v) { this.updatedAfter = v; return this; }
    public UserSearchParams updatedBefore(Long v) { this.updatedBefore = v; return this; }

    /** The query string this search sends. */
    public Map<String, Object> toQuery() {
        return Params.of()
                .set("limit", limit)
                .set("starting_after", startingAfter)
                .set("offset", offset)
                .set("query", query)
                .set("email_address", emailAddress)
                .set("username", username)
                .set("phone_number", phoneNumber)
                .set("external_id", externalId)
                .set("banned", banned == null ? null : banned.toString())
                .set("email_verified", emailVerified == null ? null : emailVerified.toString())
                .set("organization_id", organizationId)
                .set("created_after", createdAfter)
                .set("created_before", createdBefore)
                .set("order_by", orderBy)
                .set("include_total_count", includeTotalCount == null ? null : includeTotalCount.toString())
                .set("updated_after", updatedAfter)
                .set("updated_before", updatedBefore)
                .map();
    }
}
