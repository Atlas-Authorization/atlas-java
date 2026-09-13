package net.atlasauth.atlas.models;

/**
 * A tuple key used in write/check bodies. {@code object} is the target as
 * {@code type:id}. Fluent setters return {@code this}.
 */
public final class FgaTupleKey {
    public String user;
    public String relation;
    public String object;

    public FgaTupleKey() {
    }

    public FgaTupleKey(String user, String relation, String object) {
        this.user = user;
        this.relation = relation;
        this.object = object;
    }

    public FgaTupleKey user(String v) { this.user = v; return this; }
    public FgaTupleKey relation(String v) { this.relation = v; return this; }
    public FgaTupleKey object(String v) { this.object = v; return this; }
}
