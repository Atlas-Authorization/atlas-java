package net.atlasauth.atlas.models;

/** Body for {@code POST /v1/api_keys/{id}/usage}. */
public final class RecordApiKeyUsageBody {
    /** A positive integer, in {@code unit}. */
    public Long amount;
    /** App-defined unit, e.g. {@code "requests"} or {@code "usd_cents"}. */
    public String unit;
    /**
     * Optional. The same key (kept 24 hours) returns the first outcome without counting
     * again; reusing it with a different unit or amount is {@code 409 IDEMPOTENCY_CONFLICT}.
     */
    public String idempotencyKey;

    public RecordApiKeyUsageBody() {
    }

    public RecordApiKeyUsageBody(long amount, String unit) {
        this.amount = amount;
        this.unit = unit;
    }

    public RecordApiKeyUsageBody amount(Long v) { this.amount = v; return this; }
    public RecordApiKeyUsageBody unit(String v) { this.unit = v; return this; }
    public RecordApiKeyUsageBody idempotencyKey(String v) { this.idempotencyKey = v; return this; }
}
