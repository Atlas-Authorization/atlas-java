package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code POST /v1/users/search/export}. */
public final class UsersSearchExportBody {
    /** A filter tree. A group is exactly one of &#123;"and":[...]&#125;, &#123;"or":[...]&#125;, &#123;"not":&#123;...&#125;&#125; (nest up to 6 levels, 50 conditions). A condition is &#123;"field","op","value"&#125;. Fields: id, username, first_name, last_name, external_id, locale, email, phone, created_at, updated_at, last_sign_in_at, password_updated_at, ban */
    public Map<String, Object> filter;
    /** Free text over id, names, username, emails and phones, ANDed with the filter. */
    public String query;
    /** created_at (default -created_at), updated_at, last_sign_in_at; prefix - for descending. */
    public String orderBy;
    public Boolean includeDeleted;
    public String format;
    public List<String> fields;

    public UsersSearchExportBody() {
    }

    public UsersSearchExportBody filter(Map<String, Object> v) { this.filter = v; return this; }
    public UsersSearchExportBody query(String v) { this.query = v; return this; }
    public UsersSearchExportBody orderBy(String v) { this.orderBy = v; return this; }
    public UsersSearchExportBody includeDeleted(Boolean v) { this.includeDeleted = v; return this; }
    public UsersSearchExportBody format(String v) { this.format = v; return this; }
    public UsersSearchExportBody fields(List<String> v) { this.fields = v; return this; }
}
