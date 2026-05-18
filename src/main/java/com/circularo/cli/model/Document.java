package com.circularo.cli.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Mapped from docs/api-1.yaml - internalDocuments
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record Document(
    @JsonProperty("documentId") String documentId,
    @JsonProperty("documentTitle") String documentTitle,
    @JsonProperty("type") String type,
    @JsonProperty("date") String date,
    @JsonProperty("workflow") Workflow workflow
) {}
