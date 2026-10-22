package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.BeforeUserCreateHook;
import net.atlasauth.atlas.models.BeforeUserCreateHookTest;
import net.atlasauth.atlas.models.UpdateBeforeUserCreateHookBody;

/**
 * {@code /v1/instance/hooks} — the {@code before_user_create} hook. Just before Atlas creates a user
 * from a self-service sign-up, it sends your endpoint a signed {@code POST} (the webhook headers;
 * verify it with {@link net.atlasauth.atlas.webhook.WebhookVerifier}) of type {@code user.before_create}, with
 * {@code data} {@code {user, strategy, referral}}, and stamps the {@code external_id} and metadata you
 * answer. A slow or failing hook never blocks a sign-up. The signing secret is write-only.
 */
public final class UserHooks {
    private static final String PATH = "/v1/instance/hooks/before_user_create";

    private final Transport t;

    public UserHooks(Transport t) {
        this.t = t;
    }

    /** {@code GET /v1/instance/hooks/before_user_create} — the hook settings and its last call. */
    public BeforeUserCreateHook getBeforeUserCreate() {
        return t.get(PATH, new TypeReference<>() {
        });
    }

    /** {@code PATCH /v1/instance/hooks/before_user_create} — partial update. */
    public BeforeUserCreateHook updateBeforeUserCreate(UpdateBeforeUserCreateHookBody body) {
        return updateBeforeUserCreate(body, null);
    }

    /** {@link #updateBeforeUserCreate(UpdateBeforeUserCreateHookBody)} with an {@code Idempotency-Key}. */
    public BeforeUserCreateHook updateBeforeUserCreate(UpdateBeforeUserCreateHookBody body, String idempotencyKey) {
        return t.request("PATCH", PATH, null, body, idempotencyKey, false, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/instance/hooks/before_user_create/test} — send the hook a sample draft; nothing is stored. */
    public BeforeUserCreateHookTest testBeforeUserCreate() {
        return t.post(PATH + "/test", null, new TypeReference<>() {
        });
    }
}
