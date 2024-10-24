package com.synoriq.synofin.collection.collectionservice.rest.response.utilsdtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;


@Data
public class BankNameIFSCResponseDTO {
    @JsonProperty("response")
    public Boolean response;

    @JsonProperty("data")
    public Object data;

    @JsonProperty("error")
    public String error;

}

