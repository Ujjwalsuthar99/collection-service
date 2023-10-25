package com.synoriq.synofin.collection.collectionservice.rest.request.dynamicQrCodeDTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class DynamicQrCodeDataRequestDTO {

    @JsonProperty("amount")
    private String amount;

    @JsonProperty("merchantTranId")
    private String merchantTranId;

    @JsonProperty("billNumber")
    private String billNumber;

    @JsonProperty("payerAccount")
    private String payerAccount;

    @JsonProperty("payerIFSC")
    private String payerIFSC;

    @JsonProperty("firstName")
    private String firstName;

    @JsonProperty("lastName")
    private String lastName;


}
