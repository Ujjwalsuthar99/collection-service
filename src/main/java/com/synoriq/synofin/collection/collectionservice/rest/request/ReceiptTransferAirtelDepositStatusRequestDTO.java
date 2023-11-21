package com.synoriq.synofin.collection.collectionservice.rest.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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

}
