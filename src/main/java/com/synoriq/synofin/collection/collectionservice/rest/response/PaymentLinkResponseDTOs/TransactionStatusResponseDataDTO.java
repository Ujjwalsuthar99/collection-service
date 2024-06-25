package com.synoriq.synofin.collection.collectionservice.rest.response.PaymentLinkResponseDTOs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransactionStatusResponseDataDTO {

    @JsonProperty("status")
    private String status;

    @JsonProperty("order_id")
    private String orderId;

}
