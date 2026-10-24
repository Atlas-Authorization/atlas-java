package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/** {@code GET /v1/delivery_health}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record DeliveryHealthList(
        List<DeliveryHealth> data,
        boolean hasMore,
        int unhealthyCount) {
}
