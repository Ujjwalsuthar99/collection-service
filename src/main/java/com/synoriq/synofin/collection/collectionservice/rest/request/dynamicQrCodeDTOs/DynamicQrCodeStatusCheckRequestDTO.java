package com.synoriq.synofin.collection.collectionservice.rest.request.dynamicQrCodeDTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.hibernate.annotations.Type;

import javax.persistence.Column;

@Data
public class DynamicQrCodeStatusCheckRequestDTO {

    @JsonProperty("merchantTranId")
    private String merchantTranId;

    @JsonProperty("loanId")
    private Long loanId;

    @JsonProperty("userId")
    private Long userId;

    @JsonProperty("geo_location_data")
    private Object geolocation;

    @JsonProperty("digital_payment_transaction_id")
    private Long digitalPaymentTransactionId;

    @JsonProperty("vendor")
    private String vendor;

}
