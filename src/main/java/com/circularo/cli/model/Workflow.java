package com.circularo.cli.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Mapped from docs/api-1.yaml - internalDocumentsWorkflow
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record Workflow(
    @JsonProperty("shareStateName") String shareStateName
) {}
