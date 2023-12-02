package com.synoriq.synofin.collection.collectionservice.rest.request.dynamicQrCodeDTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class DynamicQrCodeStatusCheckRequestDTO {

    @JsonProperty("merchant_tran_id")
    private String merchantTranId;

//    @JsonProperty("loan_id")
//    private Long loanId;

//    @JsonProperty("user_id")
//    private Long userId;

//    @JsonProperty("mobile_number")
//    private String mobileNumber;

    @JsonProperty("geo_location_data")
    private Object geolocation;

    @JsonProperty("digital_payment_transaction_id")
    private Long digitalPaymentTransactionId;

//    @JsonProperty("vendor")
//    private String vendor;

}
