package com.synoriq.synofin.collection.collectionservice.rest.request.depositinvoicedtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DepositInvoiceWrapperRequestDataDTO {

    @JsonProperty("fund_transfer_number")
    private String fundTransferNumber;

}
