package com.synoriq.synofin.collection.collectionservice.rest.response.dummyTaskDetail;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class DummyLoanDetails {
    @JsonProperty("loan_id")
    public String loanId;

    @JsonProperty("collection_visit_charges")
    public Double collectionVisitCharges;

    @JsonProperty("bounce_charges")
    public Double bounceCharges;

    @JsonProperty("total_due_amount")
    public Double totalDueAmount;

    @JsonProperty("lpp")
    public Double lpp;

    @JsonProperty("legal_charges")
    public Double legalCharges;

    @JsonProperty("emi_amount")
    public Double emiAmount;


}
