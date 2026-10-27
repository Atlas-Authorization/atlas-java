package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.MarketingDeleteLeadResponse;
import net.atlasauth.atlas.models.MarketingGetSettingsResponse;
import net.atlasauth.atlas.models.MarketingListLeadsResponse;
import net.atlasauth.atlas.models.MarketingUpdateSettingsBody;

import java.util.List;
import java.util.Map;

import static net.atlasauth.atlas.http.Transport.enc;

/** The {@code /v1/marketing_settings} resource. */
public final class Marketing {

    private final Transport t;

    public Marketing(Transport t) {
        this.t = t;
    }

    /** {@code GET /v1/marketing_settings} - The marketing settings: which UTM / ad-click-id parameters the SDK captures onto new users, and the GA4, GTM and Hotjar ids hosted pages load after consent. Scope: {@code marketing:read}. */
    public MarketingGetSettingsResponse getSettings() {
        return t.request("GET", "/v1/marketing_settings", null, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code PUT /v1/marketing_settings} - Change the marketing settings. `capture.params` empty means the standard set (utm_source, utm_medium, utm_campaign, utm_term, utm_content, utm_id and the common ad click ids). Hosted pages load a configured script only when the visitor's consent manager grants `scripts.consent_category` (default analytics); with no consent signal nothing loads. Scope: {@code marketing:write}. */
    public MarketingGetSettingsResponse updateSettings(MarketingUpdateSettingsBody body) {
        return t.request("PUT", "/v1/marketing_settings", null, body, null, false, new TypeReference<>() {
        });
    }

    /** {@code GET /v1/leads} - The lead / subscribe form's submissions, newest first, with the first-touch attribution captured with each. `format=csv` exports up to 50,000. Scope: {@code marketing:read}. */
    public MarketingListLeadsResponse listLeads() {
        return listLeads(null, null, null, null);
    }

    /** As above, with every optional parameter. */
    public MarketingListLeadsResponse listLeads(Long limit, String startingAfter, String form, String format) {
        Map<String, Object> q = Params.of().set("limit", limit).set("starting_after", startingAfter).set("form", form).set("format", format).map();
        return t.request("GET", "/v1/leads", q, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code DELETE /v1/leads/{id}} - Delete one captured lead. Scope: {@code marketing:write}. */
    public MarketingDeleteLeadResponse deleteLead(String id) {
        return t.request("DELETE", "/v1/leads/" + enc(id), null, null, null, false, new TypeReference<>() {
        });
    }
}
