package com.synoriq.synofin.collection.collectionservice.rest.response.depositinvoiceresponsedtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DepositInvoiceResponseDataDTO {

    @JsonProperty("successful_request_count")
    private Long successfulRequestCount;

    @JsonProperty("failed_request_count")
    private Long failedRequestCount;

}
