package com.synoriq.synofin.collection.collectionservice.rest.response.dynamicqrcodedtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class DynamicQrCodeCheckStatusDataResponseDTO {

    @JsonProperty("OriginalBankRRN")
    private String originalBankRRN;

    @JsonProperty("response")
    private String response;

    @JsonProperty("merchantId")
    private String merchantId;

    @JsonProperty("subMerchantId")
    private String subMerchantId;

    @JsonProperty("terminalId")
    private String terminalId;

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

    @JsonProperty("payerAccountType")
    private String payerAccountType;

    @JsonProperty("sequenceNum")
    private String sequenceNum;

}
