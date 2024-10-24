package com.synoriq.synofin.collection.collectionservice.rest.response.activitylogdtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ActivityLogBaseResponseDTO {


    @JsonProperty("response")
    public Boolean response;

    @JsonProperty("data")
    public ActivityLogDataDTO data;

    @JsonProperty("error")
    public String error;


}
