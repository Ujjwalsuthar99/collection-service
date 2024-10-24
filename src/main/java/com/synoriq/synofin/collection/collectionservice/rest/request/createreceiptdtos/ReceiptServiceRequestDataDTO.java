package com.synoriq.synofin.collection.collectionservice.rest.request.createreceiptdtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReceiptServiceRequestDataDTO {

    @JsonProperty("transaction_date")
    public String transactionDate;

    @JsonProperty("date_of_receipt")
    @JsonFormat(pattern = "yyyy/MM/dd")
    public String dateOfReceipt;

    @JsonProperty("payment_mode")
    public String paymentMode;

    @JsonProperty("excess_money")
    public String excessMoney;

    @JsonProperty("receipt_purpose")
    public String receiptPurpose;

    @JsonProperty("instrument_number")
    public String instrumentNumber;

    @JsonProperty("created_by")
    public String createdBy;

    @JsonProperty("payment_bank")
    public String paymentBank;

    @JsonProperty("instrument_bank_name")
    public String instrumentBankName;

    @JsonProperty("upi")
    public String upi;

    @JsonProperty("received_from")
    public String receivedFrom;

    @JsonProperty("is_nach")
    public String isNach;

    @JsonProperty("cash_receipt_number")
    public String cashReceiptNumber;

    @JsonProperty("instrument_date")
    public String instrumentDate;

    @JsonProperty("is_cheque")
    public Boolean isCheque;

    @JsonProperty("receivables")
    public List<Object> receivables;

    @JsonProperty("receipt_amount")
    public String receiptAmount;

    @JsonProperty("auto_allocated")
    public String autoAllocated;

    @JsonProperty("receipts_allocation")
    public String receiptsAllocation;

    @JsonProperty("bank_account_number")
    public String bankAccountNumber;


    @JsonProperty("transaction_reference")
    public String transactionReference;

    @JsonProperty("ifsc")
    public String ifsc;

    @JsonProperty("fund_transfer_number")
    public String fundTransferNumber;

    @JsonProperty("remarks")
    public String remarks;

    @JsonProperty("sourcing_branch")
    public String sourcingBranch;
}
