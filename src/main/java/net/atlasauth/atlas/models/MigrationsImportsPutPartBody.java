package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code PUT /v1/migrations/{source}/imports/{id}/parts/{part}}. Required: data. */
public final class MigrationsImportsPutPartBody {
    /** Lines of the sealed bundle file. Required. */
    public String data;

    public MigrationsImportsPutPartBody() {
    }

    public MigrationsImportsPutPartBody data(String v) { this.data = v; return this; }
}
