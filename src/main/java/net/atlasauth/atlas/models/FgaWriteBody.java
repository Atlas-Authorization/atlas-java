package net.atlasauth.atlas.models;

import java.util.List;

/**
 * Body for {@code POST /v1/fga/stores/:id/write} — write and/or delete tuples in
 * one atomic call. Fluent setters return {@code this}.
 */
public final class FgaWriteBody {
    public List<FgaTupleKey> writes;
    public List<FgaTupleKey> deletes;

    public FgaWriteBody() {
    }

    public FgaWriteBody writes(List<FgaTupleKey> v) { this.writes = v; return this; }
    public FgaWriteBody deletes(List<FgaTupleKey> v) { this.deletes = v; return this; }
}
