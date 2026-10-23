package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/** An outbound request an action queued with {@code api.http} (a dry run sends nothing). */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ActionTestHttpRequest(
        String method,
        /** Query values are masked. */ String url,
        /** Header names only; values are never echoed. */ List<String> headerNames,
        boolean hasBody,
        String saveResponseAs) {
}
