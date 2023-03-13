package com.synoriq.synofin.collection.collectionservice.rest.response.globalSearchDTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class SearchDTOResponse {
    @JsonProperty("response")
    public String response;

    @JsonProperty("data")
    public LMSSearchDTOResponse data;

    @JsonProperty("error")
    public String error;
}
