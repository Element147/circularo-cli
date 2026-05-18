package com.circularo.cli.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * API response wrapper for document search results.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record DocumentSearchResponse(
    @JsonProperty("results") List<DocumentSearchResultWrapper> results,
    @JsonProperty("total") Integer total
) {}
