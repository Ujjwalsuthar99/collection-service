package com.synoriq.synofin.collection.collectionservice.rest.request.paymentlinkdtos.statuscheckdtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionStatusCheckDTO {

    @JsonProperty("data")
    private TransactionStatusCheckDataDTO transactionStatusCheckDataDTO;

    @JsonProperty("user_reference_number")
    private String userReferenceNumber;

    @JsonProperty("system_id")
    private String systemId;

    @JsonProperty("specific_partner_name")
    private String specificPartnerName;

}
