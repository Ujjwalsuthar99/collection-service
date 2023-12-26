package com.synoriq.synofin.collection.collectionservice.rest.response.RepossessionDTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

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

}
