package com.synoriq.synofin.collection.collectionservice.rest.response.globalsearchdtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TaskListDTOReturnResponse {

    @JsonProperty("loan_application_id")
    Long loanApplicationId;

    @JsonProperty("days_past_due_bucket")
    String daysPastDueBucket;

    @JsonProperty("customer_name")
    String customerName;

    @JsonProperty("mobile")
    String mobile;

    @JsonProperty("loan_application_number")
    String loanApplicationNumber;

    @JsonProperty("dpd_text_color_key")
    String dpdTextColorKey;

    @JsonProperty("product")
    String product;

    @JsonProperty("dpd_bg_color_key")
    String dpdBgColorKey;

    @JsonProperty("days_past_due")
    int daysPastDue;

    @JsonProperty("address")
    String address;

    @JsonProperty("overdue_repayment")
    Double overdueRepayment;

    @JsonProperty("branch")
    String branch;

}
