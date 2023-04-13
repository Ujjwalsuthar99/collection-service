package com.synoriq.synofin.collection.collectionservice.rest.request.createReceiptDTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.synoriq.synofin.lms.commondto.dto.collection.CollectionActivityLogDTO;
import lombok.Data;

@Data
public class ReceiptServiceDtoRequest {

    @JsonProperty("data")
    private ReceiptServiceDataDTO requestData;

    @JsonProperty("activity_data")
    private CollectionActivityLogDTO activityData;

    @JsonProperty("loan_application_number")
    private String loanApplicationNumber;

    @JsonProperty("user_reference_number")
    private String userReferenceNumber;

}
