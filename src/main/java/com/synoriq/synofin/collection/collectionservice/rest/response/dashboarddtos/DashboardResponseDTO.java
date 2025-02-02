package com.synoriq.synofin.collection.collectionservice.rest.response.dashboarddtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class DashboardResponseDTO {

    @JsonProperty("followup")
    private FollowUpDashboardDTO followUp;

    @JsonProperty("receipt")
    private CommonCountDashboardDTO receipt;

    @JsonProperty("cash_in_hand")
    private CashInHandDashboardDTO cashInHand;

    @JsonProperty("cheque_amount")
    private ChequeAmountDashboardDTO chequeAmount;

    @JsonProperty("upi_amount")
    private UpiAmountDashboardDTO upiAmount;

    @JsonProperty("amount_transfer")
    private CommonCountDashboardDTO amountTransfer;

    @JsonProperty("amount_transfer_inprocess")
    private CommonCountDashboardDTO amountTransferInProcess;

    @JsonProperty("task_count")
    private int taskCount;

    @JsonProperty("deposit_reminder")
    private boolean depositReminder;

    @JsonProperty("neft_amount")
    private NeftAmountDashboardDTO neftAmount;

    @JsonProperty("rtgs_amount")
    private RtgsAmountDashboardDTO rtgsAmount;

    @JsonProperty("total_payment_amount")
    private Double totalPaymentAmount;

}
