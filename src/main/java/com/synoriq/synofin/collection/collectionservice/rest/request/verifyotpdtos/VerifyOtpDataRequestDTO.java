package com.synoriq.synofin.collection.collectionservice.rest.request.verifyotpdtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class VerifyOtpDataRequestDTO {

    @JsonProperty("id")
    public String id;

    @JsonProperty("otp")
    public String otp;
}
