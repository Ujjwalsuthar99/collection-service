package com.synoriq.synofin.collection.collectionservice.rest.response.DepositInvoiceResponseDTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DepositInvoiceResponseDataDTO {

    @JsonProperty("successful_request_count")
    private Long successfulRequestCount;

    @JsonProperty("failed_request_count")
    private Long failedRequestCount;

}
