package net.atlasauth.atlas.models;

import java.util.Map;

/** Body for {@code PATCH /v1/scim_provisioning_targets/:id}. */
public final class UpdateScimProvisioningTargetBody {
    public String name;
    public String baseUrl;
    /** Write-only; omit to leave the stored bearer unchanged. */
    public String bearerToken;
    public Map<String, Object> attributeMapping;
    /** One of {@code deactivate}, {@code delete}. */
    public String deprovisionAction;
    public Boolean enabled;
    /** One of {@code active}, {@code paused}. */
    public String status;

    public UpdateScimProvisioningTargetBody() {
    }

    public UpdateScimProvisioningTargetBody name(String v) { this.name = v; return this; }
    public UpdateScimProvisioningTargetBody baseUrl(String v) { this.baseUrl = v; return this; }
    public UpdateScimProvisioningTargetBody bearerToken(String v) { this.bearerToken = v; return this; }
    public UpdateScimProvisioningTargetBody attributeMapping(Map<String, Object> v) { this.attributeMapping = v; return this; }
    public UpdateScimProvisioningTargetBody deprovisionAction(String v) { this.deprovisionAction = v; return this; }
    public UpdateScimProvisioningTargetBody enabled(Boolean v) { this.enabled = v; return this; }
    public UpdateScimProvisioningTargetBody status(String v) { this.status = v; return this; }
}
