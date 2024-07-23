package com.synoriq.synofin.collection.collectionservice.rest.request.receiptTransferDTOs;

import com.synoriq.synofin.collection.collectionservice.rest.request.CollectionActivityLogDTO;
import lombok.Data;

import java.util.Date;

@Data
public class ReceiptTransferStatusUpdateDtoRequest {

    Long receiptTransferId;
    String status;
    String actionReason;
    String actionRemarks;
    Object images;
    Long actionBy;
    CollectionActivityLogDTO activityLog;

}