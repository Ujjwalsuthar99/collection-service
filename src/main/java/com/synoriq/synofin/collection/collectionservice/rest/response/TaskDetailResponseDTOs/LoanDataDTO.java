package com.synoriq.synofin.collection.collectionservice.rest.response.TaskDetailResponseDTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import software.amazon.ion.Decimal;

import java.util.List;

@Data
public class LoanDataDTO {

    @JsonProperty("outstanding_charges")
    List<OutStandingChargesDTO> outStandingCharges;

    @JsonProperty("date_of_receipt")
    String dateOfReceipt;

    @JsonProperty("addition_in_excess_money")
    Decimal additionInExcessMoney;

    @JsonProperty("loan_application_number")
    String loanApplicationNumber;

    @JsonProperty("loan_branch")
    String loanBranch;

    @JsonProperty("emi_cycle")
    String emiCycle;

    @JsonProperty("balance_principal")
    Double balancePrincipal;

    @JsonProperty("balance_emi")
    private Double balanceEmi;

    @JsonProperty("balance_emi_count")
    private Integer balanceEmiCount;

    @JsonProperty("overdue_emi")
    private Double overdueEmi;

    @JsonProperty("overdue_emi_count")
    private Integer overdueEmiCount;


}

