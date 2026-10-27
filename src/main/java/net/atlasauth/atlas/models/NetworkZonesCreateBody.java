package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code POST /v1/network_zones}. */
public final class NetworkZonesCreateBody {
    public String name;
    public String description;
    public List<String> cidrs;
    public List<String> countries;
    public List<Long> asns;
    public List<String> anonymizers;
    public Boolean enabled;

    public NetworkZonesCreateBody() {
    }

    public NetworkZonesCreateBody name(String v) { this.name = v; return this; }
    public NetworkZonesCreateBody description(String v) { this.description = v; return this; }
    public NetworkZonesCreateBody cidrs(List<String> v) { this.cidrs = v; return this; }
    public NetworkZonesCreateBody countries(List<String> v) { this.countries = v; return this; }
    public NetworkZonesCreateBody asns(List<Long> v) { this.asns = v; return this; }
    public NetworkZonesCreateBody anonymizers(List<String> v) { this.anonymizers = v; return this; }
    public NetworkZonesCreateBody enabled(Boolean v) { this.enabled = v; return this; }
}
