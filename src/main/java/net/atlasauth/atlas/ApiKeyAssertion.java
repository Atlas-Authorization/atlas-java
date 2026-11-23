package net.atlasauth.atlas;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.interfaces.ECKey;
import java.security.PublicKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Signs the short-lived assertion a key-pair API key presents instead of a secret: ES256 for P-256
 * keys (raw {@code r || s}), EdDSA for Ed25519 keys.
 */
public final class ApiKeyAssertion {
    private static final ObjectMapper JSON = new ObjectMapper();
    private static final Base64.Encoder B64 = Base64.getUrlEncoder().withoutPadding();

    private ApiKeyAssertion() {
    }

    /** Sign with the default 300 second lifetime and a random {@code jti}. */
    public static String sign(String privateKeyPem, String keyId, String audience) {
        return sign(privateKeyPem, keyId, audience, 300, null, null);
    }

    /**
     * @param privateKeyPem the PKCS#8 PEM Atlas returned once at create
     * @param scope         optional space-separated scopes narrowing the key's own; null for none
     * @param jti           null for a random id
     */
    public static String sign(String privateKeyPem, String keyId, String audience, long ttlSeconds, String scope, String jti) {
        try {
            byte[] der = Base64.getMimeDecoder().decode(privateKeyPem.replaceAll("-----[A-Z ]+-----", ""));
            PrivateKey key;
            try {
                key = KeyFactory.getInstance("EC").generatePrivate(new PKCS8EncodedKeySpec(der));
            } catch (GeneralSecurityException notEc) {
                key = KeyFactory.getInstance("Ed25519").generatePrivate(new PKCS8EncodedKeySpec(der));
            }
            boolean ec = key instanceof ECKey;
            if (jti == null) {
                byte[] r = new byte[16];
                new SecureRandom().nextBytes(r);
                jti = B64.encodeToString(r);
            }
            long now = System.currentTimeMillis() / 1000;
            Map<String, Object> claims = new LinkedHashMap<>();
            claims.put("iss", keyId);
            claims.put("aud", audience);
            claims.put("iat", now);
            claims.put("exp", now + ttlSeconds);
            claims.put("jti", jti);
            if (scope != null) {
                claims.put("scope", scope);
            }
            Map<String, Object> header = new LinkedHashMap<>();
            header.put("alg", ec ? "ES256" : "EdDSA");
            header.put("kid", keyId);
            header.put("typ", "JWT");
            String input = B64.encodeToString(JSON.writeValueAsBytes(header)) + "." + B64.encodeToString(JSON.writeValueAsBytes(claims));
            // The P1363 format is the raw r || s a JWS carries; the default would be DER.
            Signature s = Signature.getInstance(ec ? "SHA256withECDSAinP1363Format" : "Ed25519");
            s.initSign(key);
            s.update(input.getBytes(StandardCharsets.US_ASCII));
            return input + "." + B64.encodeToString(s.sign());
        } catch (GeneralSecurityException | java.io.IOException e) {
            throw new IllegalArgumentException("the key must be a P-256 or Ed25519 PKCS#8 PEM private key", e);
        }
    }

    /** A compact JWS split into its parts. */
    public record Parsed(Map<String, Object> header, Map<String, Object> claims, String signingInput, byte[] signature) {
    }

    /** Split a compact JWS; null when it is not three base64url segments of JSON objects. */
    @SuppressWarnings("unchecked")
    public static Parsed parse(String token) {
        if (token == null) {
            return null;
        }
        String[] p = token.split("\\.", -1);
        if (p.length != 3) {
            return null;
        }
        try {
            Base64.Decoder d = Base64.getUrlDecoder();
            Object h = JSON.readValue(d.decode(p[0]), Object.class);
            Object c = JSON.readValue(d.decode(p[1]), Object.class);
            if (!(h instanceof Map) || !(c instanceof Map)) {
                return null;
            }
            return new Parsed((Map<String, Object>) h, (Map<String, Object>) c, p[0] + "." + p[1], d.decode(p[2]));
        } catch (IllegalArgumentException | java.io.IOException e) {
            return null;
        }
    }

    /** Check the signature with an SPKI PEM public key; {@code alg} must fit the key's type. */
    public static boolean verifySignature(String alg, String signingInput, byte[] signature, String publicKeyPem) {
        try {
            byte[] der = Base64.getMimeDecoder().decode(publicKeyPem.replaceAll("-----[A-Z ]+-----", ""));
            PublicKey key;
            try {
                key = KeyFactory.getInstance("EC").generatePublic(new X509EncodedKeySpec(der));
            } catch (GeneralSecurityException notEc) {
                key = KeyFactory.getInstance("Ed25519").generatePublic(new X509EncodedKeySpec(der));
            }
            boolean ec = key instanceof ECPublicKey;
            if (ec ? !"ES256".equals(alg) || ((ECPublicKey) key).getParams().getOrder().bitLength() != 256 || signature.length != 64 : !"EdDSA".equals(alg)) {
                return false;
            }
            Signature s = Signature.getInstance(ec ? "SHA256withECDSAinP1363Format" : "Ed25519");
            s.initVerify(key);
            s.update(signingInput.getBytes(StandardCharsets.US_ASCII));
            return s.verify(signature);
        } catch (GeneralSecurityException | IllegalArgumentException e) {
            return false;
        }
    }
}
