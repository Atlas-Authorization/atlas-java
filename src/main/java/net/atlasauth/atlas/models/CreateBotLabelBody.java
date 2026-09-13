package net.atlasauth.atlas.models;

/**
 * Body for {@code POST /v1/bot_labels}. {@code subjectType} is one of
 * {@code user}/{@code device}/{@code ip_hash}/{@code attempt}; {@code label} is
 * one of {@code bot}/{@code human}/{@code suspect}.
 */
public final class CreateBotLabelBody {
    public String subjectType;
    public String subjectId;
    public String label;
    /** Clamped to 0..1; defaults to 1. */
    public Double confidence;
    public String note;

    public CreateBotLabelBody() {
    }

    public CreateBotLabelBody(String subjectType, String subjectId, String label) {
        this.subjectType = subjectType;
        this.subjectId = subjectId;
        this.label = label;
    }

    public CreateBotLabelBody subjectType(String v) { this.subjectType = v; return this; }
    public CreateBotLabelBody subjectId(String v) { this.subjectId = v; return this; }
    public CreateBotLabelBody label(String v) { this.label = v; return this; }
    public CreateBotLabelBody confidence(Double v) { this.confidence = v; return this; }
    public CreateBotLabelBody note(String v) { this.note = v; return this; }
}
