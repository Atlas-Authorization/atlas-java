package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OpenFgaReadResult(
        List<OpenFgaTuple> tuples,
        String continuationToken) {
}
