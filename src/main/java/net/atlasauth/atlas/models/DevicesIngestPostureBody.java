package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code POST /v1/device_posture}. */
public final class DevicesIngestPostureBody {
    public List<Map<String, Object>> devices;

    public DevicesIngestPostureBody() {
    }

    public DevicesIngestPostureBody devices(List<Map<String, Object>> v) { this.devices = v; return this; }
}
