package net.atlasauth.atlas.models;

import java.util.List;

/**
 * One item in a batch check. {@code correlationId} is echoed back to match the
 * result to its request. Fluent setters return {@code this}.
 */
public final class FgaBatchCheckItem {
    public String user;
    public String relation;
    public String object;
    public String correlationId;
    public List<FgaTupleKey> contextualTuples;

    public FgaBatchCheckItem() {
    }

    public FgaBatchCheckItem(String user, String relation, String object) {
        this.user = user;
        this.relation = relation;
        this.object = object;
    }

    public FgaBatchCheckItem user(String v) { this.user = v; return this; }
    public FgaBatchCheckItem relation(String v) { this.relation = v; return this; }
    public FgaBatchCheckItem object(String v) { this.object = v; return this; }
    public FgaBatchCheckItem correlationId(String v) { this.correlationId = v; return this; }
    public FgaBatchCheckItem contextualTuples(List<FgaTupleKey> v) { this.contextualTuples = v; return this; }
}
