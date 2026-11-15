package net.atlasauth.atlas.webbotauth;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.EdECPrivateKey;
import java.util.Arrays;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class WebBotAuthTest {

    private static final String DIRECTORY = "https://bot.example";

    private record Bot(byte[] seed, byte[] pub, String x) {
    }

    private static Bot bot() throws Exception {
        KeyPair kp = KeyPairGenerator.getInstance("Ed25519").generateKeyPair();
        byte[] seed = ((EdECPrivateKey) kp.getPrivate()).getBytes().orElseThrow();
        byte[] der = kp.getPublic().getEncoded();
        byte[] pub = Arrays.copyOfRange(der, der.length - 32, der.length);
        return new Bot(seed, pub, Base64.getUrlEncoder().withoutPadding().encodeToString(pub));
    }

    private static WebBotAuth.Verifier verifier(Bot bot, long now, boolean replays) {
        return WebBotAuth.verifier().clock(() -> now).rejectReplays(replays).allow(DIRECTORY, "ExampleBot").fetchJson(url -> {
            assertEquals(DIRECTORY + "/.well-known/http-message-signatures-directory", url);
            try {
                return new ObjectMapper().readTree("{\"keys\":[{\"kty\":\"OKP\",\"crv\":\"Ed25519\",\"x\":\"" + bot.x() + "\"}]}");
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        }).build();
    }

    @Test
    void aSignedRequestVerifiesAndIsAllowedByPolicy() throws Exception {
        Bot bot = bot();
        long now = 1_800_000_000_000L;
        Map<String, String> headers = new LinkedHashMap<>(WebBotAuth.sign("GET", "https://site.example/page?q=1", null, DIRECTORY, bot.seed(), bot.pub(),
                now / 1000, now / 1000 + 300, "n1", null, null));
        WebBotAuth.Decision d = verifier(bot, now, true).verify("GET", "https://site.example/page?q=1", headers);
        assertTrue(d.verified(), d.reason());
        assertEquals("allow", d.decision());
        assertEquals("ExampleBot", d.agent().name());
    }

    @Test
    void aTamperedRequestAndAReplayAreRefused() throws Exception {
        Bot bot = bot();
        long now = 1_800_000_000_000L;
        Map<String, String> headers = WebBotAuth.sign("GET", "https://site.example/a", null, DIRECTORY, bot.seed(), bot.pub(), now / 1000, now / 1000 + 300, "n2", null, null);
        WebBotAuth.Verifier v = verifier(bot, now, true);
        assertEquals("bad_signature", WebBotAuth.verify("GET", "https://evil.example/a", headers, (u, r) -> List.of(bot.pub()), now, 30, 3600, null).reason());
        assertTrue(v.verify("GET", "https://site.example/a", headers).verified());
        assertEquals("replayed", v.verify("GET", "https://site.example/a", headers).reason());
        assertEquals("expired", WebBotAuth.verify("GET", "https://site.example/a", headers, (u, r) -> List.of(bot.pub()), now + 10_000_000, 30, 3600, null).reason());
        assertEquals("no_signature", WebBotAuth.verify("GET", "https://site.example/a", Map.of(), (u, r) -> null, now, 30, 3600, null).reason());
    }

    @Test
    void directoryNormalisationRejectsUnsafeReferences() {
        assertEquals("https://bot.example", WebBotAuth.normalizeDirectory("bot.example/"));
        assertNull(WebBotAuth.normalizeDirectory("http://bot.example"));
        assertNull(WebBotAuth.normalizeDirectory("https://u:p@bot.example"));
        assertEquals("https://bot.example", WebBotAuth.parseSignatureAgent("sig1=\"https://bot.example\"", "sig1"));
    }
}
