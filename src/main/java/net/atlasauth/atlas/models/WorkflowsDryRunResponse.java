package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code POST /v1/workflows/{id}/dry_run}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record WorkflowsDryRunResponse(
        String object,
        String id,
        String workflowId,
        String subject,
        String userId,
        Boolean dryRun,
        String status,
        List<WorkflowsDryRunResponseStepsItem> steps,
        String error,
        Long startedAt,
        Long finishedAt) {
}
