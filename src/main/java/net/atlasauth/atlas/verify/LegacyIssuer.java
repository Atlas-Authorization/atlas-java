package net.atlasauth.atlas.verify;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * One OLD issuer whose JWTs a {@link SessionVerifier} also accepts during a cutover, today Better Auth's
 * {@code jwt} plugin tokens.
 *
 * <p>A token whose {@code iss} is this issuer, inside the window, is verified against THIS issuer's key set
 * only (the JWKS you imported into Atlas, or the old app's {@code /api/auth/jwks}); {@code exp} is required,
 * and its {@code sub} (the Better Auth user id) is mapped to the Atlas user id, because Atlas keeps the
 * Better Auth id as the user's {@code external_id}. After {@code until}, those tokens are refused like any
 * other foreign token.
 *
 * <pre>{@code
 * LegacyIssuer.builder("https://app.example.com")
 *     .jwksUrl("https://app.example.com/api/auth/jwks")
 *     .until(Instant.parse("2026-12-31T00:00:00Z"))
 *     .build();
 * }</pre>
 */
public final class LegacyIssuer {

    /** Better Auth's {@code jwt} plugin key algorithms, the default. */
    public static final List<String> BETTER_AUTH_ALGS = List.of("EdDSA", "ES256", "ES512", "RS256", "PS256");

    private final String issuer;
    private final long untilMs;
    private final Map<String, Object> jwks;
    private final String jwksUrl;
    private final List<String> audience;
    private final List<String> algorithms;
    private final BiFunction<String, Map<String, Object>, String> resolveSubject;
    private final String kind;

    private LegacyIssuer(Builder b) {
        if (b.issuer == null || b.issuer.isEmpty()) {
            throw new IllegalArgumentException("legacyIssuers: every entry needs an issuer.");
        }
        boolean hasUrl = b.jwksUrl != null && !b.jwksUrl.isEmpty();
        if (hasUrl == (b.jwks != null)) {
            throw new IllegalArgumentException("legacyIssuers: \"" + b.issuer + "\" needs exactly one of jwks or jwksUrl.");
        }
        if (b.untilMs <= 0) {
            throw new IllegalArgumentException("legacyIssuers: \"" + b.issuer + "\" needs a valid until (the end of the window).");
        }
        List<String> algs = b.algorithms != null ? List.copyOf(b.algorithms) : BETTER_AUTH_ALGS;
        for (String alg : algs) {
            if (!BETTER_AUTH_ALGS.contains(alg)) {
                throw new IllegalArgumentException("legacyIssuers: \"" + b.issuer + "\" lists an unsupported algorithm \"" + alg + "\".");
            }
        }
        this.issuer = b.issuer;
        this.untilMs = b.untilMs;
        this.jwks = b.jwks;
        this.jwksUrl = hasUrl ? b.jwksUrl : null;
        this.audience = b.audience != null ? List.copyOf(b.audience) : null;
        this.algorithms = algs;
        this.resolveSubject = b.resolveSubject;
        this.kind = b.kind;
    }

    public static Builder builder(String issuer) {
        return new Builder(issuer);
    }

    public String issuer() {
        return issuer;
    }

    /** End of the window, epoch milliseconds. */
    public long untilMs() {
        return untilMs;
    }

    public Map<String, Object> jwks() {
        return jwks;
    }

    public String jwksUrl() {
        return jwksUrl;
    }

    public List<String> audience() {
        return audience;
    }

    public List<String> algorithms() {
        return algorithms;
    }

    public BiFunction<String, Map<String, Object>, String> resolveSubject() {
        return resolveSubject;
    }

    public String kind() {
        return kind;
    }

    /** An ISO-8601 instant or offset date-time, or a plain date ({@code 2026-12-31}), in epoch ms; 0 when unparseable. */
    public static long parseUntil(String value) {
        if (value == null || value.isBlank()) {
            return 0;
        }
        String v = value.trim();
        try {
            return Instant.parse(v).toEpochMilli();
        } catch (DateTimeParseException ignored) {
            // try the other forms
        }
        try {
            return OffsetDateTime.parse(v).toInstant().toEpochMilli();
        } catch (DateTimeParseException ignored) {
            // try a date
        }
        try {
            return java.time.LocalDate.parse(v).atStartOfDay(java.time.ZoneOffset.UTC).toInstant().toEpochMilli();
        } catch (DateTimeParseException ignored) {
            return 0;
        }
    }

    public static final class Builder {
        private final String issuer;
        private long untilMs;
        private Map<String, Object> jwks;
        private String jwksUrl;
        private List<String> audience;
        private List<String> algorithms;
        private BiFunction<String, Map<String, Object>, String> resolveSubject;
        private String kind = "better_auth";

        private Builder(String issuer) {
            this.issuer = issuer;
        }

        /** End of the acceptance window (required; never forever). */
        public Builder until(Instant until) {
            this.untilMs = until == null ? 0 : until.toEpochMilli();
            return this;
        }

        /** End of the window in epoch milliseconds. */
        public Builder untilMillis(long untilMs) {
            this.untilMs = untilMs;
            return this;
        }

        /** End of the window as ISO-8601 (an instant, an offset date-time or a date). */
        public Builder until(String iso) {
            this.untilMs = parseUntil(iso);
            return this;
        }

        /** The imported key set ({@code {"keys": [...]}}). Exactly one of {@code jwks} / {@code jwksUrl}. */
        public Builder jwks(Map<String, Object> jwks) {
            this.jwks = jwks;
            return this;
        }

        /** Where to fetch the old key set (e.g. {@code https://app.example.com/api/auth/jwks}). */
        public Builder jwksUrl(String jwksUrl) {
            this.jwksUrl = jwksUrl;
            return this;
        }

        /** Expected {@code aud} (any one of these); unchecked when unset. */
        public Builder audience(List<String> audience) {
            this.audience = audience;
            return this;
        }

        public Builder audience(String audience) {
            this.audience = audience == null ? null : List.of(audience);
            return this;
        }

        /** Accepted algorithms; default {@link #BETTER_AUTH_ALGS}. */
        public Builder algorithms(List<String> algorithms) {
            this.algorithms = algorithms;
            return this;
        }

        /**
         * Map the old {@code sub} (and the verified claims) to the Atlas user id; return null to refuse the token.
         * Default: look the user up by {@code external_id} through the Backend API (needs {@code secretKey} on the
         * verifier), cached for 5 minutes.
         */
        public Builder resolveSubject(BiFunction<String, Map<String, Object>, String> resolveSubject) {
            this.resolveSubject = resolveSubject;
            return this;
        }

        /** The {@code legacy.kind} the claims carry (default {@code better_auth}). */
        public Builder kind(String kind) {
            this.kind = kind;
            return this;
        }

        public LegacyIssuer build() {
            return new LegacyIssuer(this);
        }
    }
}
