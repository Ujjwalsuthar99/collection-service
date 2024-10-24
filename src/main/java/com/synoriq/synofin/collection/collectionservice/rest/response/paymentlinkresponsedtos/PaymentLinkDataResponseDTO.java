package com.synoriq.synofin.collection.collectionservice.rest.response.paymentlinkresponsedtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentLinkDataResponseDTO {

    @JsonProperty("accept partial")
    private String acceptPartial;
    @JsonProperty("amount")
    private String amount;
    @JsonProperty("url")
    private String url;
    @JsonProperty("status")
    private String status;
    @JsonProperty("reference_id")
    private String referenceId;
    @JsonProperty("id")
    private String id;
}
