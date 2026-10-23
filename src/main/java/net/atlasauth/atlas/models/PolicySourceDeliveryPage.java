package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/** A page of {@link PolicySourceDelivery}s. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record PolicySourceDeliveryPage(
        String object,
        List<PolicySourceDelivery> data,
        Boolean hasMore,
        String nextCursor) {
}
