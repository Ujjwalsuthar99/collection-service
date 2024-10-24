package com.synoriq.synofin.collection.collectionservice.rest.request.paymentlinkdtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
@Data
public class PaymentLinkRequestDTO {

    @JsonProperty("data")
    private PaymentLinkDataRequestDTO paymentLinkDataRequestDTO;

    @JsonProperty("user_reference_number")
    private String userReferenceNumber;

    @JsonProperty("system_id")
    private String systemId;

    @JsonProperty("specific_partner_name")
    private String specificPartnerName;

}
