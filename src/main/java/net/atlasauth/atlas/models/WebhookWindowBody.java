package net.atlasauth.atlas.models;

/**
 * Body for a bulk webhook redelivery ({@code POST /v1/webhook_endpoints/{id}/redeliver}) or replay
 * ({@code .../replay}). {@code since} and {@code until} are epoch ms (at most 90 days ago).
 */
public final class WebhookWindowBody extends ExtensibleBody {
    public Long since;
    public Long until;
    /** Redeliver only: {@code failed} (default) or {@code success}. */
    public String status;
    /** Replay only: comma-separated event names to limit the replay to. */
    public String eventType;

    public WebhookWindowBody() {
    }

    public WebhookWindowBody since(Long v) { this.since = v; return this; }
    public WebhookWindowBody until(Long v) { this.until = v; return this; }
    public WebhookWindowBody status(String v) { this.status = v; return this; }
    public WebhookWindowBody eventType(String v) { this.eventType = v; return this; }
    public WebhookWindowBody set(String key, Object value) { putField(key, value); return this; }
}
