package com.synoriq.synofin.collection.collectionservice.rest.response.repossessiondtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RepossessionRepoIdResponseDTO {

    @JsonProperty("repo_status")
    private String repoStatus;

    @JsonProperty("repo_initiate_date")
    private Date repoInitiateDate;

    @JsonProperty("loan_number")
    private String loanNumber;

    @JsonProperty("loan_id")
    private Long loanId;

    @JsonProperty("customer_name")
    private String customerName;

    @JsonProperty("mobile_number")
    private String mobileNumber;

    @JsonProperty("loan_amount")
    private Double loanAmount;

    @JsonProperty("emi_start_date")
    private String emiStartDate;

    @JsonProperty("dpd")
    private String dpd;

    @JsonProperty("vehicle_type")
    private String vehicleType;

    @JsonProperty("manufacturer")
    private String manufacturer;

    @JsonProperty("total_due")
    private Double totalDue;

    @JsonProperty("is_yard")
    private Boolean isYard;

    @JsonProperty("yard_details")
    private Object yardDetails;

    @JsonProperty("audit_logs")
    private List<Map<String, Object>> auditLogs;

}
