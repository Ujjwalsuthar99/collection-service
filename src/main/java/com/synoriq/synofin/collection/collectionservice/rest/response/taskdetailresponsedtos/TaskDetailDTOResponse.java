package com.synoriq.synofin.collection.collectionservice.rest.response.taskdetailresponsedtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TaskDetailDTOResponse {

    @JsonProperty("response")
    public Boolean response;

    @JsonProperty("data")
    public LoanDataDTO data;

    @JsonProperty("error")
    public String error;
}
