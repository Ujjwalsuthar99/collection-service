package com.synoriq.synofin.collection.collectionservice.rest.response.TaskDetailResponseDTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import software.amazon.ion.Decimal;

@Data
public class OutStandingChargesDTO {

    @JsonProperty("charge_id")
    Integer chargeId;

    @JsonProperty("charge_label")
    String chargeLabel;

    @JsonProperty("total_outstanding_amount")
    Decimal totalOutstandingAmount;

    @JsonProperty("type")
    String type;

    @JsonProperty("original_amount")
    Decimal originalAmount;

    @JsonProperty("charge_definition_id")
    Integer chargeDefinitionId;

    @JsonProperty("excess_money_id")
    Integer excessMoneyId;

    @JsonProperty("loan_id")
    Integer loanId;

    @JsonProperty("disbursal_schedule_id")
    Integer disbursalScheduleId;

    @JsonProperty("charge_code")
    Object chargeCode;

    @JsonProperty("outstanding_amount")
    Decimal outstandingAmount;

    @JsonProperty("amount_in_process")
    Decimal amountInProcess;

    @JsonProperty("payment_status")
    String paymentStatus;

}
