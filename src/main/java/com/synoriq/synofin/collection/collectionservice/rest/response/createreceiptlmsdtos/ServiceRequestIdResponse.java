package com.synoriq.synofin.collection.collectionservice.rest.response.createreceiptlmsdtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ServiceRequestIdResponse {

    @JsonProperty("result")
    public Object result;

    @JsonProperty("status")
    public String status;

    @JsonProperty("service_request_id")
    public Long serviceRequestId;

    @JsonProperty("unique_transaction_number")
    public Object uniqueTransactionNumber;

}
