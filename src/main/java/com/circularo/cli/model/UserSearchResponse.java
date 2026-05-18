package com.circularo.cli.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * API response wrapper for user search results.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record UserSearchResponse(
    @JsonProperty("results") List<User> results,
    @JsonProperty("total") Integer total
) {}
