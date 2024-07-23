package com.synoriq.synofin.collection.collectionservice.rest.request.searchDTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PaginationDTO {

    @JsonProperty("page")
    public Integer page;

    @JsonProperty("count")
    public Integer count;
}
