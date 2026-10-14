package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.LinkedHashMap;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class CreateMachineEnrolmentTokenBody {
    public String organizationId;
    public Double maxUses;
    public Double expiresInSeconds;
    public Boolean requiresApproval;
    public Map<String, Object> data;
    public String ownerUserId;
    public Boolean singleSession;
    public Double maxMachines;

    @JsonIgnore
    private final Map<String, Object> additionalFields = new LinkedHashMap<>();

    public CreateMachineEnrolmentTokenBody() {
    }

    public CreateMachineEnrolmentTokenBody organizationId(String v) { this.organizationId = v; return this; }
    public CreateMachineEnrolmentTokenBody maxUses(Double v) { this.maxUses = v; return this; }
    public CreateMachineEnrolmentTokenBody expiresInSeconds(Double v) { this.expiresInSeconds = v; return this; }
    public CreateMachineEnrolmentTokenBody requiresApproval(Boolean v) { this.requiresApproval = v; return this; }
    public CreateMachineEnrolmentTokenBody data(Map<String, Object> v) { this.data = v; return this; }
    public CreateMachineEnrolmentTokenBody ownerUserId(String v) { this.ownerUserId = v; return this; }
    public CreateMachineEnrolmentTokenBody singleSession(Boolean v) { this.singleSession = v; return this; }
    public CreateMachineEnrolmentTokenBody maxMachines(Double v) { this.maxMachines = v; return this; }

    /**
     * Set any field by its wire name, including an explicit {@code null} (which
     * clears a stored value on a partial update) or a field this SDK does not model yet.
     */
    public CreateMachineEnrolmentTokenBody set(String key, Object value) { this.additionalFields.put(key, value); return this; }

    @JsonAnyGetter
    public Map<String, Object> additionalFields() { return additionalFields; }
}
