package com.synoriq.synofin.collection.collectionservice.rest.request.depositinvoicedtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DepositInvoiceRequestDTO {

    @JsonProperty("receipt_transfer_id")
    private Long receiptTransferId;

    @JsonProperty("utr_number")
    private String utrNumber;

    @JsonProperty("remarks")
    private String remarks;

    @JsonProperty("action")
    private String action;

}
