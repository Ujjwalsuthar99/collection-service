package com.synoriq.synofin.collection.collectionservice.rest.request.ocrcheckdtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class OcrCheckRequestDTO {

    @JsonProperty("data")
    private OcrCheckRequestDataDTO data;

    @JsonProperty("user_reference_number")
    private String userReferenceNumber;

    @JsonProperty("system_id")
    private String systemId;

    @JsonProperty("specific_partner_name")
    private String specificPartnerName;

}
