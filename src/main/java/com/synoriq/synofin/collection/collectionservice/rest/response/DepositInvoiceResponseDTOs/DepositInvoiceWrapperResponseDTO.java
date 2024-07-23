package com.synoriq.synofin.collection.collectionservice.rest.response.DepositInvoiceResponseDTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DepositInvoiceWrapperResponseDTO {

    @JsonProperty("response")
    private Boolean response;

    @JsonProperty("data")
    private DepositInvoiceWrapperResponseDataDTO data;

    @JsonProperty("error")
    private Object error;

}
