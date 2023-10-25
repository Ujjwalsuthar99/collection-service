package com.synoriq.synofin.collection.collectionservice.rest.response.DynamicQrCodeDTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.synoriq.synofin.collection.collectionservice.rest.response.OcrCheckResponseDTOs.IntegrationServiceErrorResponseDTO;
import lombok.Data;

@Data
public class DynamicQrCodeCheckStatusDataResponseDTO {

    @JsonProperty("response")
    private String response;

    @JsonProperty("merchantId")
    private String merchantId;

    @JsonProperty("subMerchantId")
    private String subMerchantId;

    @JsonProperty("terminalId")
    private String terminalId;

    @JsonProperty("OriginalBankRRN")
    private String OriginalBankRRN;

    @JsonProperty("Amount")
    private String Amount;

    @JsonProperty("success")
    private String success;

    @JsonProperty("message")
    private String message;

    @JsonProperty("status")
    private String status;

    @JsonProperty("merchantTranId")
    private String merchantTranId;

    @JsonProperty("payerAccountType")
    private String payerAccountType     ;

    @JsonProperty("sequenceNum")
    private String sequenceNum;

}
