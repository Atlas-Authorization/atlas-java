package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.CaptchaVerification;
import net.atlasauth.atlas.models.VerifyCaptchaBody;

import java.util.List;
import java.util.Map;

import static net.atlasauth.atlas.http.Transport.enc;

/** Server-side captcha verification ({@code /v1/captcha/verify}). */
public final class Captcha {

    private final Transport t;

    public Captcha(Transport t) {
        this.t = t;
    }

    /** {@code POST /v1/captcha/verify} — §5 "captcha anywhere": verify a CAPTCHA token server-side against the instance's centrally-configured provider. */
    public CaptchaVerification verify(VerifyCaptchaBody body) {
        return t.post("/v1/captcha/verify", body, new TypeReference<>() {
        });
    }

    /** As above, with an {@code Idempotency-Key} so a retry replays the first response. */
    public CaptchaVerification verify(VerifyCaptchaBody body, String idempotencyKey) {
        return t.post("/v1/captcha/verify", body, idempotencyKey, new TypeReference<>() {
        });
    }
}
