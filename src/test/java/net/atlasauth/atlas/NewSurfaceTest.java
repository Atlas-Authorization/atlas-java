package net.atlasauth.atlas;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.atlasauth.atlas.http.UnknownFieldsWarning;
import net.atlasauth.atlas.models.AtlasDesign;
import net.atlasauth.atlas.models.AtlasDesignPalette;
import net.atlasauth.atlas.models.AtlasIdAppBrandingInput;
import net.atlasauth.atlas.models.AtlasIdAppCopy;
import net.atlasauth.atlas.models.AtlasIdAppWithSecret;
import net.atlasauth.atlas.models.CookieConsent;
import net.atlasauth.atlas.models.CookieConsentBody;
import net.atlasauth.atlas.models.CookieConsentSettingsLink;
import net.atlasauth.atlas.models.CookieConsentSettingsLinkInput;
import net.atlasauth.atlas.models.CreateAtlasIdAppBody;
import net.atlasauth.atlas.models.CreateEntitlementOverrideBody;
import net.atlasauth.atlas.models.CursorPage;
import net.atlasauth.atlas.models.DesignAsset;
import net.atlasauth.atlas.models.DesignCatalog;
import net.atlasauth.atlas.models.DesignSettings;
import net.atlasauth.atlas.models.EndUserPoolPolicy;
import net.atlasauth.atlas.models.EntitlementOverride;
import net.atlasauth.atlas.models.ListMachinesParams;
import net.atlasauth.atlas.models.ListPage;
import net.atlasauth.atlas.models.Machine;
import net.atlasauth.atlas.models.OAuthProvider;
import net.atlasauth.atlas.models.OrgPolicy;
import net.atlasauth.atlas.models.OrganizationEntitlements;
import net.atlasauth.atlas.models.OrganizationPolicy;
import net.atlasauth.atlas.models.OrganizationPolicyPatch;
import net.atlasauth.atlas.models.Preflight;
import net.atlasauth.atlas.models.UpdateAtlasIdAppBody;
import net.atlasauth.atlas.models.UpdateWebhookEndpointBody;
import net.atlasauth.atlas.models.UpsertOAuthProviderBody;
import net.atlasauth.atlas.models.WebhookDeliveryQueued;
import net.atlasauth.atlas.models.WebhookEndpointList;
import net.atlasauth.atlas.models.WebhookSecret;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Request shaping and response decoding for the 0.3.0 surface (strict bodies, platform, design, …). */
class NewSurfaceTest {

    private static final ObjectMapper JSON = new ObjectMapper();

    private MockBackend mock;
    private AtlasClient atlas;

    @BeforeEach
    void setUp() throws IOException {
        mock = MockBackend.start();
        atlas = AtlasClient.builder()
                .secretKey("sk_test_123")
                .platformKey("psk_test_456")
                .apiUrl(mock.baseUrl())
                .build();
    }

    @AfterEach
    void tearDown() {
        mock.close();
    }

    private JsonNode sent() throws IOException {
        return JSON.readTree(mock.requestBody);
    }

    // ---- strict bodies + unknown fields ------------------------------------

    @Test
    void strictIsNotSentByDefault() {
        mock.reply(200, "{\"object\":\"redirect_url\",\"id\":\"ru_1\",\"url\":\"https://a\"}");
        atlas.redirectUrls().create("https://a");
        assertNull(mock.header("Atlas-Strict"));
    }

    @Test
    void strictClientSendsHeaderOnWritesOnly() throws IOException {
        AtlasClient strict = AtlasClient.builder().secretKey("sk_test_123").apiUrl(mock.baseUrl()).strict(true).build();
        mock.reply(200, "{\"object\":\"redirect_url\",\"id\":\"ru_1\",\"url\":\"https://a\"}");
        strict.redirectUrls().create("https://a");
        assertEquals("true", mock.header("Atlas-Strict"));
        assertEquals("POST", mock.method);

        mock.reply(200, "{\"object\":\"list\",\"data\":[]}");
        strict.redirectUrls().list();
        assertNull(mock.header("Atlas-Strict"), "GET carries no body, so no strict header");
    }

    @Test
    void withStrictOverridesPerCall() {
        mock.reply(200, "{\"object\":\"redirect_url\",\"id\":\"ru_1\",\"url\":\"https://a\"}");
        atlas.withStrict(false).redirectUrls().create("https://a");
        assertEquals("false", mock.header("Atlas-Strict"));
        atlas.withStrict(true).redirectUrls().create("https://a");
        assertEquals("true", mock.header("Atlas-Strict"));
        atlas.redirectUrls().create("https://a");
        assertNull(mock.header("Atlas-Strict"), "the original client is unchanged");
    }

    @Test
    void unknownFieldsHeaderReachesTheCallback() {
        List<UnknownFieldsWarning> seen = new ArrayList<>();
        AtlasClient client = AtlasClient.builder().secretKey("sk_test_123").apiUrl(mock.baseUrl())
                .onUnknownFields(seen::add).build();
        mock.responseHeaders.put("Atlas-Unknown-Fields", "colour, branding.font");
        mock.reply(200, "{\"object\":\"redirect_url\",\"id\":\"ru_1\",\"url\":\"https://a\"}");
        client.redirectUrls().create("https://a");
        assertEquals(1, seen.size());
        assertEquals("POST", seen.get(0).method());
        assertEquals("/v1/redirect_urls", seen.get(0).path());
        assertEquals(List.of("colour", "branding.font"), seen.get(0).fields());
    }

    @Test
    void strictRefusalSurfacesUnknownFieldCode() {
        mock.reply(422, "{\"errors\":[{\"code\":\"UNKNOWN_FIELD\",\"message\":\"colour is not accepted\",\"param\":\"colour\"}]}");
        AtlasApiException e = assertThrows(AtlasApiException.class,
                () -> atlas.withStrict(true).redirectUrls().create("https://a"));
        assertEquals(ErrorCodes.UNKNOWN_FIELD, e.code());
        assertEquals(422, e.status());
    }

    // ---- platform: Sign in with Atlas apps ---------------------------------

    private static final String ATLAS_ID_APP = """
            {"object":"atlas_id_app","id":"aia_1","client_id":"atlas_client_1","name":"Acme",
             "logo_url":null,"homepage_url":"https://acme.test",
             "branding":{"theme":"dark","accent_color":"#112233","background_color":null,
               "privacy_policy_url":"https://acme.test/privacy","terms_of_service_url":null,"design":null,
               "copy":{"sign_in_title":"Welcome to {app}"}},
             "redirect_uris":["https://acme.test/cb"],"post_logout_redirect_uris":["https://acme.test/bye"],
             "backchannel_logout_uri":"https://acme.test/bcl","backchannel_logout_session_required":true,
             "frontchannel_logout_uri":null,"frontchannel_logout_session_required":false,
             "allowed_scopes":["openid","email"],"created_at":1,"updated_at":2,"client_secret":"secret_x"}
            """;

    @Test
    void createAtlasIdAppUsesThePlatformKeyAndSendsLogoutAndBranding() throws IOException {
        mock.reply(200, ATLAS_ID_APP);
        CreateAtlasIdAppBody body = new CreateAtlasIdAppBody()
                .name("Acme")
                .redirectUris(List.of("https://acme.test/cb"))
                .postLogoutRedirectUris(List.of("https://acme.test/bye"))
                .backchannelLogoutUri("https://acme.test/bcl")
                .backchannelLogoutSessionRequired(true)
                .frontchannelLogoutUri("https://acme.test/fcl")
                .branding(new AtlasIdAppBrandingInput()
                        .theme("dark")
                        .accentColor("#112233")
                        .copy(new AtlasIdAppCopy().signInTitle("Welcome to {app}"))
                        .design(new AtlasDesign().preset("glass")
                                .palette(new AtlasDesignPalette().primary("#112233"))));

        AtlasIdAppWithSecret app = atlas.platform().atlasIdApps().create(body, "idem-1");

        assertEquals("POST", mock.method);
        assertEquals("/v1/platform/atlas_id_apps", mock.path);
        assertEquals("Bearer psk_test_456", mock.authorization);
        assertEquals("idem-1", mock.idempotencyKey);
        JsonNode j = sent();
        assertEquals("https://acme.test/bye", j.at("/post_logout_redirect_uris/0").asText());
        assertEquals("https://acme.test/bcl", j.get("backchannel_logout_uri").asText());
        assertTrue(j.get("backchannel_logout_session_required").asBoolean());
        assertEquals("https://acme.test/fcl", j.get("frontchannel_logout_uri").asText());
        assertEquals("#112233", j.at("/branding/accent_color").asText());
        assertEquals("Welcome to {app}", j.at("/branding/copy/sign_in_title").asText());
        // the design object keeps its camelCase wire keys
        assertEquals("glass", j.at("/branding/design/preset").asText());
        assertEquals("#112233", j.at("/branding/design/palette/primary").asText());
        assertFalse(j.has("frontchannel_logout_session_required"), "unset fields are omitted");

        assertEquals("atlas_client_1", app.clientId());
        assertEquals("dark", app.branding().theme());
        assertEquals("Welcome to {app}", app.branding().copy().signInTitle);
        assertEquals(List.of("https://acme.test/bye"), app.postLogoutRedirectUris());
        assertTrue(app.backchannelLogoutSessionRequired());
    }

    @Test
    void updateAtlasIdAppCanClearAFieldWithExplicitNull() throws IOException {
        mock.reply(200, ATLAS_ID_APP);
        atlas.platform().atlasIdApps().update("aia_1", new UpdateAtlasIdAppBody()
                .name("Acme 2")
                .set("frontchannel_logout_uri", null));
        assertEquals("PATCH", mock.method);
        assertEquals("/v1/platform/atlas_id_apps/aia_1", mock.path);
        JsonNode j = sent();
        assertEquals("Acme 2", j.get("name").asText());
        assertTrue(j.has("frontchannel_logout_uri") && j.get("frontchannel_logout_uri").isNull());
    }

    @Test
    void atlasIdAppRotateDeleteAndPreview() {
        mock.reply(200, ATLAS_ID_APP);
        atlas.platform().atlasIdApps().rotateSecret("aia_1");
        assertEquals("/v1/platform/atlas_id_apps/aia_1/rotate_secret", mock.path);
        assertEquals("POST", mock.method);

        mock.reply(200, "{\"object\":\"atlas_id_app\",\"id\":\"aia_1\",\"deleted\":true}");
        assertTrue(atlas.platform().atlasIdApps().delete("aia_1").deleted());
        assertEquals("DELETE", mock.method);

        mock.reply(200, "{\"object\":\"design_preview\",\"scope\":\".atlas-root\",\"css\":\"--x\",\"data_attributes\":{},"
                + "\"font_stylesheets\":[],\"warnings\":[],\"html\":\"<div/>\"}");
        assertEquals(".atlas-root", atlas.platform().atlasIdApps().previewDesign(new AtlasDesign().preset("bold")).scope());
        assertEquals("/v1/platform/atlas_id_apps/design/preview", mock.path);
    }

    @Test
    void platformCallsWithoutAPlatformKeyFailClearly() {
        AtlasClient secretOnly = AtlasClient.builder().secretKey("sk_test_123").apiUrl(mock.baseUrl()).build();
        IllegalStateException e = assertThrows(IllegalStateException.class,
                () -> secretOnly.platform().atlasIdApps().list());
        assertTrue(e.getMessage().contains("platformKey"));
        assertNull(mock.method, "nothing was sent");
    }

    @Test
    void platformOnlyClientIsAllowed() {
        AtlasClient platformOnly = AtlasClient.builder().platformKey("psk_x").apiUrl(mock.baseUrl()).build();
        mock.reply(200, "{\"object\":\"list\",\"data\":[]}");
        platformOnly.platform().apps().list();
        assertEquals("Bearer psk_x", mock.authorization);
        assertThrows(IllegalStateException.class, () -> platformOnly.users().get("usr_1"));
    }

    // ---- raw uploads -------------------------------------------------------

    private static final String ASSET = "{\"object\":\"design_asset\",\"asset_id\":\"dasset_1\",\"kind\":\"logo\","
            + "\"url\":\"https://cdn/x.webp\",\"width\":10,\"height\":20,\"dominant_color\":null,\"bytes\":99,\"created_at\":\"2026-10-13T00:00:00Z\"}";

    @Test
    void designAssetUploadSendsRawBytesWithTheContentType() {
        mock.reply(201, ASSET);
        byte[] png = {(byte) 0x89, 'P', 'N', 'G', 1, 2, 3};
        DesignAsset asset = atlas.design().uploadAsset(png, "image/png", "logo");
        assertEquals("POST", mock.method);
        assertEquals("/v1/design/assets", mock.path);
        assertEquals("kind=logo", mock.rawQuery);
        assertEquals("image/png", mock.header("Content-Type"));
        assertArrayEquals(png, mock.requestBytes);
        assertEquals("dasset_1", asset.assetId());
        assertEquals(20L, asset.height());
    }

    @Test
    void platformDesignAssetUploadUsesThePlatformKey() {
        mock.reply(201, ASSET);
        atlas.platform().designAssets().upload(new byte[]{1}, "image/webp");
        assertEquals("/v1/platform/design/assets", mock.path);
        assertEquals("image/webp", mock.header("Content-Type"));
        assertEquals("Bearer psk_test_456", mock.authorization);
        assertNull(mock.rawQuery, "kind omitted defaults server-side");
    }

    // ---- design ------------------------------------------------------------

    private static final String DESIGN_SETTINGS = """
            {"object":"design","design":{"preset":"glass","colorScheme":"dark","palette":{"primary":"#123456"}},
             "resolved":{"version":1,"preset":"glass","colorScheme":"dark",
               "palette":{"primary":"#123456","primaryInk":"#ffffff"},"paletteDark":{"primary":"#654321"},
               "typography":{"font":"inter","baseSize":16},"shape":{"radius":8},"elevation":{"cardShadow":"md"},
               "spacing":{"density":"comfortable"},"card":{"style":"glass","blur":12,"width":"normal"},
               "layout":{"position":"center","splitImageSide":"left","logoPlacement":"card","logoSize":"md"},
               "buttons":{"style":"solid","socialLayout":"list"},"inputs":{"style":"outline"},
               "background":{"color":"#000000"},"backgroundDark":{"color":"#000000"},"logo":{}},
             "warnings":[{"code":"LOW_CONTRAST","message":"low"}],"legacy":false}
            """;

    @Test
    void designGetDecodesTypedSections() {
        mock.reply(200, DESIGN_SETTINGS);
        DesignSettings d = atlas.design().get();
        assertEquals("/v1/instance/design", mock.path);
        assertEquals("dark", d.design().colorScheme);
        assertEquals("#123456", d.design().palette.primary);
        assertEquals("#ffffff", d.resolved().palette().primaryInk);
        assertEquals(16L, d.resolved().typography().baseSize);
        assertEquals("md", d.resolved().elevation().cardShadow);
        assertFalse(d.legacy());
    }

    @Test
    void designPatchSendsCamelCaseAndExplicitNulls() throws IOException {
        mock.reply(200, DESIGN_SETTINGS);
        atlas.design().update(new AtlasDesign().colorScheme("light").set("paletteDark", null));
        assertEquals("PATCH", mock.method);
        JsonNode j = sent();
        assertEquals("light", j.at("/design/colorScheme").asText());
        assertTrue(j.get("design").has("paletteDark") && j.at("/design/paletteDark").isNull());
    }

    @Test
    void organizationDesignAndPreviewPaths() {
        mock.reply(200, DESIGN_SETTINGS);
        atlas.design().replaceForOrganization("org_1", new AtlasDesign().preset("minimal"));
        assertEquals("PUT", mock.method);
        assertEquals("/v1/organizations/org_1/design", mock.path);
        atlas.design().getForOrganization("org_1");
        assertEquals("GET", mock.method);

        mock.reply(200, "{\"object\":\"design_preview\",\"scope\":\".atlas-root\",\"css\":\"\",\"html\":\"\"}");
        atlas.design().preview(new AtlasDesign().preset("bold"));
        assertEquals("/v1/instance/design/preview", mock.path);
    }

    @Test
    void designCatalogDecodes() {
        mock.reply(200, "{\"object\":\"design_catalog\",\"version\":1,"
                + "\"presets\":[{\"id\":\"glass\",\"label\":\"Glass\",\"description\":\"d\",\"resolved\":{\"preset\":\"glass\"}}],"
                + "\"fonts\":[{\"id\":\"inter\",\"label\":\"Inter\",\"family\":\"Inter\",\"category\":\"sans\",\"web_font\":true,"
                + "\"stylesheet\":\"/v1/design/fonts/inter.css\"}],"
                + "\"css_variables\":[{\"name\":\"--atlas-color-primary\",\"type\":\"color\",\"perMode\":true,\"description\":\"x\"}]}");
        DesignCatalog c = atlas.design().catalog();
        assertEquals("/v1/design/catalog", mock.path);
        assertEquals("glass", c.presets().get(0).resolved().preset());
        assertTrue(c.fonts().get(0).webFont());
        assertTrue(c.cssVariables().get(0).perMode());
    }

    @Test
    void designAssetsListAndDelete() {
        mock.reply(200, "{\"object\":\"list\",\"data\":[" + ASSET + "]}");
        assertEquals("logo", atlas.design().listAssets().data().get(0).kind());
        mock.reply(200, "{\"object\":\"design_asset\",\"asset_id\":\"dasset_1\",\"deleted\":true}");
        assertTrue(atlas.design().deleteAsset("dasset_1").deleted());
        assertEquals("DELETE", mock.method);
        assertEquals("/v1/design/assets/dasset_1", mock.path);
    }

    // ---- preflight + cookie consent ----------------------------------------

    @Test
    void preflightDecodesTypedFindings() {
        mock.reply(200, """
                {"object":"preflight","ok":false,"checked_at":1700000000000,
                 "findings":[{"severity":"error","code":"NO_SIGN_IN_METHOD","message":"No sign-in method is enabled.",
                              "fix":"Enable one under strategies.","subject":{"type":"instance"}}]}
                """);
        Preflight p = atlas.instance().preflight();
        assertEquals("/v1/instance/preflight", mock.path);
        assertFalse(p.ok());
        assertEquals("error", p.findings().get(0).severity());
        assertEquals("NO_SIGN_IN_METHOD", p.findings().get(0).code());
        assertEquals("Enable one under strategies.", p.findings().get(0).fix());
        assertEquals("instance", p.findings().get(0).subject().get("type"));
    }

    @Test
    void cookieConsentRoundTrip() throws IOException {
        String body = "{\"object\":\"cookie_consent\",\"enabled\":true,\"script_url\":\"https://cmp.test/s.js\","
                + "\"site_id\":\"site_1\",\"blocking_mode\":\"auto\",\"extra_origins\":[\"https://cmp.test\"]}";
        mock.reply(200, body);
        CookieConsent cc = atlas.instance().replaceCookieConsent(new CookieConsentBody()
                .scriptUrl("https://cmp.test/s.js").siteId("site_1").blockingMode("auto")
                .extraOrigins(List.of("https://cmp.test")));
        assertEquals("PUT", mock.method);
        assertEquals("/v1/instance/cookie_consent", mock.path);
        assertEquals("site_1", sent().get("site_id").asText());
        assertTrue(cc.enabled());
        assertEquals(List.of("https://cmp.test"), cc.extraOrigins());

        atlas.instance().getCookieConsent();
        assertEquals("GET", mock.method);
        mock.reply(200, "{\"object\":\"cookie_consent\",\"enabled\":false}");
        assertFalse(atlas.instance().deleteCookieConsent().enabled());
        assertEquals("DELETE", mock.method);
    }

    @Test
    void cookieConsentSettingsLinkRoundTrip() throws IOException {
        mock.reply(200, "{\"object\":\"cookie_consent\",\"enabled\":true,\"script_url\":\"https://cmp.test/s.js\","
                + "\"settings_link\":{\"label\":\"Cookie settings\",\"preset\":\"cookiebot\","
                + "\"attributes\":{\"data-track\":\"footer\"},"
                + "\"rendered_attributes\":{\"data-track\":\"footer\",\"onclick\":\"Cookiebot.renew()\"}}}");
        CookieConsent cc = atlas.instance().replaceCookieConsent(new CookieConsentBody()
                .scriptUrl("https://cmp.test/s.js")
                .settingsLink(new CookieConsentSettingsLinkInput("Cookie settings")
                        .preset(CookieConsentSettingsLink.PRESET_COOKIEBOT)
                        .attributes(Map.of("data-track", "footer"))));

        assertEquals(JSON.readTree("{\"label\":\"Cookie settings\",\"preset\":\"cookiebot\","
                + "\"attributes\":{\"data-track\":\"footer\"}}"), sent().get("settings_link"));
        CookieConsentSettingsLink link = cc.settingsLink();
        assertEquals("Cookie settings", link.label());
        assertEquals("cookiebot", link.preset());
        assertEquals(Map.of("data-track", "footer"), link.attributes());
        assertEquals("Cookiebot.renew()", link.renderedAttributes().get("onclick"));

        // An attributes-only link with no preset; the label setter and a null preset decode cleanly.
        mock.reply(200, "{\"object\":\"cookie_consent\",\"enabled\":true,\"settings_link\":{\"label\":\"Prefs\","
                + "\"preset\":null,\"attributes\":{\"href\":\"#prefs\"},\"rendered_attributes\":{\"href\":\"#prefs\"}}}");
        cc = atlas.instance().replaceCookieConsent(new CookieConsentBody()
                .settingsLink(new CookieConsentSettingsLinkInput().label("Prefs").attributes(Map.of("href", "#prefs"))));
        assertEquals(JSON.readTree("{\"label\":\"Prefs\",\"attributes\":{\"href\":\"#prefs\"}}"),
                sent().get("settings_link"));
        assertNull(cc.settingsLink().preset());

        // No link: the field is omitted on the wire and decodes as null.
        mock.reply(200, "{\"object\":\"cookie_consent\",\"enabled\":true,\"settings_link\":null}");
        cc = atlas.instance().replaceCookieConsent(new CookieConsentBody().scriptUrl("https://cmp.test/s.js"));
        assertFalse(sent().has("settings_link"));
        assertNull(cc.settingsLink());

        assertEquals(List.of("cookiemunch", "cookiebot", "onetrust", "custom"), List.of(
                CookieConsentSettingsLink.PRESET_COOKIEMUNCH, CookieConsentSettingsLink.PRESET_COOKIEBOT,
                CookieConsentSettingsLink.PRESET_ONETRUST, CookieConsentSettingsLink.PRESET_CUSTOM));
        assertNull(new CookieConsent("cookie_consent", false, null, null, null, null, null).settingsLink());
    }

    // ---- oauth providers ---------------------------------------------------

    @Test
    void oauthProviderListFiltersAndNewFields() {
        mock.reply(200, """
                {"object":"list","data":[{"object":"oauth_provider","provider":"siwe","display_name":"Ethereum",
                  "configured":true,"enabled":true,"allow_sign_in":true,"allow_sign_up":true,"native":false,
                  "mode":"redirect","icon_url":"https://i/e.svg","setting_values":{"domain":"acme.test","chain_ids":"1,10"}}]}
                """);
        ListPage<OAuthProvider> page = atlas.oauthProviders().list(List.of("siwe", "google"), true);
        assertEquals("/v1/oauth_providers", mock.path);
        assertEquals("provider=siwe&provider=google&configured=true", mock.rawQuery);
        OAuthProvider p = page.data().get(0);
        assertEquals("redirect", p.mode());
        assertEquals("https://i/e.svg", p.iconUrl());
        assertEquals("1,10", p.settingValues().get("chain_ids"));
        assertFalse(p.native_());
    }

    @Test
    void oauthProviderUpsertDisplayAndWalletSettings() throws IOException {
        mock.reply(200, "{\"object\":\"oauth_provider\",\"provider\":\"siwe\",\"configured\":true,\"enabled\":false,\"updated_fields\":[]}");
        atlas.oauthProviders().upsert("siwe", UpsertOAuthProviderBody.siwe("acme.test", "1,10", null)
                .displayName("Ethereum")
                .iconUrl("https://i/e.svg")
                .mode("redirect")
                .clearSetting("rpc_url"));
        assertEquals("PUT", mock.method);
        assertEquals("/v1/oauth_providers/siwe", mock.path);
        JsonNode j = sent();
        assertEquals("Ethereum", j.get("display_name").asText());
        assertEquals("https://i/e.svg", j.get("icon_url").asText());
        assertEquals("redirect", j.get("mode").asText());
        assertEquals("acme.test", j.at("/settings/domain").asText());
        assertEquals("1,10", j.at("/settings/chain_ids").asText());
        assertTrue(j.get("settings").has("rpc_url") && j.at("/settings/rpc_url").isNull(), "null clears a setting");
        assertFalse(j.has("client_id"));
    }

    // ---- webhooks ----------------------------------------------------------

    @Test
    void webhookEndpointManagement() throws IOException {
        mock.reply(200, "{\"object\":\"webhook_endpoint\",\"id\":\"we_1\",\"url\":\"https://h\",\"enabled_events\":[\"*\"],"
                + "\"active\":false,\"failing_since\":5,\"disabled_at\":null,\"created_at\":1}");
        var ep = atlas.webhooks().endpoints().update("we_1", new UpdateWebhookEndpointBody().active(false));
        assertEquals("PATCH", mock.method);
        assertEquals("/v1/webhook_endpoints/we_1", mock.path);
        assertFalse(sent().get("active").asBoolean());
        assertEquals(5L, ep.failingSince());

        mock.reply(200, "{\"object\":\"webhook_secret\",\"secret\":\"whsec_1\"}");
        WebhookSecret s = atlas.webhooks().endpoints().revealSecret("we_1");
        assertEquals("/v1/webhook_endpoints/we_1/reveal_secret", mock.path);
        assertEquals("whsec_1", s.secret());
        atlas.webhooks().endpoints().rotateSecret("we_1");
        assertEquals("/v1/webhook_endpoints/we_1/rotate_secret", mock.path);

        mock.reply(200, "{\"object\":\"webhook_delivery\",\"id\":\"wd_1\",\"event_id\":\"ev_1\",\"status\":\"pending\",\"note\":\"queued\"}");
        WebhookDeliveryQueued q = atlas.webhooks().endpoints().test("we_1");
        assertEquals("/v1/webhook_endpoints/we_1/test", mock.path);
        assertEquals("pending", q.status());
        atlas.webhooks().redeliver("we_1", "wd_9");
        assertEquals("/v1/webhook_endpoints/we_1/deliveries/wd_9/redeliver", mock.path);

        mock.reply(200, "{\"object\":\"list\",\"data\":[{\"id\":\"wd_1\",\"event_id\":\"ev_1\",\"event_type\":\"user.created\","
                + "\"attempt_number\":1,\"max_attempts\":8,\"payload\":{\"a\":1},\"status\":\"success\",\"created_at\":1}]}");
        var deliveries = atlas.webhooks().deliveries("we_1", 10);
        assertEquals("limit=10", mock.rawQuery);
        assertEquals("user.created", deliveries.data().get(0).eventType());
        assertEquals(1, deliveries.data().get(0).payload().get("a").asInt());

        mock.reply(200, "{\"object\":\"list\",\"data\":[],\"available_events\":[\"user.created\"],"
                + "\"retry_schedule_ms\":[1000,5000],\"max_attempts\":8}");
        WebhookEndpointList all = atlas.webhooks().endpoints().listWithEvents();
        assertEquals(List.of("user.created"), all.availableEvents());
        assertEquals(List.of(1000L, 5000L), all.retryScheduleMs());
        assertEquals(8, all.maxAttempts());
    }

    // ---- entitlements + org policy -----------------------------------------

    @Test
    void entitlementOverrides() throws IOException {
        String row = "{\"object\":\"entitlement_override\",\"id\":\"eo_1\",\"subject_type\":\"org\",\"subject_id\":\"org_1\","
                + "\"plan\":\"enterprise\",\"features\":[\"sso\"],\"limits\":{\"seats\":500},\"expires_at\":null,"
                + "\"created_by\":null,\"created_at\":1}";
        mock.reply(200, row);
        EntitlementOverride eo = atlas.entitlementOverrides().create(new CreateEntitlementOverrideBody()
                .subjectType("org").subjectId("org_1").plan("enterprise")
                .features(List.of("sso")).limits(Map.of("seats", 500)));
        assertEquals("/v1/entitlement_overrides", mock.path);
        JsonNode j = sent();
        assertEquals("org", j.get("subject_type").asText());
        assertEquals(500, j.at("/limits/seats").asInt());
        assertEquals(List.of("sso"), eo.features());

        mock.reply(200, "{\"object\":\"list\",\"data\":[" + row + "]}");
        atlas.entitlementOverrides().list("org", "org_1");
        assertEquals("subject_type=org&subject_id=org_1", mock.rawQuery);
        mock.reply(200, "{\"object\":\"entitlement_override\",\"id\":\"eo_1\",\"deleted\":true}");
        assertTrue(atlas.entitlementOverrides().delete("eo_1").deleted());

        mock.reply(200, "{\"object\":\"organization_entitlements\",\"organization_id\":\"org_1\",\"plan\":\"pro\","
                + "\"features\":[\"sso\",\"audit\"],\"limits\":{\"seats\":10}}");
        OrganizationEntitlements ent = atlas.organizations().entitlements("org_1");
        assertEquals(List.of("sso", "audit"), ent.features());
        assertEquals(10, ((Number) ent.limits().get("seats")).intValue());
    }

    @Test
    void orgPolicyPatchTypedFieldsAndClear() throws IOException {
        mock.reply(200, "{\"object\":\"organization_policy\",\"organization_id\":\"org_1\",\"policy\":{"
                + "\"requireMfa\":true,\"allowedSignInMethods\":[\"passkey\"],\"ipAllowlist\":[\"10.0.0.0/8\"],"
                + "\"maxSessionAgeSeconds\":3600,\"endUserPool\":{\"enabled\":true,\"requireApproval\":false},\"future\":1}}");
        OrganizationPolicy res = atlas.organizations().updatePolicy("org_1", new OrganizationPolicyPatch()
                .requireMfa(true)
                .allowedSignInMethods(List.of("passkey"))
                .maxSessionAgeSeconds(3600L)
                .endUserPool(new EndUserPoolPolicy(true).requireApproval(false))
                .clear("ipAllowlist"));
        JsonNode j = sent();
        assertTrue(j.get("requireMfa").asBoolean());
        assertEquals("passkey", j.at("/allowedSignInMethods/0").asText());
        assertEquals(3600, j.get("maxSessionAgeSeconds").asInt());
        assertTrue(j.at("/endUserPool/enabled").asBoolean());
        assertTrue(j.has("ipAllowlist") && j.get("ipAllowlist").isNull());
        assertFalse(j.has("extra"), "the escape-hatch map is flattened, not nested");

        OrgPolicy typed = res.typed();
        assertTrue(typed.requireMfa);
        assertEquals(List.of("10.0.0.0/8"), typed.ipAllowlist);
        assertEquals(3600L, typed.maxSessionAgeSeconds);
        assertFalse(typed.endUserPool.requireApproval);
        assertEquals(1, typed.extra().get("future"));
    }

    // ---- a few Phase B shapes ----------------------------------------------

    @Test
    void machinesListUsesTheParamsObject() {
        mock.reply(200, "{\"object\":\"list\",\"data\":[],\"has_more\":true,\"next_cursor\":\"m_9\"}");
        CursorPage<Machine> page = atlas.machines().list(new ListMachinesParams().status("active").limit(5L));
        assertEquals("status=active&limit=5", mock.rawQuery);
        assertTrue(page.hasMore());
        assertEquals("m_9", page.nextCursor());
    }

    @Test
    void auditEventExportReturnsRawText() {
        mock.reply(200, "{\"id\":\"a1\"}\n{\"id\":\"a2\"}\n");
        String ndjson = atlas.auditEvents().export(new net.atlasauth.atlas.models.ExportAuditEventsParams().format("jsonl"));
        assertEquals("/v1/audit_events/export", mock.path);
        assertEquals("format=jsonl", mock.rawQuery);
        assertEquals(2, ndjson.trim().split("\n").length);
    }
}
