package net.atlasauth.atlas.verify;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.ECDSAVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jose.util.JSONObjectUtils;

import java.math.BigInteger;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.EdECPoint;
import java.security.spec.EdECPublicKeySpec;
import java.security.spec.NamedParameterSpec;
import java.util.Base64;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * DPoP proof verification (RFC 9449) for {@link AccessTokenVerifier}.
 *
 * <p>A DPoP-bound access token carries {@code cnf.jkt}, the RFC 7638 thumbprint of the client's
 * key. Each request must then arrive with the {@code DPoP} scheme and a {@code DPoP} header: a
 * short-lived JWT ({@code typ} {@code dpop+jwt}) signed by that key, carrying the public key itself,
 * the request method ({@code htm}) and URL ({@code htu}), the hash of the access token
 * ({@code ath}), a unique {@code jti}, and the server's nonce when the API asks for one.
 */
public final class Dpop {

    /** How old a DPoP proof may be. */
    public static final long PROOF_MAX_AGE_SECONDS = 300;
    /** Clock skew tolerated on a proof's {@code iat}. */
    public static final long CLOCK_SKEW_SECONDS = 5;
    /** The JWS algorithms accepted on a DPoP proof (and advertised in challenges / metadata). */
    public static final List<String> ALGS = List.of("ES256", "ES384", "ES512", "RS256", "RS384", "RS512",
            "PS256", "PS384", "PS512", "EdDSA");

    private static final List<String> PRIVATE_MEMBERS = List.of("d", "k", "p", "q", "dp", "dq", "qi");
    private static final Pattern B64U = Pattern.compile("^[A-Za-z0-9_-]*$");

    /** A refusal: {@code error} is {@code invalid_dpop_proof} or {@code use_dpop_nonce}. */
    public record Refusal(String error, String description) {
    }

    private Dpop() {
    }

    static Refusal bad(String description) {
        return new Refusal("invalid_dpop_proof", description);
    }

    /** The {@code ath} a DPoP proof carries for {@code token}: base64url(SHA-256(token)). */
    public static String accessTokenHash(String token) {
        return b64u(sha256(token.getBytes(StandardCharsets.US_ASCII)));
    }

    /**
     * The RFC 7638 SHA-256 thumbprint (base64url) of a public EC, RSA or OKP JWK: the
     * {@code cnf.jkt} a DPoP-bound token carries.
     *
     * @throws IllegalArgumentException for an unsupported or incomplete key
     */
    public static String jwkThumbprint(Map<String, Object> jwk) {
        Object kty = jwk.get("kty");
        List<String> members;
        if ("EC".equals(kty)) {
            members = List.of("crv", "kty", "x", "y");
        } else if ("RSA".equals(kty)) {
            members = List.of("e", "kty", "n");
        } else if ("OKP".equals(kty)) {
            members = List.of("crv", "kty", "x");
        } else {
            throw new IllegalArgumentException("Unsupported JWK kty.");
        }
        StringBuilder sb = new StringBuilder("{");
        for (String m : members) {
            if (!(jwk.get(m) instanceof String v) || v.isEmpty()) {
                throw new IllegalArgumentException("JWK member " + m + " is missing.");
            }
            if (sb.length() > 1) {
                sb.append(',');
            }
            sb.append('"').append(m).append("\":\"").append(jsonEscape(v)).append('"');
        }
        return b64u(sha256(sb.append('}').toString().getBytes(StandardCharsets.UTF_8)));
    }

    /**
     * Reduce a URL to the form RFC 9449 §4.3 compares {@code htu} in: scheme and host lower-cased,
     * the default port dropped, an empty path as "/", no query or fragment. Null for anything but an
     * absolute http(s) URL.
     */
    public static String normalizeHtu(String url) {
        if (url == null) {
            return null;
        }
        URI u;
        try {
            u = new URI(url);
        } catch (Exception e) {
            return null;
        }
        String scheme = u.getScheme() == null ? "" : u.getScheme().toLowerCase(Locale.ROOT);
        if (!scheme.equals("http") && !scheme.equals("https") || u.getHost() == null || u.getHost().isEmpty()) {
            return null;
        }
        String host = u.getHost().toLowerCase(Locale.ROOT);
        int port = u.getPort();
        boolean dflt = port == -1 || (scheme.equals("https") && port == 443) || (scheme.equals("http") && port == 80);
        String path = u.getRawPath() == null || u.getRawPath().isEmpty() ? "/" : u.getRawPath();
        return scheme + "://" + host + (dflt ? "" : ":" + port) + path;
    }

    static Refusal check(String proof, String token, String jkt, String method, String url, String publicOrigin,
                         long nowMillis, DpopReplayStore replay, DpopNonceSource nonces) {
        String[] parts = proof.split("\\.", -1);
        if (parts.length != 3) {
            return bad("The DPoP proof is malformed.");
        }
        Map<String, Object> header = json(parts[0]);
        if (header == null) {
            return bad("The DPoP proof is malformed.");
        }
        if (!"dpop+jwt".equals(header.get("typ"))) {
            return bad("The DPoP proof must have typ dpop+jwt.");
        }
        if (!(header.get("alg") instanceof String alg) || !ALGS.contains(alg)) {
            return bad("The DPoP proof uses an unsupported algorithm.");
        }
        if (!(header.get("jwk") instanceof Map<?, ?> rawJwk)) {
            return bad("The DPoP proof must carry its public key.");
        }
        @SuppressWarnings("unchecked")
        Map<String, Object> jwk = (Map<String, Object>) rawJwk;
        for (String m : PRIVATE_MEMBERS) {
            if (jwk.containsKey(m)) {
                return bad("The DPoP proof key must be a public key.");
            }
        }
        byte[] sig = b64uDecode(parts[2]);
        if (sig == null || !verifySignature(jwk, alg, parts[0] + "." + parts[1], sig)) {
            return bad("The DPoP proof signature is invalid.");
        }
        Map<String, Object> payload = json(parts[1]);
        if (payload == null) {
            return bad("The DPoP proof is malformed.");
        }
        String thumb;
        try {
            thumb = jwkThumbprint(jwk);
        } catch (IllegalArgumentException e) {
            thumb = "";
        }
        if (thumb.isEmpty() || !MessageDigest.isEqual(thumb.getBytes(StandardCharsets.US_ASCII),
                jkt.getBytes(StandardCharsets.US_ASCII))) {
            return bad("The DPoP proof key does not match the token binding.");
        }
        if (!method.equals(payload.get("htm"))) {
            return bad("The DPoP proof htm does not match the request method.");
        }
        String target = url;
        if (!url.regionMatches(true, 0, "http://", 0, 7) && !url.regionMatches(true, 0, "https://", 0, 8)) {
            target = null;
            if (publicOrigin != null) {
                try {
                    target = URI.create(publicOrigin).resolve(url).toString();
                } catch (Exception e) {
                    target = null;
                }
            }
        }
        String expected = target == null ? null : normalizeHtu(target);
        if (expected == null || !(payload.get("htu") instanceof String htu) || !expected.equals(normalizeHtu(htu))) {
            return bad("The DPoP proof htu does not match the request URL.");
        }
        double nowSec = nowMillis / 1000.0;
        if (!(payload.get("iat") instanceof Number iatN)) {
            return bad("The DPoP proof is too old or from the future.");
        }
        double iat = iatN.doubleValue();
        if (iat > nowSec + CLOCK_SKEW_SECONDS || nowSec - iat > PROOF_MAX_AGE_SECONDS) {
            return bad("The DPoP proof is too old or from the future.");
        }
        if (payload.get("exp") instanceof Number exp && nowSec >= exp.doubleValue() + CLOCK_SKEW_SECONDS) {
            return bad("The DPoP proof is too old or from the future.");
        }
        if (!accessTokenHash(token).equals(payload.get("ath"))) {
            return bad("The DPoP proof ath does not match the access token.");
        }
        if (!(payload.get("jti") instanceof String jti) || jti.isEmpty()) {
            return bad("The DPoP proof needs a jti.");
        }
        if (nonces != null) {
            if (!(payload.get("nonce") instanceof String nonce) || nonce.isEmpty() || !nonces.valid(nonce)) {
                return new Refusal("use_dpop_nonce", "The DPoP proof must carry the nonce from the DPoP-Nonce header.");
            }
        }
        if (!replay.claim(jkt + ":" + jti, (long) (iat * 1000) + PROOF_MAX_AGE_SECONDS * 1000)) {
            return bad("The DPoP proof has already been used.");
        }
        return null;
    }

    private static boolean verifySignature(Map<String, Object> jwk, String alg, String input, byte[] sig) {
        if (jwk.get("alg") instanceof String ka && !ka.isEmpty() && !ka.equals(alg)) {
            return false;
        }
        byte[] data = input.getBytes(StandardCharsets.US_ASCII);
        try {
            Object kty = jwk.get("kty");
            if (alg.startsWith("ES")) {
                Curve want = switch (alg) {
                    case "ES256" -> Curve.P_256;
                    case "ES384" -> Curve.P_384;
                    default -> Curve.P_521;
                };
                if (!"EC".equals(kty) || !want.getName().equals(jwk.get("crv"))) {
                    return false;
                }
                ECKey key = ECKey.parse(jwk);
                return nimbusVerify(new ECDSAVerifier(key), alg, data, sig);
            }
            if (alg.startsWith("RS") || alg.startsWith("PS")) {
                if (!"RSA".equals(kty)) {
                    return false;
                }
                RSAKey key = RSAKey.parse(jwk);
                if (key.size() < 2048) {
                    return false;
                }
                return nimbusVerify(new RSASSAVerifier(key), alg, data, sig);
            }
            if (alg.equals("EdDSA")) {
                if (!"OKP".equals(kty) || !"Ed25519".equals(jwk.get("crv")) || sig.length != 64) {
                    return false;
                }
                byte[] x = b64uDecode(String.valueOf(jwk.get("x")));
                if (x == null || x.length != 32) {
                    return false;
                }
                Signature s = Signature.getInstance("Ed25519");
                s.initVerify(ed25519PublicKey(x));
                s.update(data);
                return s.verify(sig);
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    private static boolean nimbusVerify(JWSVerifier verifier, String alg, byte[] data, byte[] sig) throws Exception {
        JWSHeader h = new JWSHeader.Builder(JWSAlgorithm.parse(alg)).build();
        return verifier.verify(h, data, Base64URL.encode(sig));
    }

    /** An Ed25519 public key from its 32-byte RFC 8032 encoding. */
    static PublicKey ed25519PublicKey(byte[] x) throws Exception {
        byte[] le = x.clone();
        boolean xOdd = (le[31] & 0x80) != 0;
        le[31] &= 0x7f;
        byte[] be = new byte[32];
        for (int i = 0; i < 32; i++) {
            be[i] = le[31 - i];
        }
        EdECPoint point = new EdECPoint(xOdd, new BigInteger(1, be));
        return KeyFactory.getInstance("Ed25519").generatePublic(new EdECPublicKeySpec(NamedParameterSpec.ED25519, point));
    }

    private static Map<String, Object> json(String segment) {
        byte[] raw = b64uDecode(segment);
        if (raw == null) {
            return null;
        }
        try {
            return JSONObjectUtils.parse(new String(raw, StandardCharsets.UTF_8));
        } catch (Exception e) {
            return null;
        }
    }

    static byte[] b64uDecode(String s) {
        if (s == null || !B64U.matcher(s).matches()) {
            return null;
        }
        try {
            return Base64.getUrlDecoder().decode(s);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    static String b64u(byte[] raw) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(raw);
    }

    private static byte[] sha256(byte[] in) {
        try {
            return MessageDigest.getInstance("SHA-256").digest(in);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private static String jsonEscape(String v) {
        StringBuilder sb = new StringBuilder();
        for (char c : v.toCharArray()) {
            if (c == '"' || c == '\\') {
                sb.append('\\').append(c);
            } else if (c < 0x20) {
                sb.append(String.format("\\u%04x", (int) c));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}
