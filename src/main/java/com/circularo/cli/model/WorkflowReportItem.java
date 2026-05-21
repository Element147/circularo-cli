package com.circularo.cli.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Mapped from docs/api-1.yaml - responseSearchReportsSingleItem
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record WorkflowReportItem(
    @JsonProperty("user") String user,
    @JsonProperty("shareType") String shareType,
    @JsonProperty("date") String date,
    @JsonProperty("seen") Boolean seen,
    @JsonProperty("duration") Integer duration,
    @JsonProperty("expiration") String expiration,
    @JsonProperty("reminder") String reminder,
    @JsonProperty("dateOfReceivingTheAction") String dateOfReceivingTheAction,
    @JsonProperty("delegatedUser") Boolean delegatedUser,
    @JsonProperty("lastAction") Boolean lastAction,
    @JsonProperty("order") Integer order,
    @JsonProperty("shareId") String shareId,
    @JsonProperty("verification") String verification
) {}
