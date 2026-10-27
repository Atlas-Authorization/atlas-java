package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code PATCH /v1/users/search/views/{id}}. */
public final class UsersSearchViewsUpdateBody {
    /** Unique per instance (max 100 characters). */
    public String name;
    /** A filter tree. A group is exactly one of &#123;"and":[...]&#125;, &#123;"or":[...]&#125;, &#123;"not":&#123;...&#125;&#125; (nest up to 6 levels, 50 conditions). A condition is &#123;"field","op","value"&#125;. Fields: id, username, first_name, last_name, external_id, locale, email, phone, created_at, updated_at, last_sign_in_at, password_updated_at, ban */
    public Map<String, Object> filter;
    /** One of created_at, updated_at, last_sign_in_at, optionally prefixed with - for descending. */
    public String orderBy;

    public UsersSearchViewsUpdateBody() {
    }

    public UsersSearchViewsUpdateBody name(String v) { this.name = v; return this; }
    public UsersSearchViewsUpdateBody filter(Map<String, Object> v) { this.filter = v; return this; }
    public UsersSearchViewsUpdateBody orderBy(String v) { this.orderBy = v; return this; }
}
