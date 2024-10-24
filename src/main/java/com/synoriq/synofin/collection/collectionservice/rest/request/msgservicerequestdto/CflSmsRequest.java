package com.synoriq.synofin.collection.collectionservice.rest.request.msgservicerequestdto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class CflSmsRequest {

    @JsonProperty("data")
    private RequestDataDTO data;

    @JsonProperty("user_reference_number")
    private String userReferenceNumber;

    @JsonProperty("system_id")
    private String systemId;

    @JsonProperty("specific_partner_name")
    private String specificPartnerName;

}
