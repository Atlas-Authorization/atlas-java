package net.atlasauth.atlas.betterauth;

/**
 * A cookie to set (or, with {@code maxAge} 0, clear) on the response.
 *
 * @param sameSite {@code Lax}, {@code Strict}, {@code None}, or null
 * @param maxAge   seconds, or null for a session cookie
 */
public record BridgeCookie(String name, String value, String path, String domain, boolean httpOnly,
                           String sameSite, boolean secure, Integer maxAge) {

    /** The cookie as a {@code Set-Cookie} header value. */
    public String toHeader() {
        StringBuilder b = new StringBuilder(name).append('=').append(value)
                .append("; Path=").append(path == null || path.isEmpty() ? "/" : path);
        if (domain != null && !domain.isEmpty()) {
            b.append("; Domain=").append(domain);
        }
        if (maxAge != null) {
            b.append("; Max-Age=").append(maxAge);
            if (maxAge == 0) {
                b.append("; Expires=Thu, 01 Jan 1970 00:00:00 GMT");
            }
        }
        if (httpOnly) {
            b.append("; HttpOnly");
        }
        if (secure) {
            b.append("; Secure");
        }
        if (sameSite != null) {
            b.append("; SameSite=").append(sameSite);
        }
        return b.toString();
    }
}
