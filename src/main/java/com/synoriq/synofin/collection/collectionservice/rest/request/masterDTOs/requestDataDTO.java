package com.synoriq.synofin.collection.collectionservice.rest.request.masterDTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.synoriq.synofin.collection.collectionservice.rest.request.searchDTOs.PaginationDTO;
import lombok.Data;

        import com.fasterxml.jackson.annotation.JsonProperty;
        import lombok.Data;

import java.util.List;

@Data
public class requestDataDTO {
    @JsonProperty("master_type")
    public List<Object> masterType;

}
