package com.synoriq.synofin.collection.collectionservice.rest.response.TaskDetailResponseDTOs.LoanSummaryForLoanDTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class NumberOfInstallmentsResponseDTO {

    @JsonProperty("paid")
    private Integer paid;

    @JsonProperty("waived_amount")
    private Double waivedAmount;

    @JsonProperty("dues_as_on_date")
    private Integer duesAsOnDate;

    @JsonProperty("net_amount")
    private Double netAmount;

}