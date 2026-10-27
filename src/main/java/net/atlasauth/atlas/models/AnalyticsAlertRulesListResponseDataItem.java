package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** A response object returned by the Backend API. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record AnalyticsAlertRulesListResponseDataItem(
        String object,
        String id,
        String name,
        String metric,
        String comparator,
        Double threshold,
        Long windowMinutes,
        Long minSample,
        Long cooldownMinutes,
        List<String> emailContacts,
        String webhookUrl,
        Boolean hasWebhookSecret,
        Boolean enabled,
        String subjectType,
        String subjectId,
        Boolean ssf,
        String state,
        Double lastValue,
        Long lastEvaluatedAt,
        Long lastTriggeredAt,
        String lastError,
        Long createdAt) {
}
