package com.synoriq.synofin.collection.collectionservice.rest.response.TaskDetailResponseDTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CommunicationReturnResponseDTO {


    @JsonProperty("numbers")
    public String mobNo;

    @JsonProperty("home_address")
    public String HomeAddress;

    @JsonProperty("work_address")
    public String WorkAddress;

    @JsonProperty("alternative_mobile")
    public String alternativeMobile;

    @JsonProperty("mobile_spouse")
    public String MobileSpouse;

}
