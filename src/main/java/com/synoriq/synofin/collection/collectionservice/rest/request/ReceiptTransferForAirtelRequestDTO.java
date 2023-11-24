package com.synoriq.synofin.collection.collectionservice.rest.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ReceiptTransferForAirtelRequestDTO {

    @JsonProperty("receipt_transfer_id")
    private Long receiptTransferId;

}



