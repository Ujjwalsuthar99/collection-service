package com.synoriq.synofin.collection.collectionservice.rest.response.PaymentLinkResponseDTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.synoriq.synofin.collection.collectionservice.rest.commondto.IntegrationServiceErrorResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentLinkResponseDTO {

    @JsonProperty("response")
    private Boolean response = false;

    @JsonProperty("data")
    private PaymentLinkDataResponseDTO data;

    @JsonProperty("error")
    private IntegrationServiceErrorResponseDTO error = null;
}
