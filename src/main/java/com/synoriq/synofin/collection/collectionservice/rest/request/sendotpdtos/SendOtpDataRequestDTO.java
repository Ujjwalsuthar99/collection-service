package com.synoriq.synofin.collection.collectionservice.rest.request.sendotpdtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class SendOtpDataRequestDTO {

    @JsonProperty("phone_number")
    public String phoneNumber;

    @JsonProperty("template_name")
    public String templateName;

    @JsonProperty("specific_otp")
    public String specificOtp;

    @JsonProperty("otp_code_length")
    public Integer otpCodeLength;

    @JsonProperty("otp_expiry")
    public String otpExpiry;

    @JsonProperty("template_variable")
    public List<String> templateVariable;

}
