package com.synoriq.synofin.collection.collectionservice.rest.request.dynamicQrCodeDTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.hibernate.annotations.Type;
import software.amazon.ion.Decimal;

import javax.persistence.Column;

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

    @JsonProperty("vendor")
    private String vendor;

    @JsonProperty("geo_location_data")
    private Object geolocation;

    @JsonProperty("receipt_request_body")
    private Object receiptRequestBody;

}
