package com.synoriq.synofin.collection.collectionservice.rest.request.depositinvoicedtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
public class DepositInvoiceWrapperRequestListDTO {

    @JsonProperty("receiptObjectData")
    private List<DepositInvoiceWrapperRequestDTO> receiptObjectData;

}
