package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code POST /v1/users/search}. */
public final class UsersSearchQueryBody {
    /** A filter tree. A group is exactly one of &#123;"and":[...]&#125;, &#123;"or":[...]&#125;, &#123;"not":&#123;...&#125;&#125; (nest up to 6 levels, 50 conditions). A condition is &#123;"field","op","value"&#125;. Fields: id, username, first_name, last_name, external_id, locale, email, phone, created_at, updated_at, last_sign_in_at, password_updated_at, ban */
    public Map<String, Object> filter;
    /** Free text over id, names, username, emails and phones, ANDed with the filter. */
    public String query;
    /** created_at (default -created_at), updated_at, last_sign_in_at; prefix - for descending. */
    public String orderBy;
    public Boolean includeDeleted;
    public Long limit;
    public String startingAfter;
    public Long offset;
    public Boolean includeTotalCount;

    public UsersSearchQueryBody() {
    }

    public UsersSearchQueryBody filter(Map<String, Object> v) { this.filter = v; return this; }
    public UsersSearchQueryBody query(String v) { this.query = v; return this; }
    public UsersSearchQueryBody orderBy(String v) { this.orderBy = v; return this; }
    public UsersSearchQueryBody includeDeleted(Boolean v) { this.includeDeleted = v; return this; }
    public UsersSearchQueryBody limit(Long v) { this.limit = v; return this; }
    public UsersSearchQueryBody startingAfter(String v) { this.startingAfter = v; return this; }
    public UsersSearchQueryBody offset(Long v) { this.offset = v; return this; }
    public UsersSearchQueryBody includeTotalCount(Boolean v) { this.includeTotalCount = v; return this; }
}
