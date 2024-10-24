package com.synoriq.synofin.collection.collectionservice.rest.request.dynamicqrcodedtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class DynamicQrCodeRequestDTO {

    @JsonProperty("amount")
    private String amount;

    @JsonProperty("payerAccount")
    private String payerAccount;

    @JsonProperty("payerIFSC")
    private String payerIFSC;

    @JsonProperty("firstName")
    private String firstName;

    @JsonProperty("lastName")
    private String lastName;

    @JsonProperty("loanId")
    private Long loanId;

    @JsonProperty("userId")
    private Long userId;

    @JsonProperty("mobileNumber")
    private String mobileNumber;

    @JsonProperty("vendor")
    private String vendor;

    @JsonProperty("geo_location_data")
    private Object geolocation;

    @JsonProperty("receipt_request_body")
    private Object receiptRequestBody;

}
