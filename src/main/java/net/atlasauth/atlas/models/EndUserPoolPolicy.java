package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/** An organization's end-user (reader) pool policy. The wire keys are camelCase. */
@JsonIgnoreProperties(ignoreUnknown = true)
public final class EndUserPoolPolicy {
    /** Whether the organization has an end-user pool. */
    @JsonProperty("enabled")
    public Boolean enabled;
    /** New end users wait for approval before they can sign in. */
    @JsonProperty("requireApproval")
    public Boolean requireApproval;
    /** End users may sign up on the hosted pages. */
    @JsonProperty("allowHostedSignup")
    public Boolean allowHostedSignup;

    public EndUserPoolPolicy() {
    }

    public EndUserPoolPolicy(boolean enabled) {
        this.enabled = enabled;
    }

    public EndUserPoolPolicy enabled(Boolean v) { this.enabled = v; return this; }
    public EndUserPoolPolicy requireApproval(Boolean v) { this.requireApproval = v; return this; }
    public EndUserPoolPolicy allowHostedSignup(Boolean v) { this.allowHostedSignup = v; return this; }
}
