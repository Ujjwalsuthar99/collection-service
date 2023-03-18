package com.synoriq.synofin.collection.collectionservice.rest.response.TaskDetailResponseDTOs;
    

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import software.amazon.ion.Decimal;
import java.util.List;

@Data
@Getter
@Setter
public class LoanBasicDetailsDTO {
    @JsonProperty("dpd")
    private Integer dpd;
    
    @JsonProperty("email")
    private Object email;
    
    @JsonProperty("address")
    private String address;
    
    @JsonProperty("tenure")
    private Object tenure;
    
    @JsonProperty("foir")
    private Double foir;
    
    @JsonProperty("source_application_number")
    private String sourceApplicationNumber;
    
    @JsonProperty("dealer_id")
    private Object dealerId;
    
    @JsonProperty("loan_application_id")
    private Integer loanApplicationId;
    
    @JsonProperty("interest_rate")
    private Double interestRate;
    
    @JsonProperty("original_tenure")
    private Integer originalTenure;
    
    @JsonProperty("ltv_ratio")
    private Double ltvRatio;
    
    @JsonProperty("loan_amount")
    private Double loanAmount;
    
    @JsonProperty("loan_tenure")
    private Integer loanTenure;
    
    @JsonProperty("installments_paid")
    private Integer installmentsPaid;
    
    @JsonProperty("loan_marking")
    private List<Object> loanMarking;
    
    @JsonProperty("installment_amount")
    private Double installmentAmount;
    
    @JsonProperty("principal_outstanding")
    private Double principalOutstanding;
    
    @JsonProperty("customer_name")
    private String customerName;
    
    @JsonProperty("phone_no")
    private String phoneNo;
    
    @JsonProperty("is_whatsapp_no")
    private Object isWhatsappNo;
    
    @JsonProperty("advance_installment_count")
    private Integer advanceInstallmentCount;
    
    @JsonProperty("advance_installment_amount")
    private Double advanceInstallmentAmount;
    
    @JsonProperty("total_overdue")
    private Double totalOverdue;
    
    @JsonProperty("emi_due")
    private Double emiDue;
    
    @JsonProperty("other_charges")
    private Double otherCharges;
    
    @JsonProperty("disbursed_by")
    private Object disbursedBy;
    
    @JsonProperty("source_application_identifier")
    private Object sourceApplicationIdentifier;
    
    @JsonProperty("product")
    private String product;
    
    @JsonProperty("scheme")
    private String scheme;
    
    @JsonProperty("loan_type")
    private Object loanType;
    
    @JsonProperty("loan_application_number")
    private String loanApplicationNumber;
    
    @JsonProperty("balance_principal")
    private Double balancePrincipal;
    
    @JsonProperty("interest_rate_monthly")
    private Double interestRateMonthly;
    
    @JsonProperty("no_of_disbursement")
    private Integer noOfDisbursement;
    
    @JsonProperty("customer_id")
    private Integer customerId;
    
    @JsonProperty("lending_rate")
    private Object lendingRate;
    
    @JsonProperty("scheme_lending_rate")
    private Object schemeLendingRate;
    
    @JsonProperty("emi_amount")
    private Double emiAmount;
    
    @JsonProperty("utilized_limit")
    private Double utilizedLimit;
    
    @JsonProperty("dsa_name")
    private Object dsaName;
    
    @JsonProperty("unrealized_limit_amount")
    private Double unrealizedLimitAmount;
    
    @JsonProperty("applied_amount")
    private Object appliedAmount;
    
    @JsonProperty("branch_manager")
    private String branchManager;
    
    @JsonProperty("sourcing_channel")
    private Object sourcingChannel;
    
    @JsonProperty("risk_category")
    private Object riskCategory;
    
    @JsonProperty("sourcing_channel_type")
    private String sourcingChannelType;
    
    @JsonProperty("pd_location")
    private Object pdLocation;
    
    @JsonProperty("account_classification")
    private String accountClassification;
    
    @JsonProperty("drawing_power")
    private Integer drawingPower;
    
    @JsonProperty("lien_mark")
    private String lienMark;
    
    @JsonProperty("lien_amount")
    private Double lienAmount;
    
    @JsonProperty("tos")
    private Double tos;
    
    @JsonProperty("debit_freeze")
    private String debitFreeze;
    
    @JsonProperty("total_freeze")
    private String totalFreeze;
    
    @JsonProperty("pemi_count")
    private Integer pemiCount;
    
    @JsonProperty("loan_net_worth")
    private Object loanNetWorth;
    
    @JsonProperty("start_principle_recovery")
    private Boolean startPrincipleRecovery;
    
    @JsonProperty("caf_number")
    private Object cafNumber;
    
    @JsonProperty("no_of_cheque_bounce")
    private Integer noOfChequeBounce;
    
    @JsonProperty("dealer_code")
    private Object dealerCode;
    
    @JsonProperty("advance_interest")
    private Object advanceInterest;
    
    @JsonProperty("advance_emi")
    private Object advanceEmi;
    
    @JsonProperty("advance_emi_payment_mode")
    private Object advanceEmiPaymentMode;
    
    @JsonProperty("advance_interest_payment_mode")
    private Object advanceInterestPaymentMode;
    
    @JsonProperty("total_moratorium_count")
    private Integer totalMoratoriumCount;
    
    @JsonProperty("next_emi_amount")
    private Double nextEmiAmount;
    
    @JsonProperty("grace_period")
    private Object gracePeriod;
    
    @JsonProperty("delinquency")
    private Object delinquency;
    
    @JsonProperty("moratorium_period")
    private Object moratoriumPeriod;
    
    @JsonProperty("advance_amount_recovery_type")
    private Object advanceAmountRecoveryType;
    
    @JsonProperty("total_nach_bounce_count")
    private Integer totalNachBounceCount;
    
    @JsonProperty("total_count_of_repayment")
    private Integer totalCountOfRepayment;
    
    @JsonProperty("whatsapp_no")
    private Object whatsappNo;
    
    @JsonProperty("aggregated_ltv")
    private Object aggregatedLtv;
    
    @JsonProperty("overline_amount")
    private Double overlineAmount;
    
    @JsonProperty("total_number_of_bounce_count")
    private Integer totalNumberOfBounceCount;
    
    @JsonProperty("moratorium_type")
    private Object moratoriumType;
    
    @JsonProperty("moratorium_months")
    private Object moratoriumMonths;
    
    @JsonProperty("disbursal_status")
    private String disbursalStatus;
    
    @JsonProperty("sourcing_rm")
    private String sourcingRm;
    
    @JsonProperty("loan_status")
    private String loanStatus;
    
    @JsonProperty("credit_manager")
    private String creditManager;
    
    @JsonProperty("sourcing_branch")
    private String sourcingBranch;
    
    @JsonProperty("disbursal_date")
    private String disbursalDate;
    
    @JsonProperty("maturity_date")
    private String maturityDate;
    
    @JsonProperty("asset_classification")
    private String assetClassification;
    
    @JsonProperty("tenure_convention")
    private Object tenureConvention;
    
    @JsonProperty("interest_start_date")
    private String interestStartDate;
    
    @JsonProperty("repayment_start_date")
    private String repaymentStartDate;
    
    @JsonProperty("repayment_mode")
    private Object repaymentMode;
    
    @JsonProperty("disbursed_start_date")
    private String disbursedStartDate;
    
    @JsonProperty("disbursed_final_date")
    private String disbursedFinalDate;
    
    @JsonProperty("agreement_date")
    private Object agreementDate;
    
    @JsonProperty("string_interest_rate")
    private String stringInterestRate;
    
    @JsonProperty("string_interest_rate_monthly")
    private String stringInterestRateMonthly;
    
    @JsonProperty("rate_type")
    private Object rateType;
    
    @JsonProperty("total_limit")
    private Double totalLimit;
    
    @JsonProperty("remaining_limit")
    private Double remainingLimit;
    
    @JsonProperty("login_date")
    private Object loginDate;
    
    @JsonProperty("total_due_amount")
    private Double totalDueAmount;
    
    @JsonProperty("irr")
    private Double irr;
    
    @JsonProperty("balance_tenure")
    private Integer balanceTenure;
    
    @JsonProperty("recovery_type")
    private String recoveryType;
    
    @JsonProperty("recovery_sub_type")
    private String recoverySubType;
    
    @JsonProperty("relationship_with_applicant")
    private Object relationshipWithApplicant;
    
    @JsonProperty("customer_nationality")
    private Object customerNationality;
    
    @JsonProperty("segement")
    private Object segement;
    
    @JsonProperty("offer_amount")
    private Object offerAmount;
    
    @JsonProperty("partial_tenure")
    private Object partialTenure;
    
    @JsonProperty("subvention_amount")
    private Object subventionAmount;
    
    @JsonProperty("over_line")
    private String overLine;
    
    @JsonProperty("partner")
    private Object partner;
    
    @JsonProperty("subvention_tax_amount")
    private Object subventionTaxAmount;
    
    @JsonProperty("subvention_type")
    private Object subventionType;
    
    @JsonProperty("state_head")
    private String stateHead;
    
    @JsonProperty("branch_hierarcy")
    private Object branchHierarcy;
    
    @JsonProperty("cost_of_asset")
    private Object costOfAsset;
    
    @JsonProperty("product_type")
    private String productType;
    
    @JsonProperty("customer_irr")
    private Object customerIrr;
    
    @JsonProperty("over_due")
    private Object overDue;
    
    @JsonProperty("sourcing_rm_name")
    private Object sourcingRmName;
    
    @JsonProperty("sourcing_supervisor")
    private Object sourcingSupervisor;
    
    @JsonProperty("cancellation_date")
    private Object cancellationDate;
    
    @JsonProperty("loan_closure_date")
    private Object loanClosureDate;
    
    @JsonProperty("dpd_effective_date")
    private Object dpdEffectiveDate;
    
    @JsonProperty("agreed_amount")
    private Double agreedAmount;
    

}

