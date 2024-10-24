package com.synoriq.synofin.collection.collectionservice.rest.request.dynamicqrcodedtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
//@JsonIgnoreProperties(ignoreUnknown = true)
public class CommonTransactionStatusCheckRequestDTO {

    @JsonProperty("merchant_tran_id")
    private String merchantTranId;

    @NotNull(message = "Error show ho jaye")
    @JsonProperty("battery_percentage")
    private Long batteryPercentage;

    @JsonProperty("geo_location_data")
    private Object geolocation;

    @JsonProperty("digital_payment_transaction_id")
    private Long digitalPaymentTransactionId;

}
