package com.synoriq.synofin.collection.collectionservice.rest.response.DepositInvoiceResponseDTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DepositInvoiceResponseDTO {

    @JsonProperty("response")
    private Boolean response;

    @JsonProperty("data")
    private DepositInvoiceResponseDataDTO data;

    @JsonProperty("error")
    private Object error;

}
