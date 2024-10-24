package com.synoriq.synofin.collection.collectionservice.rest.request.createreceiptdtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ReceiptServiceDataDTO {
    @JsonProperty("service_request_type")
    public String serviceRequestType;

    @JsonProperty("service_request_subtype")
    public String serviceRequestSubtype;

    @JsonProperty("loan_id")
    public String loanId;

    @JsonProperty("request_source")
    public String requestSource;

    @JsonProperty("request_data")
    public ReceiptServiceRequestDataDTO requestData;

    @JsonProperty("is_auto_approved")
    public boolean isAutoApproved;
}
