package com.synoriq.synofin.collection.collectionservice.rest.response.DynamicQrCodeDTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DynamicQrCodeCheckStatusDataResponseDTO {

    @JsonProperty("OriginalBankRRN")
    private String originalBankRRN;

    @JsonProperty("Amount")
    private String amount;

    @JsonProperty("success")
    private String success;

    @JsonProperty("message")
    private String message;

    @JsonProperty("status")
    private String status;

    @JsonProperty("merchantTranId")
    private String merchantTranId;

}
