package com.synoriq.synofin.collection.collectionservice.rest.response.TaskDetailResponseDTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AddressReturnResponseDTO {

    @JsonProperty("home_address")
    public String HomeAddress;

    @JsonProperty("work_address")
    public String WorkAddress;

}
