package net.atlasauth.atlas;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Vercel BotID for Atlas: the signing half that runs next to your BotID check.
 *
 * <p>BotID's verdict ({@code checkBotId()}) only exists inside a Vercel-hosted request, so
 * that route turns it into a short signed attestation; the browser sends it to Atlas as
 * {@code captcha_token}, and Atlas checks the signature, freshness and {@code is_bot == false},
 * and spends it (each attestation passes once). The same secret is stored in Atlas for the
 * {@code vercel_botid} captcha provider.
 *
 * <p>Format (version 1): {@code atlas_botid.v1.<payload>.<sig>} — {@code payload} is
 * base64url JSON {@code {v: 1, is_bot, verified_bot, iat, exp, jti}}; {@code sig} is
 * base64url HMAC-SHA256 over {@code atlas_botid.v1.<payload>} keyed by the shared secret.
 */
public final class BotId {

    public static final String PREFIX = "atlas_botid.v1.";
    public static final int MAX_TTL_SECONDS = 300;
    public static final int SECRET_MIN_LENGTH = 32;

    private static final SecureRandom RANDOM = new SecureRandom();

    private BotId() {
    }

    /** Sign a verdict with a 60 s lifetime. */
    public static String createAttestation(String secret, boolean isBot, boolean isVerifiedBot) {
        return createAttestation(secret, isBot, isVerifiedBot, 60, System.currentTimeMillis() / 1000L);
    }

    /**
     * Sign a BotID verdict for Atlas. Throws when the secret is shorter than 32 characters.
     * A bot verdict is still signed — Atlas refuses it. {@code ttlSeconds} is clamped to 1–300.
     */
    public static String createAttestation(String secret, boolean isBot, boolean isVerifiedBot, int ttlSeconds,
                                           long nowSeconds) {
        if (secret == null || secret.length() < SECRET_MIN_LENGTH) {
            throw new IllegalArgumentException(
                    "The BotID attestation secret must be at least " + SECRET_MIN_LENGTH + " characters.");
        }
        int ttl = Math.max(1, Math.min(ttlSeconds, MAX_TTL_SECONDS));
        byte[] jti = new byte[18];
        RANDOM.nextBytes(jti);
        String json = "{\"v\":1,\"is_bot\":" + isBot + ",\"verified_bot\":" + isVerifiedBot + ",\"iat\":" + nowSeconds
                + ",\"exp\":" + (nowSeconds + ttl) + ",\"jti\":\"" + b64u(jti) + "\"}";
        String payload = b64u(json.getBytes(StandardCharsets.UTF_8));
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] sig = mac.doFinal((PREFIX + payload).getBytes(StandardCharsets.UTF_8));
            return PREFIX + payload + "." + b64u(sig);
        } catch (java.security.GeneralSecurityException e) {
            throw new IllegalStateException(e);
        }
    }

    private static String b64u(byte[] raw) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(raw);
    }
}
