package com.synoriq.synofin.collection.collectionservice.rest.response.taskdetailresponsedtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class NumbersReturnResponseDTO {


    @JsonProperty("numbers")
    public String mobNo;

    @JsonProperty("alternative_mobile")
    public String alternativeMobile;


}
