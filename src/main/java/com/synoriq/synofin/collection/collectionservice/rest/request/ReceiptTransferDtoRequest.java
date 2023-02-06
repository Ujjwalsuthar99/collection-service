package com.synoriq.synofin.collection.collectionservice.rest.request;

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
    List<Long> receipts;
//    String activityName;
//    Double distanceFromUserBranch;
//    Object address;
//    String activityRemarks;
//    Object activityImages;
//    Long loanId;
//    Object geoLocationData;

}
