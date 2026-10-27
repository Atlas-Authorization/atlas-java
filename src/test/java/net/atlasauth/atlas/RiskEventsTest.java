package net.atlasauth.atlas;

import net.atlasauth.atlas.models.FraudLookup;
import net.atlasauth.atlas.models.FraudLookupBody;
import net.atlasauth.atlas.models.RiskEvent;
import net.atlasauth.atlas.models.RiskEventMetrics;
import net.atlasauth.atlas.models.UpdateRiskBasedMfaBody;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** The risk-event timeline, event feedback, fraud lookup and the extra adaptive-MFA config fields. */
class RiskEventsTest {
    private static final String EVENT = "{\"object\":\"risk_event\",\"id\":\"re_1\",\"user_id\":\"usr_1\",\"level\":\"high\",\"score\":80,"
            + "\"signals\":[\"newDevice\"],\"action\":\"mfa\",\"mode\":\"audit\",\"ip\":\"1.2.3.4\",\"feedback\":null,\"feedback_at\":null,"
            + "\"created_at\":\"2026-01-01T00:00:00Z\"";

    private MockBackend mock;
    private AtlasClient atlas;

    @BeforeEach
    void setUp() throws IOException {
        mock = MockBackend.start();
        atlas = AtlasClient.builder().secretKey("sk_test_123").apiUrl(mock.baseUrl()).build();
    }

    @AfterEach
    void tearDown() {
        mock.close();
    }

    @Test
    void timelineMetricsAndFeedback() {
        mock.reply(200, "{\"object\":\"list\",\"data\":[" + EVENT + "}]}");
        var page = atlas.riskBasedMfa().listEvents("usr_1", "high", 10, null);
        assertEquals("GET", mock.method);
        assertEquals("/v1/risk_events", mock.path);
        assertEquals("user_id=usr_1&level=high&limit=10", mock.rawQuery);
        RiskEvent e = page.data().get(0);
        assertEquals("usr_1", e.userId());
        assertEquals("newDevice", e.signals().get(0));
        assertNull(e.feedback());

        mock.reply(200, "{\"object\":\"risk_event_metrics\",\"days\":14,\"total\":3,\"buckets\":[{\"level\":\"high\",\"action\":\"mfa\",\"mode\":\"audit\",\"count\":3}]}");
        RiskEventMetrics m = atlas.riskBasedMfa().eventMetrics(14);
        assertEquals("/v1/risk_events/metrics", mock.path);
        assertEquals("days=14", mock.rawQuery);
        assertEquals(3, m.buckets().get(0).count());

        mock.reply(200, EVENT.replace("\"feedback\":null", "\"feedback\":\"fraud\"") + "}");
        RiskEvent f = atlas.riskBasedMfa().eventFeedback("re_1", "fraud");
        assertEquals("POST", mock.method);
        assertEquals("/v1/risk_events/re_1/feedback", mock.path);
        assertTrue(mock.requestBody.contains("\"verdict\":\"fraud\""));
        assertEquals("fraud", f.feedback());
    }

    @Test
    void fraudLookupAndConfigFields() {
        mock.reply(200, "{\"object\":\"fraud_lookup\",\"verdict\":\"challenge\",\"reasons\":[\"r\"],\"matched_rules\":[\"fr_1\"],\"device_id\":\"d1\",\"signals\":{\"country\":\"US\"}}");
        FraudLookup l = atlas.riskBasedMfa().fraudLookup(new FraudLookupBody("1.2.3.4").userAgent("ua").deviceId("d1"));
        assertEquals("POST", mock.method);
        assertEquals("/v1/fraud/lookup", mock.path);
        assertTrue(mock.requestBody.contains("\"ip\":\"1.2.3.4\""));
        assertTrue(mock.requestBody.contains("\"user_agent\":\"ua\""));
        assertTrue(mock.requestBody.contains("\"device_id\":\"d1\""));
        assertEquals("challenge", l.verdict());
        assertEquals("fr_1", l.matchedRules().get(0));

        mock.reply(200, "{\"object\":\"risk_based_mfa\",\"enabled\":true,\"step_up_threshold\":\"medium\",\"on_high_risk_no_factor\":\"block\","
                + "\"weights\":{},\"signals\":{},\"mode\":\"audit\",\"level_actions\":{\"high\":\"block\"},\"mid_session\":{\"enabled\":true,\"action\":\"revoke\"}}");
        var cfg = atlas.riskBasedMfa().update(new UpdateRiskBasedMfaBody().mode("audit").midSession(Map.of("enabled", true)));
        assertEquals("PATCH", mock.method);
        assertTrue(mock.requestBody.contains("\"mode\":\"audit\""));
        assertTrue(mock.requestBody.contains("\"mid_session\":{\"enabled\":true}"));
        assertEquals("audit", cfg.mode());
        assertEquals("revoke", cfg.midSession().get("action"));
    }
}
