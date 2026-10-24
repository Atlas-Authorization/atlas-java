package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/** {@code GET /v1/meta/egress_ips}: the addresses deliveries leave from. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record EgressIps(
        String object,
        /** false when this deployment does not send from fixed addresses. */ @JsonProperty("static") boolean staticAddresses,
        List<String> ipv4,
        List<String> ipv6,
        List<String> appliesTo,
        String note) {
}
