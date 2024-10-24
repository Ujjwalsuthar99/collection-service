package com.synoriq.synofin.collection.collectionservice.rest.response.receipttransferdtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class ReceiptTransferCustomDataResponseDTO {

    @JsonProperty("receipt_transfer_id")
    private Long receiptTransferId;

    @JsonProperty("created_date")
    private String createdDate;

    @JsonProperty("transfer_by_name")
    private String transferByName;

    @JsonProperty("transfer_to_name")
    private String transferToName;

    @JsonProperty("transfer_type")
    private String transferType;

    @JsonProperty("deposit_amount")
    private Double depositAmount;

    @JsonProperty("approved_by")
    private String approvedBy;

    @JsonProperty("action_datetime")
    private String actionDateTime;

    @JsonProperty("bank_name")
    private String bankName;

    @JsonProperty("status")
    private String status;

    @JsonProperty("account_number")
    private String accountNumber;

    @JsonProperty("receipt_transfer_proofs")
    private Object receiptTransferProofs;

    @JsonProperty("transfer_location_data")
    private Object transferLocationData;

    @JsonProperty("approval_location_data")
    private Object approvalLocationData;
}
