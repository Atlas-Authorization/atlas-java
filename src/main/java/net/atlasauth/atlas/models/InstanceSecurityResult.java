package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/** A confirmed self-lockout returns the config plus a plain-language {@code warning}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record InstanceSecurityResult(
        String object,
        InstanceKillSwitches killSwitches,
        List<String> ipAllowlist,
        String warning) {
}
