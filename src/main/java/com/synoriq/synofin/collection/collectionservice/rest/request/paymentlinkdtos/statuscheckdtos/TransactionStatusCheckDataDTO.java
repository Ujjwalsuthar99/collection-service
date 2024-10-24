package com.synoriq.synofin.collection.collectionservice.rest.request.paymentlinkdtos.statuscheckdtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionStatusCheckDataDTO {

    @JsonProperty("id")
    private String id;

    @JsonProperty("product_type")
    private String productType;

}
