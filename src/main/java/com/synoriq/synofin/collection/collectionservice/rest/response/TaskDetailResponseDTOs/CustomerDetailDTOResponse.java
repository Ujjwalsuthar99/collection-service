package com.synoriq.synofin.collection.collectionservice.rest.response.TaskDetailResponseDTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class CustomerDetailDTOResponse {

    @JsonProperty("response")
    public Boolean response;

    @JsonProperty("data")
    public List<CustomerDataResponseDTO> data;

    @JsonProperty("error")
    public String error;
}
