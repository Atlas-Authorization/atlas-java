package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code PUT /v1/iga/settings}. */
public final class IgaLifecycleUpdateSettingsBody {
    public Boolean jmlEnabled;
    public String leaverAction;
    public Long leaverCloseGraceSeconds;

    public IgaLifecycleUpdateSettingsBody() {
    }

    public IgaLifecycleUpdateSettingsBody jmlEnabled(Boolean v) { this.jmlEnabled = v; return this; }
    public IgaLifecycleUpdateSettingsBody leaverAction(String v) { this.leaverAction = v; return this; }
    public IgaLifecycleUpdateSettingsBody leaverCloseGraceSeconds(Long v) { this.leaverCloseGraceSeconds = v; return this; }
}
