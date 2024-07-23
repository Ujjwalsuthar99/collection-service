package com.synoriq.synofin.collection.collectionservice.rest.request.taskDetailsDTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.synoriq.synofin.collection.collectionservice.rest.request.searchDTOs.PaginationDTO;
import lombok.Data;

@Data
public class TaskDetailRequestDataDTO {

    @JsonProperty("loan_id")
    public String loanId;

    @JsonProperty("request_id")
    public String requestId;
}
