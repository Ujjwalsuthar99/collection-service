package com.synoriq.synofin.collection.collectionservice.rest.response.TaskDetailResponseDTOs.LoanSummaryForLoanDTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.synoriq.synofin.collection.collectionservice.rest.response.TaskDetailResponseDTOs.CustomerDataResponseDTO;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LoanSummaryDataResponseDTO {

    @JsonProperty("number_of_installments")
    public NumberOfInstallmentsResponseDTO numberOfInstallments;

    @JsonProperty("installment_amount")
    public InstallmentAmountResponseDTO installmentAmount;

    @JsonProperty("receivable_charges")
    public Object receivableCharges;

    @JsonProperty("payable_charges")
    public Object payableCharges;

    @JsonProperty("branch_hierarcy")
    public Object branchHierarcy;

}
