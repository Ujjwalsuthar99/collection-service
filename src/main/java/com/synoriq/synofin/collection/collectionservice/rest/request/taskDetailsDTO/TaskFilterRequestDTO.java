package com.synoriq.synofin.collection.collectionservice.rest.request.taskDetailsDTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TaskFilterRequestDTO {
    @JsonProperty("dpd")
    private String dpd;

    @JsonProperty("order")
    private String order;

}
