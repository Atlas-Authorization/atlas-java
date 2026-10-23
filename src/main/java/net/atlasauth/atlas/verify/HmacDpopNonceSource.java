package net.atlasauth.atlas.verify;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.function.LongSupplier;

/**
 * DPoP nonces derived from a secret and the clock: a nonce is good for the window it was issued in
 * and the next one, so every instance sharing the secret agrees without shared state.
 */
public final class HmacDpopNonceSource implements DpopNonceSource {

    private final byte[] secret;
    private final long windowMillis;
    private final LongSupplier now;

    /** @param secret at least 32 bytes; nonces rotate every 5 minutes */
    public HmacDpopNonceSource(byte[] secret) {
        this(secret, 300, System::currentTimeMillis);
    }

    /**
     * @param secret        at least 32 bytes
     * @param windowSeconds how long one nonce is issued for
     * @param now           clock returning epoch milliseconds
     */
    public HmacDpopNonceSource(byte[] secret, long windowSeconds, LongSupplier now) {
        if (secret == null || secret.length < 32) {
            throw new IllegalArgumentException("The DPoP nonce secret must be at least 32 bytes.");
        }
        if (windowSeconds <= 0) {
            throw new IllegalArgumentException("The DPoP nonce window must be positive.");
        }
        this.secret = secret.clone();
        this.windowMillis = windowSeconds * 1000L;
        this.now = now != null ? now : System::currentTimeMillis;
    }

    @Override
    public String current() {
        return at(epoch());
    }

    @Override
    public boolean valid(String nonce) {
        if (nonce == null) {
            return false;
        }
        long e = epoch();
        byte[] got = nonce.getBytes(StandardCharsets.US_ASCII);
        return MessageDigest.isEqual(at(e).getBytes(StandardCharsets.US_ASCII), got)
                || MessageDigest.isEqual(at(e - 1).getBytes(StandardCharsets.US_ASCII), got);
    }

    private long epoch() {
        return Math.floorDiv(now.getAsLong(), windowMillis);
    }

    private String at(long epoch) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret, "HmacSHA256"));
            mac.update("atlas-dpop-nonce:".getBytes(StandardCharsets.US_ASCII));
            mac.update(ByteBuffer.allocate(8).putLong(epoch).array());
            return Base64.getUrlEncoder().withoutPadding().encodeToString(mac.doFinal());
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException(e);
        }
    }
}
