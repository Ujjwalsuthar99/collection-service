package com.synoriq.synofin.collection.collectionservice.rest.request.createreceiptdtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.synoriq.synofin.collection.collectionservice.rest.request.CollectionActivityLogDTO;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReceiptServiceDtoRequest {

    @JsonProperty("data")
    private ReceiptServiceDataDTO requestData;

    @JsonProperty("activity_data")
    private CollectionActivityLogDTO activityData;

    @JsonProperty("loan_application_number")
    private String loanApplicationNumber;

    @JsonProperty("user_reference_number")
    private String userReferenceNumber;

    @JsonProperty("customer_type")
    private String customerType;

    @JsonProperty("applicant_mobile_number")
    private String applicantMobileNumber;

    @JsonProperty("collected_from_number")
    private String collectedFromNumber;

    @JsonProperty("customer_name")
    private String customerName;

}
