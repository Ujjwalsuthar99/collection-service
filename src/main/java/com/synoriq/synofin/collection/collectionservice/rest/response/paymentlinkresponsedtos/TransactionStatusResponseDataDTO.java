package com.synoriq.synofin.collection.collectionservice.rest.response.paymentlinkresponsedtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
public class TransactionStatusResponseDataDTO {

    @JsonProperty("status")
    private String status;

    @JsonProperty("order_id")
    private String orderId;

}
