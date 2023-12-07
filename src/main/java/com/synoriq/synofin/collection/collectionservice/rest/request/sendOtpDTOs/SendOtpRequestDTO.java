package com.synoriq.synofin.collection.collectionservice.rest.request.sendOtpDTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SendOtpRequestDTO {

    @JsonProperty("data")
    public SendOtpDataRequestDTO data;

    @JsonProperty("specific_partner_name")
    public String specificPartnerName;

    @JsonProperty("user_reference_number")
    public String userReferenceNumber;

    @JsonProperty("system_id")
    public String systemId;

}
