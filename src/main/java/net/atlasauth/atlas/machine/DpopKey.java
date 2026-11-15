package net.atlasauth.atlas.machine;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.UUID;

/**
 * A DPoP (RFC 9449) key for a machine, and the proofs it signs. A token minted with a proof from this key
 * carries {@code cnf.jkt}, and a resource server accepts it only with a fresh proof from the same key, so a
 * stolen token is useless on its own. Ed25519 keys only. The JDK cannot derive a public key from a private
 * one, so a key is held as a pair; persist both PEMs.
 */
public final class DpopKey {

    private static final Base64.Encoder B64U = Base64.getUrlEncoder().withoutPadding();

    private final PrivateKey privateKey;
    private final PublicKey publicKey;
    private final String privatePem;
    private final String publicPem;

    private DpopKey(PrivateKey privateKey, PublicKey publicKey) {
        this.privateKey = privateKey;
        this.publicKey = publicKey;
        this.privatePem = pem("PRIVATE KEY", privateKey.getEncoded());
        this.publicPem = pem("PUBLIC KEY", publicKey.getEncoded());
    }

    /** A new Ed25519 key. */
    public static DpopKey generate() {
        try {
            KeyPair kp = KeyPairGenerator.getInstance("Ed25519").generateKeyPair();
            return new DpopKey(kp.getPrivate(), kp.getPublic());
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Ed25519 is not available in this JVM", e);
        }
    }

    /** Load a key from its PKCS#8 private PEM and SPKI public PEM. */
    public static DpopKey fromPem(String privatePem, String publicPem) {
        try {
            KeyFactory kf = KeyFactory.getInstance("Ed25519");
            return new DpopKey(kf.generatePrivate(new PKCS8EncodedKeySpec(unpem(privatePem))), kf.generatePublic(new X509EncodedKeySpec(unpem(publicPem))));
        } catch (GeneralSecurityException e) {
            throw new IllegalArgumentException("a DPoP key must be an Ed25519 PKCS#8 / SPKI PEM pair", e);
        }
    }

    public String privateKeyPem() {
        return privatePem;
    }

    public String publicKeyPem() {
        return publicPem;
    }

    /** The 32 raw public key bytes, base64url: the JWK {@code x}. */
    private String x() {
        byte[] der = publicKey.getEncoded();
        return B64U.encodeToString(Arrays.copyOfRange(der, der.length - 32, der.length));
    }

    /** The RFC 7638 thumbprint of the key: the {@code cnf.jkt} a token minted with its proofs carries. */
    public String thumbprint() {
        String canonical = "{\"crv\":\"Ed25519\",\"kty\":\"OKP\",\"x\":\"" + x() + "\"}";
        return B64U.encodeToString(sha256(canonical.getBytes(StandardCharsets.UTF_8)));
    }

    /**
     * Sign one DPoP proof for one request. {@code url} is the full request URL (query and fragment are dropped, as
     * the standard requires); pass {@code accessToken} when calling a resource server so the proof names the
     * token, and {@code nonce} when the server answered {@code use_dpop_nonce}.
     */
    public String proof(String method, String url, String accessToken, String nonce, long nowMs) {
        java.net.URI u = java.net.URI.create(url);
        String origin = u.getScheme() + "://" + u.getRawAuthority();
        String htu = origin + (u.getRawPath() == null ? "" : u.getRawPath());
        StringBuilder payload = new StringBuilder("{\"jti\":\"").append(UUID.randomUUID()).append("\",\"htm\":\"")
                .append(method.toUpperCase()).append("\",\"htu\":\"").append(jsonEscape(htu)).append("\",\"iat\":").append(nowMs / 1000);
        if (accessToken != null) {
            payload.append(",\"ath\":\"").append(B64U.encodeToString(sha256(accessToken.getBytes(StandardCharsets.UTF_8)))).append('"');
        }
        if (nonce != null) {
            payload.append(",\"nonce\":\"").append(jsonEscape(nonce)).append('"');
        }
        payload.append('}');
        String header = "{\"typ\":\"dpop+jwt\",\"alg\":\"EdDSA\",\"jwk\":{\"kty\":\"OKP\",\"crv\":\"Ed25519\",\"x\":\"" + x() + "\"}}";
        String input = B64U.encodeToString(header.getBytes(StandardCharsets.UTF_8)) + "." + B64U.encodeToString(payload.toString().getBytes(StandardCharsets.UTF_8));
        return input + "." + B64U.encodeToString(sign(privateKey, input.getBytes(StandardCharsets.US_ASCII)));
    }

    static byte[] sign(PrivateKey key, byte[] message) {
        try {
            Signature s = Signature.getInstance("Ed25519");
            s.initSign(key);
            s.update(message);
            return s.sign();
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException(e);
        }
    }

    static byte[] sha256(byte[] in) {
        try {
            return MessageDigest.getInstance("SHA-256").digest(in);
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException(e);
        }
    }

    static String pem(String label, byte[] der) {
        return "-----BEGIN " + label + "-----\n" + Base64.getMimeEncoder(64, "\n".getBytes(StandardCharsets.US_ASCII)).encodeToString(der)
                + "\n-----END " + label + "-----\n";
    }

    static byte[] unpem(String pem) {
        return Base64.getMimeDecoder().decode(pem.replaceAll("-----[A-Z ]+-----", "").replaceAll("\\s", ""));
    }

    /** A private key from a raw 32-byte Ed25519 seed (hex), for test vectors. */
    static PrivateKey privateKeyFromSeedHex(String seedHex) throws GeneralSecurityException {
        byte[] prefix = hexBytes("302e020100300506032b657004220420");
        byte[] seed = hexBytes(seedHex);
        byte[] der = Arrays.copyOf(prefix, prefix.length + seed.length);
        System.arraycopy(seed, 0, der, prefix.length, seed.length);
        return KeyFactory.getInstance("Ed25519").generatePrivate(new PKCS8EncodedKeySpec(der));
    }

    static byte[] hexBytes(String hex) {
        byte[] out = new byte[hex.length() / 2];
        for (int i = 0; i < out.length; i++) {
            out[i] = (byte) Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
        }
        return out;
    }

    private static String jsonEscape(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
