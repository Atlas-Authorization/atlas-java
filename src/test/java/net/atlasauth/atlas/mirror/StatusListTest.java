package net.atlasauth.atlas.mirror;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.atlasauth.atlas.TestJwt;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.*;

class StatusListTest {

    private static final ObjectMapper M = new ObjectMapper();
    private static final String ISS = "https://clerk.example.com";

    static String token(TestJwt key, String typ, List<String> sids, boolean truncated, long nowSec, String iss) throws Exception {
        List<byte[]> hashes = sids.stream().map(s -> {
            try {
                return Arrays.copyOf(MessageDigest.getInstance("SHA-256").digest(s.getBytes(StandardCharsets.UTF_8)), 8);
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        }).sorted(Arrays::compareUnsigned).toList();
        byte[] all = new byte[hashes.size() * 8];
        for (int i = 0; i < hashes.size(); i++) {
            System.arraycopy(hashes.get(i), 0, all, i * 8, 8);
        }
        String claims = "{\"iss\":\"" + iss + "\",\"aud\":\"atlas:session-status-list\",\"token_use\":\"session_status_list\",\"iat\":" + nowSec + ",\"exp\":" + (nowSec + 60)
                + ",\"since\":" + (nowSec - 3600) + ",\"hash_bytes\":8,\"count\":" + hashes.size() + ",\"truncated\":" + truncated + ",\"h\":\"" + Base64.getUrlEncoder().withoutPadding().encodeToString(all) + "\"}";
        return key.sign(key.alg, typ, claims);
    }

    @Test
    void verifiesAndLooksUpUnderEveryAlgorithm() throws Exception {
        long now = System.currentTimeMillis();
        for (String alg : TestJwt.ALGS) {
            TestJwt key = TestJwt.generate(alg);
            JsonNode jwks = M.readTree(key.jwksJson());
            StatusList l = StatusList.verify(token(key, "statuslist+jwt", List.of("s1", "s2", "s3"), false, now / 1000, ISS), jwks, ISS, now, 30);
            assertTrue(l.has("s2"), alg);
            assertFalse(l.has("s9"), alg);
            assertEquals(3, l.count());
        }
    }

    @Test
    void refusesWrongIssuerTypeKeyFamilyAndTampering() throws Exception {
        long now = System.currentTimeMillis();
        TestJwt rsa = TestJwt.generate("RS256");
        TestJwt ec = TestJwt.generate("ES256");
        JsonNode rsaJwks = M.readTree(rsa.jwksJson());
        String good = token(rsa, "statuslist+jwt", List.of("s1"), false, now / 1000, ISS);
        assertEquals("bad_claims", assertThrows(StatusList.StatusListException.class, () -> StatusList.verify(good, rsaJwks, "https://other", now, 30)).reason());
        assertEquals("unsupported", assertThrows(StatusList.StatusListException.class, () -> StatusList.verify(token(rsa, "jwt", List.of(), false, now / 1000, ISS), rsaJwks, ISS, now, 30)).reason());
        // an EC token against a JWKS that only holds an RSA key: no key of the right type
        assertEquals("no_key", assertThrows(StatusList.StatusListException.class, () -> StatusList.verify(token(ec, "statuslist+jwt", List.of(), false, now / 1000, ISS), rsaJwks, ISS, now, 30)).reason());
        String[] p = good.split("\\.");
        String tampered = p[0] + "." + Base64.getUrlEncoder().withoutPadding().encodeToString("{\"aud\":\"x\"}".getBytes()) + "." + p[2];
        assertEquals("bad_signature", assertThrows(StatusList.StatusListException.class, () -> StatusList.verify(tampered, rsaJwks, ISS, now, 30)).reason());
        assertEquals("expired", assertThrows(StatusList.StatusListException.class, () -> StatusList.verify(good, rsaJwks, ISS, now + 3_600_000, 30)).reason());
    }

    @Test
    void sidListUsesTheSignedListAndFallsBackToTheFeedWhenItCannotAnswer() throws Exception {
        long nowMs = System.currentTimeMillis();
        TestJwt key = TestJwt.generate("ES384");
        com.sun.net.httpserver.HttpServer jwksServer = com.sun.net.httpserver.HttpServer.create(new java.net.InetSocketAddress("127.0.0.1", 0), 0);
        jwksServer.createContext("/jwks", ex -> {
            byte[] b = key.jwksJson().getBytes(StandardCharsets.UTF_8);
            ex.sendResponseHeaders(200, b.length);
            ex.getResponseBody().write(b);
            ex.close();
        });
        jwksServer.start();
        try {
            String[] listToken = {token(key, "statuslist+jwt", List.of("revoked-sid"), false, nowMs / 1000, ISS)};
            int[] feedCalls = {0};
            Backend b = new Backend() {
                public JsonNode createApiKey(Map<String, Object> body, String k) { return null; }
                public void deleteApiKey(String id) { }
                public JsonNode listRevokedApiKeys(String s, int l) { return null; }
                @Override
                public JsonNode getSessionStatusList(Integer w) {
                    try {
                        return M.readTree("{\"token\":\"" + listToken[0] + "\"}");
                    } catch (Exception e) {
                        throw new IllegalStateException(e);
                    }
                }
                @Override
                public JsonNode listRevokedSessions(Long since, String cursor, int limit) {
                    feedCalls[0]++;
                    try {
                        return M.readTree("{\"data\":[{\"sid\":\"feed-sid\",\"revoked_at\":" + nowMs + "}],\"has_more\":false,\"server_time\":" + nowMs + "}");
                    } catch (Exception e) {
                        throw new IllegalStateException(e);
                    }
                }
            };
            AtomicLong clock = new AtomicLong(nowMs);
            SidRevocationList list = SidRevocationList.builder(b).intervalMs(1000)
                    .statusList("http://127.0.0.1:" + jwksServer.getAddress().getPort() + "/jwks", ISS).clock(clock::get).build();
            assertTrue(list.isRevoked("revoked-sid"));
            assertFalse(list.isRevoked("fine"));
            assertEquals(0, feedCalls[0], "the signed list answered");
            // a truncated list proves nothing about a miss: the feed answers
            listToken[0] = token(key, "statuslist+jwt", List.of("x"), true, nowMs / 1000, ISS);
            clock.addAndGet(2000);
            assertTrue(list.isRevoked("feed-sid"));
            assertTrue(feedCalls[0] > 0);
        } finally {
            jwksServer.stop(0);
        }
    }
}
