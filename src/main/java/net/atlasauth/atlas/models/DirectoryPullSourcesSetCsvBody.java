package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code PUT /v1/directory_pull_sources/{id}/csv}. */
public final class DirectoryPullSourcesSetCsvBody {
    public String csv;

    public DirectoryPullSourcesSetCsvBody() {
    }

    public DirectoryPullSourcesSetCsvBody csv(String v) { this.csv = v; return this; }
}
