package com.synoriq.synofin.collection.collectionservice.rest.request.dynamicQrCodeDTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class DynamicQrCodeCallBackRequestDTO {

    @JsonProperty("merchantId")
    private String merchantId;

    @JsonProperty("OriginalBankRRN")
    private String originalBankRRN;

    @JsonProperty("merchantTranId")
    private String merchantTranId;

    @JsonProperty("Amount")
    private String amount;

    @JsonProperty("success")
    private String success;

    @JsonProperty("message")
    private String message;

    @JsonProperty("status")
    private String status;

    @JsonProperty("TxnInitDate")
    private String txnInitDate;

    @JsonProperty("vendor")
    private String vendor;

}



