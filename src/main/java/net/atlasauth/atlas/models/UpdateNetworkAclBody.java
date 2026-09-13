package net.atlasauth.atlas.models;

/** Body for {@code PATCH /v1/network_acls/:id}. {@code action} is {@code "allow"} or {@code "deny"}. */
public final class UpdateNetworkAclBody {
    public String action;
    public String cidr;
    public String description;
    public Integer priority;
    public Boolean enabled;

    public UpdateNetworkAclBody action(String v) { this.action = v; return this; }
    public UpdateNetworkAclBody cidr(String v) { this.cidr = v; return this; }
    public UpdateNetworkAclBody description(String v) { this.description = v; return this; }
    public UpdateNetworkAclBody priority(Integer v) { this.priority = v; return this; }
    public UpdateNetworkAclBody enabled(Boolean v) { this.enabled = v; return this; }
}
