package net.atlasauth.atlas.verify;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import java.net.http.HttpClient;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Verify a Transaction Token (draft-ietf-oauth-transaction-tokens) a service received: {@code typ txntoken+jwt},
 * signed by the issuer's keys (RS256, PS256, ES256, ES384 or EdDSA, see {@link SigningAlgs}), for THIS trust domain,
 * unexpired, with the required claims. A recipient must refuse a token whose {@code aud} is not its own trust domain.
 *
 * <pre>{@code
 * TransactionTokenVerifier v = TransactionTokenVerifier.forIssuer("https://auth.example.com", "payments.example.com");
 * TransactionTokenVerifier.Result r = v.verify(token);
 * if (r.ok()) r.claims().get("txn"); r.scopes();
 * }</pre>
 */
public final class TransactionTokenVerifier {

    /** The verdict. {@code error} is set when not ok; the claims and scopes are set when ok. */
    public record Result(boolean ok, Map<String, Object> claims, List<String> scopes, String error) {
    }

    private static final String ERROR = "The transaction token is invalid, expired, or for another trust domain.";
    private final String issuer;
    private final String trustDomain;
    private final Function<String, JWKSet> keys;

    private TransactionTokenVerifier(String issuer, String trustDomain, Function<String, JWKSet> keys) {
        this.issuer = issuer.replaceAll("/+$", "");
        this.trustDomain = trustDomain;
        this.keys = keys;
    }

    /** Keys fetched (and cached) from {@code <issuer>/.well-known/jwks.json}. */
    public static TransactionTokenVerifier forIssuer(String issuer, String trustDomain) {
        return forJwksUrl(issuer, trustDomain, issuer.replaceAll("/+$", "") + "/.well-known/jwks.json");
    }

    /** Keys fetched (and cached) from this JWKS URL. */
    public static TransactionTokenVerifier forJwksUrl(String issuer, String trustDomain, String jwksUrl) {
        JwksCache cache = new JwksCache(jwksUrl, HttpClient.newHttpClient(), System::currentTimeMillis);
        return new TransactionTokenVerifier(issuer, trustDomain, cache::get);
    }

    /** Verify against a JWKS you already hold (no network). */
    public static TransactionTokenVerifier forJwks(String issuer, String trustDomain, JWKSet jwks) {
        return new TransactionTokenVerifier(issuer, trustDomain, kid -> jwks);
    }

    /** Verify {@code token} against the current time (5 seconds of clock skew allowed). */
    public Result verify(String token) {
        try {
            SignedJWT jwt = SignedJWT.parse(token);
            if (jwt.getHeader().getType() == null || !"txntoken+jwt".equals(jwt.getHeader().getType().getType()) || !SigningAlgs.allowed(jwt)) {
                return fail();
            }
            String kid = jwt.getHeader().getKeyID();
            JWKSet set = keys.apply(kid);
            if (set == null || !SigningAlgs.verify(jwt, set, kid)) {
                return fail();
            }
            JWTClaimsSet c = jwt.getJWTClaimsSet();
            long now = System.currentTimeMillis();
            if (!issuer.equals(c.getIssuer()) || c.getAudience() == null || !c.getAudience().contains(trustDomain)
                    || c.getExpirationTime() == null || c.getIssueTime() == null
                    || c.getExpirationTime().getTime() + 5000 < now || c.getIssueTime().getTime() - 5000 > now) {
                return fail();
            }
            for (String required : List.of("txn", "sub", "scope", "req_wl")) {
                if (c.getClaim(required) == null) {
                    return fail();
                }
            }
            String scope = c.getStringClaim("scope");
            return new Result(true, c.getClaims(), Arrays.stream(scope.split(" ")).filter(s -> !s.isEmpty()).toList(), null);
        } catch (Exception e) {
            return fail();
        }
    }

    private static Result fail() {
        return new Result(false, null, List.of(), ERROR);
    }
}
