package com.synoriq.synofin.collection.collectionservice.rest.response.dummyTaskDetail;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;

@Data
public class DummyLoanDetails {
    @JsonProperty("loan_id")
    public String loanId;

    @JsonProperty("collection_visit_charges")
    public Double collectionVisitCharges;

    @JsonProperty("cheque_bounce_charges")
    public Double chequeBounceCharges;

    @JsonProperty("total_due_amount")
    public Double totalDueAmount;

    @JsonProperty("lpp")
    public Double lpp;

    @JsonProperty("legal_charges")
    public Double legalCharges;

    @JsonProperty("emi_amount")
    public Double emiAmount;
    @JsonProperty("visit_charges")
    public Double visitCharges;
    @JsonProperty("pos")
    public Integer pos;
    @JsonProperty("emi_start_date")
    public String emiStartDate;
}
