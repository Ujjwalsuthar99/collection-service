package com.synoriq.synofin.collection.collectionservice.rest.request.receipttransferdtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReceiptTransferAirtelDepositStatusRequestDTO {

    @JsonProperty("receipt_transfer_id")
    private Long receiptTransferId;

    @JsonProperty("status")
    private String status;

    @JsonProperty("utr_number")
    private String utrNumber;

    @JsonProperty("transaction_id")
    private String transactionId;

}
