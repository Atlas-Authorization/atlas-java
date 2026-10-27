package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code POST /v1/least_privilege/apply}. */
public final class LeastPrivilegeApplyBody {
    public String subjectType;
    public String subjectId;
    public String digest;
    public Long days;
    public Boolean acknowledgeShortWindow;

    public LeastPrivilegeApplyBody() {
    }

    public LeastPrivilegeApplyBody subjectType(String v) { this.subjectType = v; return this; }
    public LeastPrivilegeApplyBody subjectId(String v) { this.subjectId = v; return this; }
    public LeastPrivilegeApplyBody digest(String v) { this.digest = v; return this; }
    public LeastPrivilegeApplyBody days(Long v) { this.days = v; return this; }
    public LeastPrivilegeApplyBody acknowledgeShortWindow(Boolean v) { this.acknowledgeShortWindow = v; return this; }
}
