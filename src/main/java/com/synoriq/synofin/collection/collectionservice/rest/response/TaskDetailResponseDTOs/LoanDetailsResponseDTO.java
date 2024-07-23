package com.synoriq.synofin.collection.collectionservice.rest.response.TaskDetailResponseDTOs;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import software.amazon.ion.Decimal;

import java.util.List;
@Data
public class LoanDetailsResponseDTO {

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

    @JsonProperty("emi_paid")
    private Double emiPaid;

    @JsonProperty("emi_paid_count")
    private Integer emiPaidCount;

    @JsonProperty("repo_status")
    private String repoStatus;

    @JsonProperty("repo_id")
    private Long repoId;

    @JsonProperty("repo_card_show")
    private Boolean repoCardShow;

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

    @JsonProperty("asset_classification")
    public String assetClassification;

    @JsonProperty("loan_tenure")
    public Integer loanTenure;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    @JsonProperty("emi_date")
    public String emiDate;

    @JsonProperty("emi_amount")
    public Double emiAmount;


}
