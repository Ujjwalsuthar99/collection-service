package com.synoriq.synofin.collection.collectionservice.rest.response.paymentlinkresponsedtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.synoriq.synofin.collection.collectionservice.rest.commondto.IntegrationServiceErrorResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentLinkResponseDTO {

    @JsonProperty("response")
    private Boolean response = false;

    @JsonProperty("data")
    private PaymentLinkDataResponseDTO data;

    @JsonProperty("request_id")
    private String requestId;

    @JsonProperty("error")
    private IntegrationServiceErrorResponseDTO error = null;
}
