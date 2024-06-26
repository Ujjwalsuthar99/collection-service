package com.synoriq.synofin.collection.collectionservice.rest.response.DynamicQrCodeDTOs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.synoriq.synofin.collection.collectionservice.rest.request.dynamicQrCodeDTOs.DynamicQrCodeDataRequestDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class DynamicQrCodeDataResponseDTO {

    @JsonProperty("merchantTranId")
    private String merchantTranId;

    @JsonProperty("refId")
    private String refId;

    @JsonProperty("status")
    private String status;

    @JsonProperty("message")
    private String message;

    @JsonProperty("link")
    private String link;

    @JsonProperty("digitalPaymentTransactionsId")
    private Long digitalPaymentTransactionsId;

    @JsonProperty("expiredTime")
    private String expiredTime;

}
