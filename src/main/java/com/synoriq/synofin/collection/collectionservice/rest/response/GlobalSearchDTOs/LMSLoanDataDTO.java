package com.synoriq.synofin.collection.collectionservice.rest.response.GlobalSearchDTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class LMSLoanDataDTO {

    @JsonProperty("dpd")
    int dpd;

    @JsonProperty("product")
    String product;

    @JsonProperty("scheme")
    String scheme;

    @JsonProperty("policies")
    String policies;

    @JsonProperty("branch")
    String branch;

    @JsonProperty("region")
    String region;

    @JsonProperty("area")
    String area;

    @JsonProperty("state")
    String state;

    @JsonProperty("loan_id")
    String loanId;

    @JsonProperty("application_form_number")
    String applicationFormNumber;

    @JsonProperty("loan_creation_date")
    String loanCreationDate;

    @JsonProperty("loan_modified_date")
    String loanModifiedDate;

    @JsonProperty("disbursal_date")
    String disbursalDate;

    @JsonProperty("loan_amount")
    String loanAmount;

    @JsonProperty("overdue_amount")
    Double overDueAmount;

    @JsonProperty("rate_of_interest")
    String rateOfInterest;

    @JsonProperty("emi_amount")
    String emiAmount;

    @JsonProperty("loan_application_number")
    String loanApplicationNumber;

    @JsonProperty("no_of_emi")
    String noOfEmi;

    @JsonProperty("loan_tenure")
    String loanTenure;

    @JsonProperty("loan_markings")
    String loanMarkings;

    @JsonProperty("loan_status")
    String loanStatus;

    @JsonProperty("searchable_date")
    Long searchableDate;

    @JsonProperty("customer_details")
    CustomerDetailsInLoanDataDTO customerDetails;

    @JsonProperty("disbursal_status")
    String disbursalStatus;

    @JsonProperty("cancellation_date")
    String cancellationDate;

    @JsonProperty("loan_closure_date")
    String loanClosureDate;

}

