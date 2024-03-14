package com.synoriq.synofin.collection.collectionservice.rest.request.followUpDTOs;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.synoriq.synofin.collection.collectionservice.rest.request.CollectionActivityLogDTO;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Builder
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class FollowUpStatusRequestDTO {

    @JsonProperty("followup_id")
    @NotNull(message = "followupId is mandatory")
    private Long followUpId;

    @JsonProperty("loan_id")
    @NotNull(message = "loanId is mandatory")
    private Long loanId;

    @JsonProperty("status")
    private String status;

    @JsonProperty("remarks")
    private String remarks;

    @JsonProperty("service_request_id")
    private Long serviceRequestId;

    @JsonProperty("activity_log")
    private CollectionActivityLogDTO activityLog;

}
