package com.synoriq.synofin.collection.collectionservice.rest.response.ReceiptTransferDTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@Getter
@Setter
public class ReceiptTransferDataByReceiptIdResponseDTO {

    @JsonProperty("transfer_history_button")
    private Boolean transferHistoryButton;

    @JsonProperty("bank_transfer")
    private List<ReceiptTransferCustomDataResponseDTO> bankTransfer;

    @JsonProperty("user_transfer")
    private List<ReceiptTransferCustomDataResponseDTO> userTransfer;

    @JsonProperty("receipt_data")
    private ReceiptTransferReceiptDataResponseDTO receiptData;

}
