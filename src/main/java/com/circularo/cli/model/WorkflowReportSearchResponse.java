package com.circularo.cli.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * Mapped from docs/api-1.yaml - responseSearchReportsMultiple
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record WorkflowReportSearchResponse(
    @JsonProperty("results") List<WorkflowReport> results,
    @JsonProperty("total") int total
) {}
