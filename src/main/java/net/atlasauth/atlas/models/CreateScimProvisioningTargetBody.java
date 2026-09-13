package net.atlasauth.atlas.models;

import java.util.Map;

/** Body for {@code POST /v1/scim_provisioning_targets}. */
public final class CreateScimProvisioningTargetBody {
    public String name;
    /** Must be an https:// URL so the bearer never travels in cleartext. */
    public String baseUrl;
    /** Write-only; stored encrypted, never returned. */
    public String bearerToken;
    public Map<String, Object> attributeMapping;
    /** One of {@code deactivate}, {@code delete}. */
    public String deprovisionAction;
    public Boolean enabled;

    public CreateScimProvisioningTargetBody() {
    }

    public CreateScimProvisioningTargetBody(String name, String baseUrl, String bearerToken) {
        this.name = name;
        this.baseUrl = baseUrl;
        this.bearerToken = bearerToken;
    }

    public CreateScimProvisioningTargetBody name(String v) { this.name = v; return this; }
    public CreateScimProvisioningTargetBody baseUrl(String v) { this.baseUrl = v; return this; }
    public CreateScimProvisioningTargetBody bearerToken(String v) { this.bearerToken = v; return this; }
    public CreateScimProvisioningTargetBody attributeMapping(Map<String, Object> v) { this.attributeMapping = v; return this; }
    public CreateScimProvisioningTargetBody deprovisionAction(String v) { this.deprovisionAction = v; return this; }
    public CreateScimProvisioningTargetBody enabled(Boolean v) { this.enabled = v; return this; }
}
