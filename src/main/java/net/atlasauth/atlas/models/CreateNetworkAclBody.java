package net.atlasauth.atlas.models;

/** Body for {@code POST /v1/network_acls}. {@code action} is {@code "allow"} or {@code "deny"}. */
public final class CreateNetworkAclBody {
    public String action;
    public String cidr;
    public String description;
    public Integer priority;
    public Boolean enabled;

    public CreateNetworkAclBody() {
    }

    public CreateNetworkAclBody(String action, String cidr) {
        this.action = action;
        this.cidr = cidr;
    }

    public CreateNetworkAclBody action(String v) { this.action = v; return this; }
    public CreateNetworkAclBody cidr(String v) { this.cidr = v; return this; }
    public CreateNetworkAclBody description(String v) { this.description = v; return this; }
    public CreateNetworkAclBody priority(Integer v) { this.priority = v; return this; }
    public CreateNetworkAclBody enabled(Boolean v) { this.enabled = v; return this; }
}
