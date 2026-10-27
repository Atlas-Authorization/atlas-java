package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code POST /v1/desktop_mfa/enrolment_codes}. */
public final class DesktopMfaCreateEnrolmentCodeBody {
    public String userId;
    public Double expiresInMinutes;

    public DesktopMfaCreateEnrolmentCodeBody() {
    }

    public DesktopMfaCreateEnrolmentCodeBody userId(String v) { this.userId = v; return this; }
    public DesktopMfaCreateEnrolmentCodeBody expiresInMinutes(Double v) { this.expiresInMinutes = v; return this; }
}
