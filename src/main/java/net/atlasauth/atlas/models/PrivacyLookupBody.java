package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code POST /v1/privacy/lookup}: find a person by {@code atlas} ({@code iss}/{@code sub}/{@code ext}/{@code principal}; {@code sub} wins, then {@code ext}) or by verified {@code emails}. */
public final class PrivacyLookupBody extends ExtensibleBody {
    public List<String> emails;
    public Map<String, Object> atlas;

    public PrivacyLookupBody() {
    }

    public PrivacyLookupBody emails(List<String> v) { this.emails = v; return this; }
    public PrivacyLookupBody atlas(Map<String, Object> v) { this.atlas = v; return this; }

    /** Set any field by its wire name, including one this SDK does not model yet. */
    public PrivacyLookupBody set(String key, Object value) { putField(key, value); return this; }

}
