package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code POST /v1/network_zones/{id}/check}. */
public final class NetworkZonesCheckBody {
    public String ip;

    public NetworkZonesCheckBody() {
    }

    public NetworkZonesCheckBody ip(String v) { this.ip = v; return this; }
}
