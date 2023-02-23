package com.synoriq.synofin.collection.collectionservice.rest.request.createReceiptDTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

@Data
public class requestDataDTO {
    @JsonProperty("service_request_type")
    public String serviceRequestType;

    @JsonProperty("service_request_subtype")
    public String serviceRequestSubtype;

    @JsonProperty("loan_id")
    public String loanId;

    @JsonProperty("request_source")
    public String requestSource;

    @JsonProperty("request_data")
    public Object requestData;
}
