package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code POST /v1/connections/{id}/credentials/rotate}. */
public final class ConnectionsRotateCredentialsBody {
    public Long overlapSeconds;
    /** Epoch milliseconds or an ISO date. */
    public Object expiresAt;

    public ConnectionsRotateCredentialsBody() {
    }

    public ConnectionsRotateCredentialsBody overlapSeconds(Long v) { this.overlapSeconds = v; return this; }
    public ConnectionsRotateCredentialsBody expiresAt(Object v) { this.expiresAt = v; return this; }
}
