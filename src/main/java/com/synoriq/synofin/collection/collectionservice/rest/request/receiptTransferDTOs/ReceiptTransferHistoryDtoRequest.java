package com.synoriq.synofin.collection.collectionservice.rest.request.receiptTransferDTOs;

import lombok.Data;

import java.util.Date;

@Data
public class ReceiptTransferHistoryDtoRequest {


    Long createdBy;
    Long transferredBy;

}