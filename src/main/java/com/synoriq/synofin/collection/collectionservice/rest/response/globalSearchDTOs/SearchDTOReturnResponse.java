package com.synoriq.synofin.collection.collectionservice.rest.response.globalSearchDTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@Getter
@Setter
public class SearchDTOReturnResponse {

    @JsonProperty("response")
    public Boolean response;

    @JsonProperty("data")
    public List<TaskListDTOReturnResponse> data;

    @JsonProperty("error")
    public String error;
}
