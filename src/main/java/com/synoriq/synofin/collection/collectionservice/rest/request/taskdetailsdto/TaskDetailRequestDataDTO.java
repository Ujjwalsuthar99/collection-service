package com.synoriq.synofin.collection.collectionservice.rest.request.taskdetailsdto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TaskDetailRequestDataDTO {

    @JsonProperty("loan_id")
    public String loanId;

    @JsonProperty("request_id")
    public String requestId;
}
