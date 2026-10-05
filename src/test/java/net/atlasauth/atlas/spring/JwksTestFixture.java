package net.atlasauth.atlas.spring;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.sun.net.httpserver.HttpServer;

import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

/**
 * The same loopback-JWKS + real-RSA-signer setup {@code SessionVerifierTest}
 * uses, reused across the Spring tests (the original helper is private to that
 * class). A real 2048-bit key signs real RS256 JWTs and a {@link HttpServer}
 * serves the public JWKS, so the Spring filter runs the genuine
 * {@link net.atlasauth.atlas.verify.SessionVerifier} — no mocking of verification.
 *
 * <p>Unlike {@code SessionVerifierTest} this signs against the real wall clock
 * (exp = now + 60s), because the auto-configured verifier uses the default clock.
 */
final class JwksTestFixture implements AutoCloseable {

    static final String ISSUER = "https://clerk.example.com";
    private static final String KID = "spring-test-key-1";

    private final HttpServer server;
    private final RSAKey rsaKey;
    private final String jwksUrl;

    JwksTestFixture() throws Exception {
        rsaKey = new RSAKeyGenerator(2048).keyID(KID).generate();
        String jwks = new JWKSet(rsaKey.toPublicJWK()).toString();

        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/v1/jwks", exchange -> {
            byte[] body = jwks.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("content-type", "application/json");
            exchange.sendResponseHeaders(200, body.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(body);
            }
            exchange.close();
        });
        server.start();
        jwksUrl = "http://127.0.0.1:" + server.getAddress().getPort() + "/v1/jwks";
    }

    String jwksUrl() {
        return jwksUrl;
    }

    /** A valid session JWT for {@code usr_1}, expiring 60s from now. */
    String signSession(Map<String, Object> extra) throws Exception {
        long now = System.currentTimeMillis();
        JWTClaimsSet.Builder claims = new JWTClaimsSet.Builder()
                .issuer(ISSUER)
                .subject("usr_1")
                .claim("sid", "sess_1")
                .issueTime(new Date(now - 1000))
                .notBeforeTime(new Date(now - 1000))
                .expirationTime(new Date(now + 60_000));
        extra.forEach(claims::claim);

        SignedJWT jwt = new SignedJWT(
                new JWSHeader.Builder(JWSAlgorithm.RS256).keyID(KID).build(),
                claims.build());
        JWSSigner signer = new RSASSASigner(rsaKey);
        jwt.sign(signer);
        return jwt.serialize();
    }

    @Override
    public void close() {
        server.stop(0);
    }
}
