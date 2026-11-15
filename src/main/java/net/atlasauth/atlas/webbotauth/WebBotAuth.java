package net.atlasauth.atlas.webbotauth;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.URLDecoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.LongSupplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Web Bot Auth (HTTP Message Signatures, RFC 9421, with the {@code web-bot-auth} tag): verify that a request
 * was signed by a bot whose Ed25519 key is published in the key directory named by its
 * {@code Signature-Agent} header, then decide allow / block / unknown against your policy. Also signs, so a bot
 * of your own can authenticate to a site that checks.
 *
 * <pre>{@code
 * WebBotAuth.Verifier v = WebBotAuth.verifier().allow("https://bot.example", "ExampleBot").build();
 * WebBotAuth.Decision d = v.verify("GET", "https://site.example/page", headers);
 * if (d.verified()) { d.agent().name(); }
 * }</pre>
 *
 * <p>Verification fails closed: a request that does not verify is {@code unknown} (never allowed). Directories
 * are fetched over https only, never from a literal IP or private host, with redirects refused, a 5 second
 * timeout and a 64 KiB cap, and cached.
 *
 * <p>shortcut: a covered header with the {@code ;sf} parameter is compared as sent rather than re-serialized
 * as a structured field, so a signer using {@code ;sf} over a non-canonical header is rejected as
 * {@code bad_signature} (fail closed); the common {@code ;key} and {@code ;bs} forms and every derived
 * component are exact.
 */
public final class WebBotAuth {

    public static final String TAG = "web-bot-auth";
    public static final String DIRECTORY_PATH = "/.well-known/http-message-signatures-directory";
    public static final String DIRECTORY_MEDIA_TYPE = "application/http-message-signatures-directory+json";
    public static final int DEFAULT_SKEW_SECONDS = 30;
    public static final int DEFAULT_MAX_LIFETIME_SECONDS = 3600;

    private static final Base64.Encoder B64U = Base64.getUrlEncoder().withoutPadding();
    private static final Pattern KEY_RE = Pattern.compile("^[a-z*][a-z0-9_.*-]*$");
    private static final Set<String> DERIVED = Set.of("@method", "@authority", "@path", "@target-uri", "@scheme", "@query", "@request-target");

    private WebBotAuth() {
    }

    // ---- result types ------------------------------------------------------

    /** What verification found. {@code reason} (when not verified): one of the lowercase failure names, e.g. {@code bad_signature}, {@code expired}, {@code key_not_found}. */
    public record Result(boolean signed, boolean verified, String reason, String directory, String keyId, String label,
                         Long created, Long expires, String nonce) {
        static Result fail(String reason, String label, String keyId, Long created, Long expires, String directory) {
            return new Result(true, false, reason, directory, keyId, label, created, expires, null);
        }
    }

    public record Agent(String directory, String keyId, String name) {
    }

    /** The policy verdict: {@code decision} is allow / block / unknown, {@code tier} is allowed / blocked / verified / unverified. */
    public record Decision(boolean verified, Agent agent, String tier, String decision, String reason) {
    }

    /** A covered component of a signature: a derived component ({@code @authority}) or a header, with optional parameters. */
    public record Component(String name, Map<String, Object> params) {
        public Component(String name) {
            this(name, Map.of());
        }
    }

    /** Directory keys for a directory URL; {@code refresh} asks for a fresh copy. Null = unavailable. */
    @FunctionalInterface
    public interface KeySource {
        List<byte[]> getKeys(String directoryUrl, boolean refresh);
    }

    // ---- encoding ----------------------------------------------------------

    static byte[] b64uDecode(String s) {
        if (!s.matches("^[A-Za-z0-9_-]*$")) {
            return null;
        }
        try {
            return Base64.getUrlDecoder().decode(s);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    static byte[] b64Decode(String s) {
        if (!s.matches("^[A-Za-z0-9+/]*={0,2}$")) {
            return null;
        }
        try {
            return Base64.getDecoder().decode(s);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /** The RFC 7638 thumbprint of an Ed25519 JWK whose {@code x} is given: the {@code keyid} a signature carries. */
    public static String ed25519JwkThumbprint(String x) {
        return B64U.encodeToString(sha256(("{\"crv\":\"Ed25519\",\"kty\":\"OKP\",\"x\":\"" + x + "\"}").getBytes(StandardCharsets.UTF_8)));
    }

    /** The Ed25519 public keys (raw 32 bytes) of a directory document, at most 20; entries that are not Ed25519 OKP keys are skipped. */
    public static List<byte[]> parseDirectory(JsonNode doc) {
        List<byte[]> out = new ArrayList<>();
        JsonNode keys = doc == null ? null : doc.get("keys");
        if (keys == null || !keys.isArray()) {
            return out;
        }
        for (JsonNode k : keys) {
            if (out.size() >= 20) {
                break;
            }
            if ("OKP".equals(k.path("kty").asText()) && "Ed25519".equals(k.path("crv").asText()) && k.path("x").isTextual()) {
                byte[] bytes = b64uDecode(k.get("x").asText());
                if (bytes != null && bytes.length == 32) {
                    out.add(bytes);
                }
            }
        }
        return out;
    }

    static String thumbprintOf(byte[] pub) {
        return ed25519JwkThumbprint(B64U.encodeToString(pub));
    }

    // ---- structured fields (the subset RFC 9421 headers use) ----------------

    static List<String> splitTop(String s, char sep) {
        List<String> out = new ArrayList<>();
        int depth = 0, start = 0;
        boolean quoted = false;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (quoted) {
                if (c == '\\') {
                    i++;
                } else if (c == '"') {
                    quoted = false;
                }
            } else if (c == '"') {
                quoted = true;
            } else if (c == '(') {
                depth++;
            } else if (c == ')') {
                depth--;
            } else if (c == sep && depth == 0) {
                out.add(s.substring(start, i));
                start = i + 1;
            }
        }
        out.add(s.substring(start));
        return out.stream().map(String::trim).filter(m -> !m.isEmpty()).toList();
    }

    /** Reads a quoted sf-string at {@code i}; returns {value, end} or null. */
    private static Object[] readString(String s, int i) {
        if (i >= s.length() || s.charAt(i) != '"') {
            return null;
        }
        StringBuilder out = new StringBuilder();
        for (int j = i + 1; j < s.length(); j++) {
            char c = s.charAt(j);
            if (c == '\\') {
                if (j + 1 >= s.length() || (s.charAt(j + 1) != '"' && s.charAt(j + 1) != '\\')) {
                    return null;
                }
                out.append(s.charAt(j + 1));
                j++;
            } else if (c == '"') {
                return new Object[]{out.toString(), j + 1};
            } else if (c < 0x20 || c > 0x7e) {
                return null;
            } else {
                out.append(c);
            }
        }
        return null;
    }

    private static boolean tokenChar(char c) {
        return Character.isLetterOrDigit(c) && c < 128 || ":/!#$%&'*+-.^_`|~".indexOf(c) >= 0;
    }

    /** Parse {@code ;k=v;k2}; null when malformed. Values are String, Long, Boolean or byte[]. */
    static Map<String, Object> parseParams(String text) {
        Map<String, Object> out = new LinkedHashMap<>();
        int i = 0;
        while (i < text.length()) {
            if (text.charAt(i) != ';') {
                return null;
            }
            i++;
            StringBuilder key = new StringBuilder();
            while (i < text.length() && "abcdefghijklmnopqrstuvwxyz0123456789_.*-".indexOf(text.charAt(i)) >= 0) {
                key.append(text.charAt(i++));
            }
            if (!KEY_RE.matcher(key).matches()) {
                return null;
            }
            if (i >= text.length() || text.charAt(i) != '=') {
                out.put(key.toString(), Boolean.TRUE);
                continue;
            }
            i++;
            char c = i < text.length() ? text.charAt(i) : '\0';
            if (c == '"') {
                Object[] str = readString(text, i);
                if (str == null) {
                    return null;
                }
                out.put(key.toString(), str[0]);
                i = (Integer) str[1];
            } else if (c == ':') {
                int close = text.indexOf(':', i + 1);
                if (close < 0) {
                    return null;
                }
                byte[] bytes = b64Decode(text.substring(i + 1, close));
                if (bytes == null) {
                    return null;
                }
                out.put(key.toString(), bytes);
                i = close + 1;
            } else if (c == '?') {
                String b = text.substring(i, Math.min(i + 2, text.length()));
                if (!b.equals("?0") && !b.equals("?1")) {
                    return null;
                }
                out.put(key.toString(), b.equals("?1"));
                i += 2;
            } else if (c == '-' || Character.isDigit(c)) {
                StringBuilder n = new StringBuilder();
                while (i < text.length() && "-0123456789.".indexOf(text.charAt(i)) >= 0) {
                    n.append(text.charAt(i++));
                }
                if (!n.toString().matches("^-?\\d{1,15}(\\.\\d{1,3})?$")) {
                    return null;
                }
                try {
                    out.put(key.toString(), n.indexOf(".") >= 0 ? (Object) Double.valueOf(n.toString()).longValue() : (Object) Long.valueOf(n.toString()));
                } catch (NumberFormatException e) {
                    return null;
                }
            } else if (Character.isLetter(c) || c == '*') {
                StringBuilder t = new StringBuilder();
                while (i < text.length() && tokenChar(text.charAt(i))) {
                    t.append(text.charAt(i++));
                }
                out.put(key.toString(), t.toString());
            } else {
                return null;
            }
        }
        return out;
    }

    record ParsedInput(String label, List<Component> components, Map<String, Object> params, String raw) {
    }

    /** Parse a {@code Signature-Input} field; null when any member is malformed. */
    static List<ParsedInput> parseSignatureInput(String header) {
        List<ParsedInput> out = new ArrayList<>();
        for (String member : splitTop(header, ',')) {
            int eq = member.indexOf('=');
            if (eq <= 0) {
                return null;
            }
            String label = member.substring(0, eq).trim();
            String raw = member.substring(eq + 1).trim();
            if (!KEY_RE.matcher(label).matches() || raw.isEmpty() || raw.charAt(0) != '(') {
                return null;
            }
            int close = -1;
            boolean quoted = false;
            for (int i = 1; i < raw.length(); i++) {
                char c = raw.charAt(i);
                if (quoted) {
                    if (c == '\\') {
                        i++;
                    } else if (c == '"') {
                        quoted = false;
                    }
                } else if (c == '"') {
                    quoted = true;
                } else if (c == ')') {
                    close = i;
                    break;
                }
            }
            if (close < 0) {
                return null;
            }
            List<Component> components = new ArrayList<>();
            String inner = raw.substring(1, close);
            int i = 0;
            while (i < inner.length()) {
                if (inner.charAt(i) == ' ') {
                    i++;
                    continue;
                }
                Object[] str = readString(inner, i);
                if (str == null) {
                    return null;
                }
                i = (Integer) str[1];
                StringBuilder ptext = new StringBuilder();
                while (i < inner.length() && inner.charAt(i) != ' ') {
                    if (inner.charAt(i) == '"') {
                        Object[] q = readString(inner, i);
                        if (q == null) {
                            return null;
                        }
                        ptext.append(inner, i, (Integer) q[1]);
                        i = (Integer) q[1];
                    } else {
                        ptext.append(inner.charAt(i++));
                    }
                }
                Map<String, Object> params = parseParams(ptext.toString());
                if (params == null) {
                    return null;
                }
                components.add(new Component((String) str[0], params));
            }
            Map<String, Object> params = parseParams(raw.substring(close + 1));
            if (params == null) {
                return null;
            }
            out.add(new ParsedInput(label, components, params, raw));
        }
        return out.isEmpty() ? null : out;
    }

    /** Parse a {@code Signature} field into label to signature bytes; null when malformed. */
    static Map<String, byte[]> parseSignatureHeader(String header) {
        Map<String, byte[]> out = new LinkedHashMap<>();
        Pattern p = Pattern.compile("^([a-z*][a-z0-9_.*-]*)=:([A-Za-z0-9+/=]*):$");
        for (String member : splitTop(header, ',')) {
            Matcher m = p.matcher(member);
            if (!m.matches()) {
                return null;
            }
            byte[] bytes = b64Decode(m.group(2));
            if (bytes == null) {
                return null;
            }
            out.put(m.group(1), bytes);
        }
        return out.isEmpty() ? null : out;
    }

    /**
     * The directory a {@code Signature-Agent} value names, normalised (https, no credentials, query or fragment,
     * no trailing slash). Accepts a quoted string, a bare URL, or the dictionary form ({@code sig1="https://bot.example"}).
     * Null when it names nothing usable.
     */
    public static String parseSignatureAgent(String value, String label) {
        String v = value == null ? "" : value.trim();
        if (v.isEmpty()) {
            return null;
        }
        if (v.matches("^[a-z*][a-z0-9_.*-]*=.*")) {
            List<String> members = splitTop(v, ',');
            String pick = members.stream().filter(m -> label != null && m.startsWith(label + "=")).findFirst().orElse(members.get(0));
            v = pick.substring(pick.indexOf('=') + 1).trim();
        }
        if (v.startsWith("\"")) {
            Object[] str = readString(v, 0);
            if (str == null || (Integer) str[1] != v.length()) {
                return null;
            }
            v = (String) str[0];
        }
        return normalizeDirectory(v);
    }

    /** Canonical form of a directory reference: {@code https://host[:port][/path]}; a bare host gets https. Null for anything else. */
    public static String normalizeDirectory(String input) {
        String s = input == null ? "" : input.trim();
        if (s.isEmpty()) {
            return null;
        }
        if (!s.matches("(?i)^[a-z][a-z0-9+.-]*://.*")) {
            s = "https://" + s;
        }
        URI u;
        try {
            u = new URI(s);
        } catch (java.net.URISyntaxException e) {
            return null;
        }
        if (!"https".equalsIgnoreCase(u.getScheme()) || u.getUserInfo() != null || u.getRawQuery() != null || u.getRawFragment() != null || u.getHost() == null) {
            return null;
        }
        String path = u.getRawPath() == null ? "" : u.getRawPath().replaceAll("/+$", "");
        return "https://" + u.getRawAuthority().toLowerCase(Locale.ROOT) + path;
    }

    static String directoryUrlFor(String directory) {
        return directory + DIRECTORY_PATH;
    }

    // ---- signature base ----------------------------------------------------

    private static String escapeSfString(String v) {
        return "\"" + v.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
    }

    static String serializeComponentId(Component c) {
        StringBuilder out = new StringBuilder(escapeSfString(c.name()));
        for (Map.Entry<String, Object> e : c.params().entrySet()) {
            Object v = e.getValue();
            if (Boolean.TRUE.equals(v)) {
                out.append(';').append(e.getKey());
            } else if (Boolean.FALSE.equals(v)) {
                out.append(';').append(e.getKey()).append("=?0");
            } else if (v instanceof String s) {
                out.append(';').append(e.getKey()).append('=').append(escapeSfString(s));
            } else if (v instanceof Number n) {
                out.append(';').append(e.getKey()).append('=').append(n.longValue());
            } else if (v instanceof byte[] b) {
                out.append(';').append(e.getKey()).append("=:").append(Base64.getEncoder().encodeToString(b)).append(':');
            }
        }
        return out.toString();
    }

    private static String percentEncode(String v) {
        StringBuilder sb = new StringBuilder();
        for (byte b : v.getBytes(StandardCharsets.UTF_8)) {
            int c = b & 0xff;
            if ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') || (c >= '0' && c <= '9') || "-_.~".indexOf(c) >= 0) {
                sb.append((char) c);
            } else {
                sb.append('%').append(String.format("%02X", c));
            }
        }
        return sb.toString();
    }

    /** First value of a header (case-insensitive); repeated headers are joined with {@code ", "}. */
    static String headerValue(Map<String, ?> headers, String name) {
        List<String> parts = new ArrayList<>();
        for (Map.Entry<String, ?> e : headers.entrySet()) {
            if (!e.getKey().equalsIgnoreCase(name) || e.getValue() == null) {
                continue;
            }
            Object v = e.getValue();
            if (v instanceof Iterable<?> it) {
                it.forEach(x -> parts.add(String.valueOf(x).trim()));
            } else {
                parts.add(String.valueOf(v).trim());
            }
        }
        return parts.isEmpty() ? null : String.join(", ", parts);
    }

    private static String componentValue(Component c, String method, URI url, Map<String, ?> headers) {
        String name = c.name();
        Map<String, Object> params = c.params();
        String path = url.getRawPath() == null || url.getRawPath().isEmpty() ? "/" : url.getRawPath();
        String query = url.getRawQuery() == null ? "" : "?" + url.getRawQuery();
        if (name.equals("@query-param")) {
            if (params.size() != 1 || !(params.get("name") instanceof String qn)) {
                return null;
            }
            List<String> found = new ArrayList<>();
            if (url.getRawQuery() != null) {
                for (String pair : url.getRawQuery().split("&")) {
                    int eq = pair.indexOf('=');
                    String k = URLDecoder.decode(eq < 0 ? pair : pair.substring(0, eq), StandardCharsets.UTF_8);
                    if (k.equals(qn)) {
                        found.add(URLDecoder.decode(eq < 0 ? "" : pair.substring(eq + 1), StandardCharsets.UTF_8));
                    }
                }
            }
            return found.size() == 1 ? percentEncode(found.get(0)) : null;
        }
        if (name.startsWith("@")) {
            if (!params.isEmpty()) {
                return null;
            }
            return switch (name) {
                case "@method" -> method.toUpperCase(Locale.ROOT);
                case "@authority" -> url.getRawAuthority().toLowerCase(Locale.ROOT);
                case "@path" -> path;
                case "@target-uri" -> url.toString();
                case "@scheme" -> url.getScheme().toLowerCase(Locale.ROOT);
                case "@query" -> query.isEmpty() ? "?" : query;
                case "@request-target" -> path + query;
                default -> null;
            };
        }
        String raw = headerValue(headers, name);
        if (raw == null) {
            return null;
        }
        if (params.isEmpty()) {
            return raw;
        }
        if (params.size() != 1) {
            return null;
        }
        if (Boolean.TRUE.equals(params.get("sf"))) {
            return raw; // shortcut: not re-serialized (see the class doc)
        }
        if (params.get("key") instanceof String key) {
            for (String m : splitTop(raw, ',')) {
                if (m.startsWith(key + "=")) {
                    return m.substring(key.length() + 1).trim();
                }
            }
            return null;
        }
        if (Boolean.TRUE.equals(params.get("bs"))) {
            return ":" + Base64.getEncoder().encodeToString(raw.getBytes(StandardCharsets.UTF_8)) + ":";
        }
        return null;
    }

    /** The signature base of RFC 9421 section 2.5, or an error name ({@code unsupported_component}, {@code component_missing}, {@code bad_request}) as {@code "error:<name>"}. */
    static String buildSignatureBase(String method, String urlText, Map<String, ?> headers, List<Component> components, String signatureParams) {
        URI url;
        try {
            url = new URI(urlText);
            if (url.getScheme() == null || url.getRawAuthority() == null) {
                return "error:bad_request";
            }
        } catch (java.net.URISyntaxException e) {
            return "error:bad_request";
        }
        Set<String> seen = new HashSet<>();
        StringBuilder sb = new StringBuilder();
        for (Component c : components) {
            if (!c.name().equals(c.name().toLowerCase(Locale.ROOT))) {
                return "error:unsupported_component";
            }
            if (c.name().startsWith("@") && !DERIVED.contains(c.name()) && !c.name().equals("@query-param")) {
                return "error:unsupported_component";
            }
            String id = serializeComponentId(c);
            if (!seen.add(id)) {
                return "error:unsupported_component";
            }
            if (!Set.of("sf", "key", "bs", "name").containsAll(c.params().keySet())) {
                return "error:unsupported_component";
            }
            if (c.name().startsWith("@") && !c.name().equals("@query-param") && !c.params().isEmpty()) {
                return "error:unsupported_component";
            }
            String v = componentValue(c, method, url, headers);
            if (v == null || v.contains("\r") || v.contains("\n")) {
                return "error:component_missing";
            }
            sb.append(id).append(": ").append(v).append('\n');
        }
        return sb.append("\"@signature-params\": ").append(signatureParams).toString();
    }

    // ---- verify ------------------------------------------------------------

    /**
     * Verify a request's Web Bot Auth signature. Never throws for a bad request: check {@link Result#verified()}.
     *
     * @param nowMs          epoch ms
     * @param claimNonce     when non-null and the signature has a nonce, called with a key and the expiry (epoch ms); returns false for a replay
     */
    public static Result verify(String method, String url, Map<String, ?> headers, KeySource keys, long nowMs, int skewSeconds,
                                int maxLifetimeSeconds, java.util.function.BiPredicate<String, Long> claimNonce) {
        String inputHeader = headerValue(headers, "signature-input");
        String sigHeader = headerValue(headers, "signature");
        if (inputHeader == null || sigHeader == null) {
            return new Result(false, false, "no_signature", null, null, null, null, null, null);
        }
        List<ParsedInput> inputs = parseSignatureInput(inputHeader);
        Map<String, byte[]> sigs = parseSignatureHeader(sigHeader);
        if (inputs == null || sigs == null) {
            return Result.fail("malformed", null, null, null, null, null);
        }
        ParsedInput input = inputs.stream().filter(i -> TAG.equals(i.params().get("tag")) && sigs.containsKey(i.label())).findFirst().orElse(null);
        if (input == null) {
            return Result.fail("no_web_bot_auth_signature", null, null, null, null, null);
        }
        byte[] signature = sigs.get(input.label());
        Map<String, Object> p = input.params();
        Object createdO = p.get("created"), expiresO = p.get("expires"), keyIdO = p.get("keyid"), nonceO = p.get("nonce");
        if (!(createdO instanceof Long created) || !(expiresO instanceof Long expires) || !(keyIdO instanceof String keyId)
                || keyId.isEmpty() || keyId.length() > 128 || (nonceO != null && (!(nonceO instanceof String ns) || ns.length() > 512))) {
            return Result.fail("bad_params", input.label(), null, null, null, null);
        }
        String label = input.label();
        if (p.get("alg") != null && !"ed25519".equals(p.get("alg"))) {
            return Result.fail("unsupported_algorithm", label, keyId, created, expires, null);
        }
        long nowSec = nowMs / 1000;
        if (expires <= created) {
            return Result.fail("bad_params", label, keyId, created, expires, null);
        }
        if (expires - created > maxLifetimeSeconds) {
            return Result.fail("lifetime_too_long", label, keyId, created, expires, null);
        }
        if (created > nowSec + skewSeconds) {
            return Result.fail("not_yet_valid", label, keyId, created, expires, null);
        }
        if (expires < nowSec - skewSeconds) {
            return Result.fail("expired", label, keyId, created, expires, null);
        }
        String agentHeader = headerValue(headers, "signature-agent");
        if (agentHeader == null) {
            return Result.fail("missing_signature_agent", label, keyId, created, expires, null);
        }
        List<String> names = input.components().stream().map(Component::name).toList();
        Component agentComponent = input.components().stream().filter(c -> c.name().equals("signature-agent")).findFirst().orElse(null);
        if (!names.contains("@authority") || agentComponent == null) {
            return Result.fail("components_not_covered", label, keyId, created, expires, null);
        }
        String agentKey = agentComponent.params().get("key") instanceof String k ? k : null;
        if (agentKey != null && splitTop(agentHeader, ',').stream().noneMatch(m -> m.startsWith(agentKey + "="))) {
            return Result.fail("bad_signature_agent", label, keyId, created, expires, null);
        }
        String directory = parseSignatureAgent(agentHeader, agentKey != null ? agentKey : label);
        if (directory == null) {
            return Result.fail("bad_signature_agent", label, keyId, created, expires, null);
        }
        String base = buildSignatureBase(method, url, headers, input.components(), input.raw());
        if (base.startsWith("error:")) {
            return Result.fail(base.substring(6), label, keyId, created, expires, directory);
        }
        String dirUrl = directoryUrlFor(directory);
        List<byte[]> found = keys.getKeys(dirUrl, false);
        byte[] pub = find(found, keyId);
        if (pub == null) {
            found = keys.getKeys(dirUrl, true);
            pub = find(found, keyId);
        }
        if (pub == null) {
            return Result.fail(found == null ? "directory_unavailable" : "key_not_found", label, keyId, created, expires, directory);
        }
        if (signature.length != 64 || !ed25519Verify(pub, base.getBytes(StandardCharsets.UTF_8), signature)) {
            return Result.fail("bad_signature", label, keyId, created, expires, directory);
        }
        if (claimNonce != null && nonceO instanceof String nonce
                && !claimNonce.test(directory + "|" + keyId + "|" + nonce, expires * 1000 + skewSeconds * 1000L)) {
            return Result.fail("replayed", label, keyId, created, expires, directory);
        }
        return new Result(true, true, null, directory, keyId, label, created, expires, nonceO instanceof String n ? n : null);
    }

    private static byte[] find(List<byte[]> keys, String keyId) {
        if (keys == null) {
            return null;
        }
        for (byte[] k : keys) {
            if (thumbprintOf(k).equals(keyId)) {
                return k;
            }
        }
        return null;
    }

    private static boolean ed25519Verify(byte[] rawPub, byte[] message, byte[] signature) {
        try {
            byte[] prefix = hex("302a300506032b6570032100");
            byte[] der = Arrays.copyOf(prefix, prefix.length + 32);
            System.arraycopy(rawPub, 0, der, prefix.length, 32);
            PublicKey key = KeyFactory.getInstance("Ed25519").generatePublic(new X509EncodedKeySpec(der));
            Signature s = Signature.getInstance("Ed25519");
            s.initVerify(key);
            s.update(message);
            return s.verify(signature);
        } catch (GeneralSecurityException | RuntimeException e) {
            return false;
        }
    }

    // ---- sign --------------------------------------------------------------

    /**
     * Sign a request so a site that checks Web Bot Auth can verify it. Returns the three headers to send:
     * {@code signature-agent}, {@code signature-input} and {@code signature}.
     *
     * @param seed           the 32-byte Ed25519 private key seed
     * @param signatureAgent the directory URL the bot publishes its keys under
     * @param components     covered components; must include {@code @authority} and {@code signature-agent} (null = those two)
     */
    public static Map<String, String> sign(String method, String url, Map<String, String> headers, String signatureAgent, byte[] seed,
                                           byte[] publicKey, long created, long expires, String nonce, String label, List<Component> components) {
        String lbl = label == null ? "sig1" : label;
        List<Component> comps = components != null ? components : List.of(new Component("@authority"), new Component("signature-agent"));
        String keyId = thumbprintOf(publicKey);
        String agentHeader = "\"" + signatureAgent + "\"";
        StringBuilder ids = new StringBuilder();
        for (Component c : comps) {
            ids.append(ids.length() == 0 ? "" : " ").append(serializeComponentId(c));
        }
        String params = "(" + ids + ");created=" + created + ";keyid=\"" + keyId + "\";alg=\"ed25519\";expires=" + expires
                + (nonce != null ? ";nonce=\"" + nonce + "\"" : "") + ";tag=\"" + TAG + "\"";
        Map<String, String> all = new LinkedHashMap<>(headers == null ? Map.of() : headers);
        all.put("signature-agent", agentHeader);
        String base = buildSignatureBase(method, url, all, comps, params);
        if (base.startsWith("error:")) {
            throw new IllegalArgumentException("Cannot sign: " + base.substring(6) + ".");
        }
        try {
            byte[] prefix = hex("302e020100300506032b657004220420");
            byte[] der = Arrays.copyOf(prefix, prefix.length + seed.length);
            System.arraycopy(seed, 0, der, prefix.length, seed.length);
            PrivateKey key = KeyFactory.getInstance("Ed25519").generatePrivate(new PKCS8EncodedKeySpec(der));
            Signature s = Signature.getInstance("Ed25519");
            s.initSign(key);
            s.update(base.getBytes(StandardCharsets.UTF_8));
            Map<String, String> out = new LinkedHashMap<>();
            out.put("signature-agent", agentHeader);
            out.put("signature-input", lbl + "=" + params);
            out.put("signature", lbl + "=:" + Base64.getEncoder().encodeToString(s.sign()) + ":");
            return out;
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException(e);
        }
    }

    private static byte[] sha256(byte[] in) {
        try {
            return MessageDigest.getInstance("SHA-256").digest(in);
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException(e);
        }
    }

    private static byte[] hex(String h) {
        byte[] out = new byte[h.length() / 2];
        for (int i = 0; i < out.length; i++) {
            out[i] = (byte) Integer.parseInt(h.substring(2 * i, 2 * i + 2), 16);
        }
        return out;
    }

    // ---- policy ------------------------------------------------------------

    /** Turn a verification {@link Result} into a decision against allow and block lists of directories. */
    public static Decision decide(Result result, Map<String, String> allow, Map<String, String> block) {
        if (!result.verified() || result.directory() == null || result.keyId() == null) {
            return new Decision(false, null, "unverified", "unknown", result.reason());
        }
        String blockedName = lookup(block, result.directory());
        String allowedName = blockedName != null ? null : lookup(allow, result.directory());
        String name = blockedName != null ? blockedName : allowedName;
        if (name == null || name.isBlank()) {
            name = URI.create(result.directory()).getHost();
        }
        return new Decision(true, new Agent(result.directory(), result.keyId(), name),
                blockedName != null ? "blocked" : allowedName != null ? "allowed" : "verified",
                blockedName != null ? "block" : allowedName != null ? "allow" : "unknown", null);
    }

    private static String lookup(Map<String, String> list, String directory) {
        if (list == null) {
            return null;
        }
        for (Map.Entry<String, String> e : list.entrySet()) {
            if (directory.equals(normalizeDirectory(e.getKey()))) {
                return e.getValue() == null ? "" : e.getValue();
            }
        }
        return null;
    }

    // ---- directory cache and the ready-made verifier ------------------------

    /** Fetches and caches key directories: 10 minute TTL, 1 minute failure TTL, one forced refresh per minute per directory. */
    public static final class DirectoryCache implements KeySource {
        private record Entry(List<byte[]> keys, long at, long fetchedAt) {
        }

        private final java.util.function.Function<String, JsonNode> fetchJson;
        private final long ttlMs, failureTtlMs, cooldownMs;
        private final int max;
        private final LongSupplier now;
        private final Map<String, Entry> entries = new ConcurrentHashMap<>();

        public DirectoryCache(java.util.function.Function<String, JsonNode> fetchJson, long ttlMs, long failureTtlMs, long cooldownMs, int max, LongSupplier now) {
            this.fetchJson = fetchJson;
            this.ttlMs = ttlMs;
            this.failureTtlMs = failureTtlMs;
            this.cooldownMs = cooldownMs;
            this.max = max;
            this.now = now;
        }

        @Override
        public List<byte[]> getKeys(String url, boolean refresh) {
            long t = now.getAsLong();
            Entry hit = entries.get(url);
            long ttl = hit != null && hit.keys() != null ? ttlMs : failureTtlMs;
            if (hit != null && t - hit.at() < ttl && !(refresh && t - hit.fetchedAt() >= cooldownMs)) {
                return hit.keys();
            }
            List<byte[]> keys = null;
            try {
                JsonNode doc = fetchJson.apply(url);
                List<byte[]> parsed = doc == null ? List.of() : parseDirectory(doc);
                keys = parsed.isEmpty() ? null : parsed;
            } catch (RuntimeException e) {
                keys = null;
            }
            List<byte[]> kept = keys != null ? keys : hit != null ? hit.keys() : null;
            if (entries.size() >= max && !entries.containsKey(url)) {
                entries.keySet().stream().findFirst().ifPresent(entries::remove);
            }
            entries.put(url, new Entry(kept, t, t));
            return kept;
        }

        public void clear() {
            entries.clear();
        }
    }

    public static Builder verifier() {
        return new Builder();
    }

    /** A verifier with a directory cache and an allow / block policy. */
    public static final class Verifier {
        private final DirectoryCache cache;
        private final Map<String, String> allow;
        private final Map<String, String> block;
        private final int skew;
        private final int maxLifetime;
        private final boolean rejectReplays;
        private final LongSupplier now;
        private final Map<String, Long> seen = new ConcurrentHashMap<>();

        private Verifier(Builder b) {
            this.now = b.now;
            this.allow = Map.copyOf(b.allow);
            this.block = Map.copyOf(b.block);
            this.skew = b.skewSeconds;
            this.maxLifetime = b.maxLifetimeSeconds;
            this.rejectReplays = b.rejectReplays;
            this.cache = new DirectoryCache(b.fetchJson != null ? b.fetchJson : Verifier::fetchDirectory, b.ttlMs, 60_000, b.refreshCooldownMs, 1000, b.now);
        }

        /** Verify and decide. {@code headers} may map a name to a String or a collection of Strings. */
        public Decision verify(String method, String url, Map<String, ?> headers) {
            Result r = WebBotAuth.verify(method, url, headers, cache, now.getAsLong(), skew, maxLifetime, rejectReplays ? this::claimNonce : null);
            return decide(r, allow, block);
        }

        public void clearCache() {
            cache.clear();
        }

        private boolean claimNonce(String key, Long expiresAtMs) {
            long t = now.getAsLong();
            if (seen.size() > 5000) {
                seen.values().removeIf(exp -> exp <= t);
            }
            if (seen.size() > 20_000) {
                return false; // fail closed rather than grow without bound
            }
            Long prior = seen.get(key);
            if (prior != null && prior > t) {
                return false;
            }
            seen.put(key, expiresAtMs);
            return true;
        }

        private static final HttpClient HTTP = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NEVER).connectTimeout(Duration.ofSeconds(5)).build();

        static boolean privateHostLiteral(String host) {
            String h = host.toLowerCase(Locale.ROOT).replaceAll("^\\[|\\]$", "");
            return h.equals("localhost") || h.endsWith(".localhost") || h.contains(":") || h.matches("^\\d{1,3}(\\.\\d{1,3}){3}$") || !h.contains(".");
        }

        private static JsonNode fetchDirectory(String url) {
            try {
                URI u = URI.create(url);
                if (!"https".equals(u.getScheme()) || privateHostLiteral(u.getHost())) {
                    return null;
                }
                HttpResponse<byte[]> res = HTTP.send(HttpRequest.newBuilder(u).timeout(Duration.ofSeconds(5)).header("accept", DIRECTORY_MEDIA_TYPE).GET().build(),
                        HttpResponse.BodyHandlers.ofByteArray());
                if (res.statusCode() != 200 || res.body().length > 64 * 1024) {
                    return null;
                }
                return new ObjectMapper().readTree(res.body());
            } catch (IOException | RuntimeException e) {
                return null;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return null;
            }
        }
    }

    public static final class Builder {
        private final Map<String, String> allow = new LinkedHashMap<>();
        private final Map<String, String> block = new LinkedHashMap<>();
        private int skewSeconds = DEFAULT_SKEW_SECONDS;
        private int maxLifetimeSeconds = DEFAULT_MAX_LIFETIME_SECONDS;
        private boolean rejectReplays;
        private long ttlMs = 10 * 60_000;
        private long refreshCooldownMs = 60_000;
        private java.util.function.Function<String, JsonNode> fetchJson;
        private LongSupplier now = System::currentTimeMillis;

        /** Allow a bot by its key directory (a URL or bare host); {@code name} labels it in decisions. */
        public Builder allow(String directory, String name) {
            allow.put(directory, name);
            return this;
        }

        /** Block a bot by its key directory. */
        public Builder block(String directory, String name) {
            block.put(directory, name);
            return this;
        }

        public Builder skewSeconds(int s) {
            this.skewSeconds = s;
            return this;
        }

        public Builder maxLifetimeSeconds(int s) {
            this.maxLifetimeSeconds = s;
            return this;
        }

        /** Refuse a signature whose nonce was seen before (in memory, per process). Off by default: some bots reuse one signed request until it expires. */
        public Builder rejectReplays(boolean reject) {
            this.rejectReplays = reject;
            return this;
        }

        public Builder directoryTtlMs(long ms) {
            this.ttlMs = ms;
            return this;
        }

        public Builder refreshCooldownMs(long ms) {
            this.refreshCooldownMs = ms;
            return this;
        }

        /** Replace the directory fetcher (tests); it returns the parsed document or null. */
        public Builder fetchJson(java.util.function.Function<String, JsonNode> fetchJson) {
            this.fetchJson = fetchJson;
            return this;
        }

        public Builder clock(LongSupplier now) {
            this.now = now;
            return this;
        }

        public Verifier build() {
            return new Verifier(this);
        }
    }
}
