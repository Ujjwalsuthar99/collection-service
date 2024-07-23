package com.synoriq.synofin.collection.collectionservice.rest.request.verifyOtpDTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class VerifyOtpDataRequestDTO {

    @JsonProperty("id")
    public String id;

    @JsonProperty("otp")
    public String otp;
}
