package net.atlasauth.atlas;

import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.OctetKeyPair;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.util.Base64URL;

import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.Signature;
import java.security.interfaces.ECPublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.PSSParameterSpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

/** A runtime key per JWS algorithm Atlas signs with, and a compact-JWT signer using only the JDK. */
public final class TestJwt {

    public static final List<String> ALGS = List.of("RS256", "PS256", "ES256", "ES384", "EdDSA");
    private static final Base64.Encoder B64U = Base64.getUrlEncoder().withoutPadding();

    public final String alg;
    public final String kid;
    public final KeyPair pair;

    private TestJwt(String alg, String kid, KeyPair pair) {
        this.alg = alg;
        this.kid = kid;
        this.pair = pair;
    }

    public static TestJwt generate(String alg) throws Exception {
        KeyPairGenerator g;
        switch (alg) {
            case "RS256", "PS256" -> {
                g = KeyPairGenerator.getInstance("RSA");
                g.initialize(2048);
            }
            case "ES256" -> {
                g = KeyPairGenerator.getInstance("EC");
                g.initialize(new ECGenParameterSpec("secp256r1"));
            }
            case "ES384" -> {
                g = KeyPairGenerator.getInstance("EC");
                g.initialize(new ECGenParameterSpec("secp384r1"));
            }
            default -> g = KeyPairGenerator.getInstance("Ed25519");
        }
        return new TestJwt(alg, "k-" + alg, g.generateKeyPair());
    }

    public JWK publicJwk() {
        return switch (alg) {
            case "RS256", "PS256" -> new RSAKey.Builder((RSAPublicKey) pair.getPublic()).keyID(kid).build();
            case "ES256" -> new ECKey.Builder(Curve.P_256, (ECPublicKey) pair.getPublic()).keyID(kid).build();
            case "ES384" -> new ECKey.Builder(Curve.P_384, (ECPublicKey) pair.getPublic()).keyID(kid).build();
            default -> {
                byte[] der = pair.getPublic().getEncoded();
                yield new OctetKeyPair.Builder(Curve.Ed25519, Base64URL.encode(Arrays.copyOfRange(der, der.length - 32, der.length))).keyID(kid).build();
            }
        };
    }

    public String jwksJson() {
        return "{\"keys\":[" + publicJwk().toJSONString() + "]}";
    }

    /** Sign with this key under the algorithm named in {@code headerAlg} (to build mismatches), typ optional. */
    public String sign(String headerAlg, String typ, String payloadJson) throws Exception {
        String header = "{\"alg\":\"" + headerAlg + "\",\"kid\":\"" + kid + "\"" + (typ != null ? ",\"typ\":\"" + typ + "\"" : "") + "}";
        String input = B64U.encodeToString(header.getBytes(StandardCharsets.UTF_8)) + "." + B64U.encodeToString(payloadJson.getBytes(StandardCharsets.UTF_8));
        Signature s = switch (alg) {
            case "RS256" -> Signature.getInstance("SHA256withRSA");
            case "PS256" -> {
                Signature p = Signature.getInstance("RSASSA-PSS");
                p.setParameter(new PSSParameterSpec("SHA-256", "MGF1", MGF1ParameterSpec.SHA256, 32, 1));
                yield p;
            }
            case "ES256" -> Signature.getInstance("SHA256withECDSAinP1363Format");
            case "ES384" -> Signature.getInstance("SHA384withECDSAinP1363Format");
            default -> Signature.getInstance("Ed25519");
        };
        s.initSign(pair.getPrivate());
        s.update(input.getBytes(StandardCharsets.US_ASCII));
        return input + "." + B64U.encodeToString(s.sign());
    }

    public String sign(String payloadJson) throws Exception {
        return sign(alg, null, payloadJson);
    }
}
