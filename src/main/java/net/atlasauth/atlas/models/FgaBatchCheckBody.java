package net.atlasauth.atlas.models;

import java.util.List;

/**
 * Body for {@code POST /v1/fga/stores/:id/batch-check}. {@code checks} is
 * required; {@code authorizationModelId} defaults to the store's latest model.
 * Fluent setters return {@code this}.
 */
public final class FgaBatchCheckBody {
    public List<FgaBatchCheckItem> checks;
    public String authorizationModelId;

    public FgaBatchCheckBody() {
    }

    public FgaBatchCheckBody(List<FgaBatchCheckItem> checks) {
        this.checks = checks;
    }

    public FgaBatchCheckBody checks(List<FgaBatchCheckItem> v) { this.checks = v; return this; }
    public FgaBatchCheckBody authorizationModelId(String v) { this.authorizationModelId = v; return this; }
}
