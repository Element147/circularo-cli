package com.circularo.cli.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Nested wrapper in document search results wrapping each individual document.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record DocumentSearchResultWrapper(
    @JsonProperty("document") Document document
) {}
