package net.atlasauth.atlas;

/**
 * Atlas credential prefixes. Keys come in two formats: the suite format {@code atlas_<live|test>_<kind>_<random><checksum>}
 * ({@code sk}, {@code psk}, {@code dsk}, {@code whsec}, {@code pl}, {@code at}) and the legacy format
 * ({@code sk_live_...}, {@code sk_test_...}, {@code pk_...}, {@code psk_...}, {@code whsec_...}, {@code dsk_...}). Any prefix
 * check in your code must accept both; these helpers do. They classify by prefix only: no checksum is validated and
 * Atlas is not contacted.
 */
public final class KeyFormat {

    /** What a credential is. */
    public enum Kind {
        SECRET("sk"), PLATFORM("psk"), PUBLISHABLE("pk"), DEPLOY("dsk"), WEBHOOK_SECRET("whsec"), PARTNER_LINK("pl"), ACCESS_TOKEN("at");

        private final String code;

        Kind(String code) {
            this.code = code;
        }

        /** The kind's prefix segment ({@code sk}, {@code psk}, ...). */
        public String code() {
            return code;
        }
    }

    /**
     * What {@link #parse(String)} learned from a prefix.
     *
     * @param kind        what the credential is
     * @param environment {@code live}, {@code test} or null when the key carries none
     * @param suite       true for the {@code atlas_<env>_<kind>_...} format, false for legacy
     */
    public record Parsed(Kind kind, String environment, boolean suite) {
    }

    private KeyFormat() {
    }

    private static boolean isEnv(String s) {
        return "live".equals(s) || "test".equals(s);
    }

    private static Kind kindOf(String code) {
        for (Kind k : Kind.values()) {
            if (k.code.equals(code)) {
                return k;
            }
        }
        return null;
    }

    /** Classify a credential by its prefix; null for anything that is not an Atlas key. */
    public static Parsed parse(String key) {
        if (key == null) {
            return null;
        }
        if (key.startsWith("atlas_")) {
            String[] parts = key.substring("atlas_".length()).split("_", 3);
            if (parts.length == 3 && isEnv(parts[0])) {
                Kind kind = kindOf(parts[1]);
                if (kind != null && kind != Kind.PUBLISHABLE) {
                    return new Parsed(kind, parts[0], true);
                }
            }
            return null;
        }
        String[] parts = key.split("_", 3);
        if (parts.length < 2) {
            return null;
        }
        Kind kind = kindOf(parts[0]);
        if (kind == null) {
            return null;
        }
        switch (kind) {
            case SECRET:
            case PUBLISHABLE:
                if (parts.length == 3 && isEnv(parts[1])) {
                    return new Parsed(kind, parts[1], false);
                }
                return null;
            case PLATFORM:
            case DEPLOY:
            case WEBHOOK_SECRET:
                return new Parsed(kind, parts.length == 3 && isEnv(parts[1]) ? parts[1] : null, false);
            default:
                return null;
        }
    }

    private static boolean is(String key, Kind kind) {
        Parsed p = parse(key);
        return p != null && p.kind() == kind;
    }

    /** An instance secret key in either format ({@code atlas_live_sk_...} or {@code sk_live_...}). */
    public static boolean isSecretKey(String key) {
        return is(key, Kind.SECRET);
    }

    /** A platform key in either format ({@code atlas_live_psk_...} or {@code psk_...}). */
    public static boolean isPlatformKey(String key) {
        return is(key, Kind.PLATFORM);
    }

    /** A publishable key ({@code pk_live_...} / {@code pk_test_...}); publishable keys are not secrets and have no suite form. */
    public static boolean isPublishableKey(String key) {
        return is(key, Kind.PUBLISHABLE);
    }

    /** A webhook signing secret in either format; verification is unchanged: the whole string is the HMAC key. */
    public static boolean isWebhookSecret(String key) {
        return is(key, Kind.WEBHOOK_SECRET);
    }

    /** A partner-link secret ({@code atlas_live_pl_...}). */
    public static boolean isPartnerLinkSecret(String key) {
        return is(key, Kind.PARTNER_LINK);
    }

    /** A one-hour Atlas access token from an operator token exchange ({@code atlas_live_at_...}). */
    public static boolean isAccessToken(String key) {
        return is(key, Kind.ACCESS_TOKEN);
    }
}
