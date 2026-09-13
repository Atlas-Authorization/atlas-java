package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.CreateLtiPlatformBody;
import net.atlasauth.atlas.models.DeletedObject;
import net.atlasauth.atlas.models.ListPage;
import net.atlasauth.atlas.models.LtiPlatform;
import net.atlasauth.atlas.models.UpdateLtiPlatformBody;

import static net.atlasauth.atlas.http.Transport.enc;

/** The {@code /v1/lti_platforms} resource (LTI 1.3 platform registrations). */
public final class LtiPlatforms {

    private final Transport t;

    public LtiPlatforms(Transport t) {
        this.t = t;
    }

    public ListPage<LtiPlatform> list() {
        return t.get("/v1/lti_platforms", new TypeReference<>() {
        });
    }

    public LtiPlatform get(String id) {
        return t.get("/v1/lti_platforms/" + enc(id), new TypeReference<>() {
        });
    }

    public LtiPlatform create(CreateLtiPlatformBody body) {
        return create(body, null);
    }

    public LtiPlatform create(CreateLtiPlatformBody body, String idempotencyKey) {
        return t.post("/v1/lti_platforms", body, idempotencyKey, new TypeReference<>() {
        });
    }

    public LtiPlatform update(String id, UpdateLtiPlatformBody body) {
        return t.patch("/v1/lti_platforms/" + enc(id), body, new TypeReference<>() {
        });
    }

    public DeletedObject delete(String id) {
        return t.delete("/v1/lti_platforms/" + enc(id), new TypeReference<>() {
        });
    }
}
