package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code GET /v1/audit_events/{id}/redactions}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record AuditEventSchemasRedactionsResponse(
        String object,
        List<AuditEventSchemasRedactionsResponseDataItem> data,
        Boolean hasMore,
        String nextCursor) {
}
