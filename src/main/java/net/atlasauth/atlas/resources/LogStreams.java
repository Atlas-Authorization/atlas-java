package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.CreateLogStreamBody;
import net.atlasauth.atlas.models.DeletedObject;
import net.atlasauth.atlas.models.ListPage;
import net.atlasauth.atlas.models.LogStream;
import net.atlasauth.atlas.models.LogStreamTestResult;
import net.atlasauth.atlas.models.UpdateLogStreamBody;

import static net.atlasauth.atlas.http.Transport.enc;

/** The {@code /v1/log_streams} resource. */
public final class LogStreams {

    private final Transport t;

    public LogStreams(Transport t) {
        this.t = t;
    }

    public ListPage<LogStream> list() {
        return t.get("/v1/log_streams", new TypeReference<>() {
        });
    }

    public LogStream get(String id) {
        return t.get("/v1/log_streams/" + enc(id), new TypeReference<>() {
        });
    }

    public LogStream create(CreateLogStreamBody body) {
        return create(body, null);
    }

    public LogStream create(CreateLogStreamBody body, String idempotencyKey) {
        return t.post("/v1/log_streams", body, idempotencyKey, new TypeReference<>() {
        });
    }

    public LogStream update(String id, UpdateLogStreamBody body) {
        return t.patch("/v1/log_streams/" + enc(id), body, new TypeReference<>() {
        });
    }

    public DeletedObject delete(String id) {
        return t.delete("/v1/log_streams/" + enc(id), new TypeReference<>() {
        });
    }

    /** Send ONE synthetic event with the real credentials; the cursor is never touched. */
    public LogStreamTestResult test(String id) {
        return t.post("/v1/log_streams/" + enc(id) + "/test", null, new TypeReference<>() {
        });
    }
}
