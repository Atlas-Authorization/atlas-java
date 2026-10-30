package net.atlasauth.atlas.models;

import java.util.Map;

/** Body for {@code POST /v1/app_events}: one event to one organization's subscribed endpoints. */
public final class PublishAppEventBody extends ExtensibleBody {
    /** The organization (your customer) whose endpoints receive the event. */
    public String organizationId;
    /** Shorthand for {@code organizationId}. */
    public String org;
    /** A type from your catalog. */
    public String type;
    /** The event body your customers receive under {@code data} (at most 256 KB). */
    public Map<String, Object> data;
    /** A repeat with the same key returns the first event and queues nothing (kept 90 days). */
    public String idempotencyKey;

    public PublishAppEventBody() {
    }

    public PublishAppEventBody(String organizationId, String type, Map<String, Object> data) {
        this.organizationId = organizationId;
        this.type = type;
        this.data = data;
    }

    public PublishAppEventBody organizationId(String v) { this.organizationId = v; return this; }
    public PublishAppEventBody org(String v) { this.org = v; return this; }
    public PublishAppEventBody type(String v) { this.type = v; return this; }
    public PublishAppEventBody data(Map<String, Object> v) { this.data = v; return this; }
    public PublishAppEventBody idempotencyKey(String v) { this.idempotencyKey = v; return this; }
    public PublishAppEventBody set(String key, Object value) { putField(key, value); return this; }
}
