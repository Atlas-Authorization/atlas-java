package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class CookieConsentBody {
    /** Your CMP script URL. Must be https; its origin is added to the page CSP. */
    public String scriptUrl;
    /** The &lt;script id&gt; some CMPs need. */
    public String scriptId;
    /** Your CMP site id (sent as data-cbid). */
    public String siteId;
    /** The CMP prior-blocking mode, e.g. "auto" (sent as data-blockingmode). */
    public String blockingMode;
    /** Other https URLs the CMP loads from or calls; each origin is added to the page CSP. */
    public List<String> extraOrigins;

    @JsonIgnore
    private final Map<String, Object> additionalFields = new LinkedHashMap<>();

    public CookieConsentBody() {
    }

    public CookieConsentBody scriptUrl(String v) { this.scriptUrl = v; return this; }
    public CookieConsentBody scriptId(String v) { this.scriptId = v; return this; }
    public CookieConsentBody siteId(String v) { this.siteId = v; return this; }
    public CookieConsentBody blockingMode(String v) { this.blockingMode = v; return this; }
    public CookieConsentBody extraOrigins(List<String> v) { this.extraOrigins = v; return this; }

    /**
     * Set any field by its wire name, including an explicit {@code null} (which
     * clears a stored value on a partial update) or a field this SDK does not model yet.
     */
    public CookieConsentBody set(String key, Object value) { this.additionalFields.put(key, value); return this; }

    @JsonAnyGetter
    public Map<String, Object> additionalFields() { return additionalFields; }
}
