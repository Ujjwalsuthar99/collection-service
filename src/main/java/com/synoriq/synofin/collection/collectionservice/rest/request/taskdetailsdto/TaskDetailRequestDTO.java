package com.synoriq.synofin.collection.collectionservice.rest.request.taskdetailsdto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TaskDetailRequestDTO {

    @JsonProperty("data")
    private TaskDetailRequestDataDTO requestData;


    @JsonProperty("user_reference_number")
    private String userReferenceNumber;
}
