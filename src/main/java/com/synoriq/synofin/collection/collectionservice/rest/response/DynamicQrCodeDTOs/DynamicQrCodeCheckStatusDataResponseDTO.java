package com.synoriq.synofin.collection.collectionservice.rest.response.DynamicQrCodeDTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.synoriq.synofin.collection.collectionservice.rest.response.OcrCheckResponseDTOs.IntegrationServiceErrorResponseDTO;
import lombok.Data;

@Data
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
