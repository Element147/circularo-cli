package com.circularo.cli.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Mapped from docs/api-1.yaml - responseUsersNormal / responseUsersBasic
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record User(
    @JsonProperty("id") String id,
    @JsonProperty("fullname") String fullname,
    @JsonProperty("mail") String mail,
    @JsonProperty("name") String name,
    @JsonProperty("phone") String phone,
    @JsonProperty("recordType") String recordType,
    @JsonProperty("role") String role,
    @JsonProperty("status") String status
) {}
