package com.synoriq.synofin.collection.collectionservice.rest.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.synoriq.synofin.collection.collectionservice.entity.ReceiptTransferEntity;
import com.synoriq.synofin.collection.collectionservice.rest.response.userDetailsByUserIdDTOs.UserDataReturnResponseDTO;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReceiptTransferResponseDTO {

    @JsonProperty("receipt_transfer_data")
    ReceiptTransferEntity receiptTransferData;

    @JsonProperty("receipt_data")
    List<Map<String, Object>> receiptData;

    @JsonProperty("receiver_user")
    UserDataReturnResponseDTO transferToUserData;

    @JsonProperty("transfer_user")
    UserDataReturnResponseDTO transferByUserData;

    @JsonProperty("amount_in_hand")
    Double amountInHand;

    @JsonProperty("button_restriction")
    Boolean buttonRestriction;

}
