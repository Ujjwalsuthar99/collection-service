package com.synoriq.synofin.collection.collectionservice.rest.request.repossessionDTOs.lmsRepossession;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;

@Data
@NoArgsConstructor
public class LmsRepossessionDTO {

    @JsonProperty("service_request_type")
    public String serviceRequestType;

    @JsonProperty("service_request_subtype")
    public String serviceRequestSubtype;

    @JsonProperty("service_type")
    public String serviceType;

    @JsonProperty("loan_id")
    public String loanId;

    @JsonProperty("request_data")
    public LmsRepossessionDataDTO requestData;

    @JsonProperty("service_request_id")
    public String serviceRequestId;

}
