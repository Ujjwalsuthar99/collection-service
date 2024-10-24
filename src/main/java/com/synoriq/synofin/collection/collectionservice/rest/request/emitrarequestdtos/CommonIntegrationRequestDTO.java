package com.synoriq.synofin.collection.collectionservice.rest.request.emitrarequestdtos;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CommonIntegrationRequestDTO {

    @JsonProperty("data")
    private Object data;

    @JsonProperty("user_reference_number")
    private String userReferenceNumber;

    @JsonProperty("system_id")
    private String systemId;

    @JsonProperty("specific_partner_name")
    private String specificPartnerName;

    public CommonIntegrationRequestDTO(Object data, String userReferenceNumber, String systemId, String specificPartnerName) {
        this.data = data;
        this.userReferenceNumber = userReferenceNumber;
        this.systemId = systemId;
        this.specificPartnerName = specificPartnerName;
    }
}
