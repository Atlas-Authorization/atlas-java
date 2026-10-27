package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code POST /v1/iga/delegations}. Required: delegatorUserId, delegateUserId, endsAt. */
public final class IgaDelegationsCreateBody {
    /** Required. */
    public String delegatorUserId;
    /** Required. */
    public String delegateUserId;
    public Long startsAt;
    /** Required. */
    public Long endsAt;

    public IgaDelegationsCreateBody() {
    }

    public IgaDelegationsCreateBody delegatorUserId(String v) { this.delegatorUserId = v; return this; }
    public IgaDelegationsCreateBody delegateUserId(String v) { this.delegateUserId = v; return this; }
    public IgaDelegationsCreateBody startsAt(Long v) { this.startsAt = v; return this; }
    public IgaDelegationsCreateBody endsAt(Long v) { this.endsAt = v; return this; }
}
