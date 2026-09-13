package net.atlasauth.atlas.models;

import java.util.List;

/**
 * Body for {@code POST /v1/fga/stores/:id/check}. {@code user}, {@code relation}
 * and {@code object} are required; {@code authorizationModelId} defaults to the
 * store's latest model. Fluent setters return {@code this}.
 */
public final class FgaCheckBody {
    public String user;
    public String relation;
    public String object;
    public String authorizationModelId;
    public List<FgaTupleKey> contextualTuples;

    public FgaCheckBody() {
    }

    public FgaCheckBody(String user, String relation, String object) {
        this.user = user;
        this.relation = relation;
        this.object = object;
    }

    public FgaCheckBody user(String v) { this.user = v; return this; }
    public FgaCheckBody relation(String v) { this.relation = v; return this; }
    public FgaCheckBody object(String v) { this.object = v; return this; }
    public FgaCheckBody authorizationModelId(String v) { this.authorizationModelId = v; return this; }
    public FgaCheckBody contextualTuples(List<FgaTupleKey> v) { this.contextualTuples = v; return this; }
}
