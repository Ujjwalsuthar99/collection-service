package com.synoriq.synofin.collection.collectionservice.rest.request.masterdtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class RequestDataDTO {
    @JsonProperty("master_type")
    public List<Object> masterType;

}
