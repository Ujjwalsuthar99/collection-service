package com.synoriq.synofin.collection.collectionservice.rest.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.synoriq.synofin.utilityservice.utility.DateTime;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class ReceiptTransferDtoRequest {

    Long transferredBy;
    Boolean deleted;
    String transferType;
    String transferMode;
    Long transferredToUserId;
    Double amount;
    Object receiptImage;
    String status;
    String remarks;
    String transferBankCode;
    Date actionDatetime;
    String actionReason;
    String actionRemarks;
    Long actionBy;
    Long collectionActivityLogsId;
    List<Long> receipts;

}
