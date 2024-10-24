package com.synoriq.synofin.collection.collectionservice.rest.request.searchdtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RequestDataDTO {
    @JsonProperty("searchTerm")
    public String searchTerm;

    @JsonProperty("pagination")
    public PaginationDTO paginationDTO;

    @JsonProperty("filterBy")
    public String filterBy;
}
