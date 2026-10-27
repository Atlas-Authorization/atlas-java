package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code POST /v1/groups/{id}/members}. Required: userIds. */
public final class GroupsAddMembersBody {
    /** Required. */
    public List<String> userIds;

    public GroupsAddMembersBody() {
    }

    public GroupsAddMembersBody userIds(List<String> v) { this.userIds = v; return this; }
}
