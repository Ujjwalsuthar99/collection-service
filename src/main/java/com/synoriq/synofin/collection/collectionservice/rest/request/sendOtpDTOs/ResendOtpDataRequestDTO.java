package com.synoriq.synofin.collection.collectionservice.rest.request.sendOtpDTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class ResendOtpDataRequestDTO {

    @JsonProperty("phone_number")
    public String phoneNumber;

    @JsonProperty("retry_type")
    public String retryType;


}
