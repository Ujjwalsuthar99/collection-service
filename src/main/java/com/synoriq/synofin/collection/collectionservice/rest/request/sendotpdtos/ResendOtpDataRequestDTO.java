package com.synoriq.synofin.collection.collectionservice.rest.request.sendotpdtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ResendOtpDataRequestDTO {

    @JsonProperty("phone_number")
    public String phoneNumber;

    @JsonProperty("retry_type")
    public String retryType;


}
