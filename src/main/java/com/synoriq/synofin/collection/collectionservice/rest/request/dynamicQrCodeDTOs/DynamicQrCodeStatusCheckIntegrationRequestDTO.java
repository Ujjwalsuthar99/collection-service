package com.synoriq.synofin.collection.collectionservice.rest.request.dynamicQrCodeDTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class DynamicQrCodeStatusCheckIntegrationRequestDTO {

    @JsonProperty("data")
    private DynamicQrCodeStatusCheckDataRequestDTO dynamicQrCodeStatusCheckDataRequestDTO;

    @JsonProperty("user_reference_number")
    private String userReferenceNumber;

    @JsonProperty("system_id")
    private String systemId;

    @JsonProperty("specific_partner_name")
    private String specificPartnerName;

}
