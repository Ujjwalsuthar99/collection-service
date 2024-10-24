package com.synoriq.synofin.collection.collectionservice.rest.response.paymentlinkresponsedtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.synoriq.synofin.collection.collectionservice.rest.commondto.IntegrationServiceErrorResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TransactionStatusResponseDTO {
    @JsonProperty("response")
    private Boolean response = false;

    @JsonProperty("data")
    private TransactionStatusResponseDataDTO data;

    @JsonProperty("request_id")
    private String requestId;

    @JsonProperty("error")
    private IntegrationServiceErrorResponseDTO error = null;
}
