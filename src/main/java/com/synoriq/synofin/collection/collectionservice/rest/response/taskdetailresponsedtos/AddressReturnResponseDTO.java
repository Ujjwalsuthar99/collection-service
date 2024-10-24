package com.synoriq.synofin.collection.collectionservice.rest.response.taskdetailresponsedtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AddressReturnResponseDTO {

    @JsonProperty("home_address")
    public String homeAddress;

    @JsonProperty("work_address")
    public String workAddress;

    @JsonProperty("residential_address")
    public String residentialAddress;

}
