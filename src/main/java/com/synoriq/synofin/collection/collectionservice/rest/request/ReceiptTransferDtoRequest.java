package com.synoriq.synofin.collection.collectionservice.rest.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.synoriq.synofin.collection.collectionservice.rest.request.CollectionActivityLogDTO;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReceiptTransferDtoRequest {

//    Long transferredBy;
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
    Long receiptTransferId;
    CollectionActivityLogDTO activityData;

}
