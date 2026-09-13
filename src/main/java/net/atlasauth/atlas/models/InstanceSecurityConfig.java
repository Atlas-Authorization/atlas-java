package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * Instance security config that lives OUTSIDE {@code auth_config}: the per-flow
 * kill switches and the customer IP allowlist. No secret is ever readable back.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record InstanceSecurityConfig(
        String object,
        InstanceKillSwitches killSwitches,
        List<String> ipAllowlist) {
}
