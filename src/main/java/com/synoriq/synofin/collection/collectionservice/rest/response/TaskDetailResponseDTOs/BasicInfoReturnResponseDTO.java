package com.synoriq.synofin.collection.collectionservice.rest.response.TaskDetailResponseDTOs;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.persistence.criteria.CriteriaBuilder;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BasicInfoReturnResponseDTO {


    @JsonProperty("id")
    public Long id;

    @JsonProperty("first_name")
    public String firstName;

    @JsonProperty("middle_name")
    public String middleName;

    @JsonProperty("last_name")
    public String lastName;

    @JsonProperty("relation")
    public String relation;

    @JsonProperty("dob")
    public String dob;

    @JsonProperty("loan_amount")
    public Double loanAmount;

    @JsonProperty("dpd")
    public Integer dpd;

    @JsonProperty("dpd_bucket")
    public String dpdBucket;

    @JsonProperty("dpd_bg_color")
    public String dpdBgColor;

    @JsonProperty("dpd_text_color")
    public String dpdTextColor;

    @JsonProperty("pos")
    public Double pos;

    @JsonProperty("loan_tenure")
    public Integer loanTenure;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    @JsonProperty("emi_date")
    public String emiDate;
}
