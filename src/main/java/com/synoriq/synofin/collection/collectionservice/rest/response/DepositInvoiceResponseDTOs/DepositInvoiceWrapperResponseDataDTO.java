package com.synoriq.synofin.collection.collectionservice.rest.response.DepositInvoiceResponseDTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DepositInvoiceWrapperResponseDataDTO {

    @JsonProperty("successful_requests")
    private List<Long> successfulRequests;

    @JsonProperty("failed_requests")
    private List<Object> failedRequests;

    @JsonProperty("successful_request_count")
    private String successfulRequestCount;

    @JsonProperty("failed_request_count")
    private String failedRequestCount;

}
