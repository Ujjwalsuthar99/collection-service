package com.synoriq.synofin.collection.collectionservice.rest.request.receipttransferdtos;

import com.synoriq.synofin.collection.collectionservice.rest.request.CollectionActivityLogDTO;
import lombok.Data;

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
