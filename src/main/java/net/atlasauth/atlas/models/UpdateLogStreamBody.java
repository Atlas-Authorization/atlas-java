package net.atlasauth.atlas.models;

import java.util.List;
import java.util.Map;

/** Body for {@code PATCH /v1/log_streams/:id} — accepts everything create does except {@code type}, which is immutable. */
public final class UpdateLogStreamBody {
    public String name;
    /** Merged over the stored destination; a secret left out is kept. */
    public Map<String, Object> destination;
    public List<String> eventFilter;
    public Boolean enabled;
    /** {@code active} or {@code paused}. */
    public String status;

    public UpdateLogStreamBody() {
    }

    public UpdateLogStreamBody name(String v) { this.name = v; return this; }
    public UpdateLogStreamBody destination(Map<String, Object> v) { this.destination = v; return this; }
    public UpdateLogStreamBody eventFilter(List<String> v) { this.eventFilter = v; return this; }
    public UpdateLogStreamBody enabled(Boolean v) { this.enabled = v; return this; }
    public UpdateLogStreamBody status(String v) { this.status = v; return this; }
}
