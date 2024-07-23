package com.synoriq.synofin.collection.collectionservice.rest.response.TaskDetailResponseDTOs;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.math.BigInteger;

public class FilterTaskResponseDTO implements Serializable {

    @JsonProperty("loan_application_id")
    private BigInteger loanApplicationId;
    @JsonProperty("branch")
    private String branch;
    @JsonProperty("customer_name")
    private String customerName;
    @JsonProperty("mobile")
    private String mobile;
    @JsonProperty("address")
    private String address;
    @JsonProperty("product")
    private String product;
    @JsonProperty("loan_application_number")
    private String loanApplicationNumber;
    @JsonProperty("task_purpose")
    private String taskPurpose;
    @JsonProperty("total_count")
    private BigInteger totalCount;
    @JsonProperty("days_past_due_bucket")
    private String daysPastDueBucket;
    @JsonProperty("days_past_due")
    private Integer daysPastDue;
    @JsonProperty("dpd_bg_color_key")
    private String dpdBgColorKey;
    @JsonProperty("dpd_text_color_key")
    private String dpdTextColorKey;

    public FilterTaskResponseDTO(Object[] result) {
        this.loanApplicationId = (BigInteger) result[0];
        this.branch = (String) result[1];
        this.customerName = (String) result[2];
        this.mobile = (String) result[3];
        this.address = (String) result[4];
        this.product = (String) result[5];
        this.loanApplicationNumber = (String) result[6];
        this.taskPurpose = (String) result[7];
        this.totalCount = (BigInteger) result[8];
        this.daysPastDueBucket = (String) result[9];
        this.daysPastDue = (Integer) result[10];
        this.dpdBgColorKey = (String) result[11];
        this.dpdTextColorKey = (String) result[12];
    }
    
}
