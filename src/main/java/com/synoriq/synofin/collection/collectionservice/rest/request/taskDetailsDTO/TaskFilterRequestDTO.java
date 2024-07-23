package com.synoriq.synofin.collection.collectionservice.rest.request.taskDetailsDTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class TaskFilterRequestDTO {

    @JsonProperty("dpd")
    private List<String> dpd;

    @JsonProperty("order")
    private String order;

    @JsonProperty("searchKey")
    private String searchKey;

}
