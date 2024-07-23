package com.synoriq.synofin.collection.collectionservice.rest.request.depositInvoiceDTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DepositInvoiceWrapperRequestDTO {

    @JsonProperty("action")
    private String action;

    @JsonProperty("action_by")
    private String actionBy;

    @JsonProperty("comment")
    private String comment;

    @JsonProperty("service_request_id")
    private Long serviceRequestId;

    @JsonProperty("loan_id")
    private Long loanId;

    @JsonProperty("req_source")
    private String reqSource;

    @JsonProperty("req_data")
    private DepositInvoiceWrapperRequestDataDTO reqData;


}
