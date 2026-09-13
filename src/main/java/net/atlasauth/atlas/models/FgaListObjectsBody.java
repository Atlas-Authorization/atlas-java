package net.atlasauth.atlas.models;

import java.util.List;

/**
 * Body for {@code POST /v1/fga/stores/:id/list-objects}. {@code user},
 * {@code relation} and {@code type} are required. Fluent setters return
 * {@code this}.
 */
public final class FgaListObjectsBody {
    public String user;
    public String relation;
    public String type;
    public String authorizationModelId;
    public List<FgaTupleKey> contextualTuples;

    public FgaListObjectsBody() {
    }

    public FgaListObjectsBody(String user, String relation, String type) {
        this.user = user;
        this.relation = relation;
        this.type = type;
    }

    public FgaListObjectsBody user(String v) { this.user = v; return this; }
    public FgaListObjectsBody relation(String v) { this.relation = v; return this; }
    public FgaListObjectsBody type(String v) { this.type = v; return this; }
    public FgaListObjectsBody authorizationModelId(String v) { this.authorizationModelId = v; return this; }
    public FgaListObjectsBody contextualTuples(List<FgaTupleKey> v) { this.contextualTuples = v; return this; }
}
