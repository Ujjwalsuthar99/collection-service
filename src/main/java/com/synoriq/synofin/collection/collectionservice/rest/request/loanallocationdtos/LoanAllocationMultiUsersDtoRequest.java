package com.synoriq.synofin.collection.collectionservice.rest.request.loanallocationdtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class LoanAllocationMultiUsersDtoRequest {

    @JsonProperty("createdBy")
    private Long createdBy;

    @JsonProperty("loanId")
    private Long loanId;

    @JsonProperty("allocatedToUserId")
    private List<Long> allocatedToUserId;

    @JsonProperty("removedUserId")
    private List<Long> removedUserId;

    @JsonProperty("taskPurpose")
    private String taskPurpose;

}
