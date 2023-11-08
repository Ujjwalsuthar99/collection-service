package com.synoriq.synofin.collection.collectionservice.rest.request.dynamicQrCodeDTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class DynamicQrCodeCallBackRequestDTO {

    @JsonProperty("merchantId")
    private String merchantId;

    @JsonProperty("subMerchantId")
    private String subMerchantId;

    @JsonProperty("terminalId")
    private String terminalId;

    @JsonProperty("BankRRN")
    private String BankRRN;

    @JsonProperty("merchantTranId")
    private String merchantTranId;

    @JsonProperty("PayerName")
    private String PayerName;

    @JsonProperty("PayerMobile")
    private String PayerMobile;

    @JsonProperty("PayerVA")
    private String PayerVA;

    @JsonProperty("PayerAmount")
    private String PayerAmount;

    @JsonProperty("TxnStatus")
    private String TxnStatus;

    @JsonProperty("TxnInitDate")
    private String TxnInitDate;

    @JsonProperty("TxnCompletionDate")
    private String TxnCompletionDate;

}



