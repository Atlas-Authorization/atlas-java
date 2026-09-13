package net.atlasauth.atlas.models;

import java.util.List;

/** Body for {@code PUT /v1/managed_waf}; omitted fields are left unchanged. */
public final class UpdateManagedWafBody {
    public Boolean enabled;
    /** {@code "REGIONAL"} or {@code "CLOUDFRONT"}. */
    public String scope;
    public String region;
    public String webAclName;
    public String edgeResourceArn;
    public String cloudfrontDistributionId;
    public List<String> tokenDomains;
    public List<String> gatedPaths;
    /** 60..86400 seconds. */
    public Integer immunitySeconds;
    public ManagedWafCredentialBody credential;

    public UpdateManagedWafBody enabled(Boolean v) { this.enabled = v; return this; }
    public UpdateManagedWafBody scope(String v) { this.scope = v; return this; }
    public UpdateManagedWafBody region(String v) { this.region = v; return this; }
    public UpdateManagedWafBody webAclName(String v) { this.webAclName = v; return this; }
    public UpdateManagedWafBody edgeResourceArn(String v) { this.edgeResourceArn = v; return this; }
    public UpdateManagedWafBody cloudfrontDistributionId(String v) { this.cloudfrontDistributionId = v; return this; }
    public UpdateManagedWafBody tokenDomains(List<String> v) { this.tokenDomains = v; return this; }
    public UpdateManagedWafBody gatedPaths(List<String> v) { this.gatedPaths = v; return this; }
    public UpdateManagedWafBody immunitySeconds(Integer v) { this.immunitySeconds = v; return this; }
    public UpdateManagedWafBody credential(ManagedWafCredentialBody v) { this.credential = v; return this; }
}
