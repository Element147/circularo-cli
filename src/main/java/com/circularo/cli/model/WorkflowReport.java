package com.circularo.cli.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Mapped from docs/api-1.yaml - responseSearchReportsSingle
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record WorkflowReport(
    @JsonProperty("documentId") String documentId,
    @JsonProperty("documentTitle") String documentTitle,
    @JsonProperty("shareStateName") String shareStateName,
    @JsonProperty("transactionStartDate") String transactionStartDate,
    @JsonProperty("transactionEndDate") String transactionEndDate,
    @JsonProperty("transactionDuration") Integer transactionDuration,
    @JsonProperty("transactionEndReason") String transactionEndReason,
    @JsonProperty("totalRecipients") Integer totalRecipients,
    @JsonProperty("totalProcessed") Integer totalProcessed,
    @JsonProperty("totalWaitingFor") Integer totalWaitingFor,
    @JsonProperty("owner") String owner,
    @JsonProperty("sender") String sender
) {}
