package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DnsInstruction(
        String role,
        String host,
        String type,
        String target,
        String purpose) {
}
