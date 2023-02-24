package com.synoriq.synofin.collection.collectionservice.service;

import com.synoriq.synofin.collection.collectionservice.repository.CollectionReceiptRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CollectionReceiptService {

    @Autowired
    CollectionReceiptRepository collectionReceiptRepository;


//    public String createCollectionReceiptMapping(CollectionReceiptDTO collectionReceiptDTO) throws Exception {
//
//        CollectionReceiptEntity collectionReceiptEntity = new CollectionReceiptEntity();
//
//            collectionReceiptEntity.setCreatedBy(collectionReceiptDTO.getCreated_by());
//            collectionReceiptEntity.setReceiptHolderUserId(collectionReceiptDTO.getReceipt_holder_user_id());
//            collectionReceiptEntity.setLastReceiptTransferId(collectionReceiptDTO.getLast_receipt_transfer_id());
//            collectionReceiptEntity.setCollectionActivityLogsId(collectionReceiptDTO.getCollection_activity_logs_id());
//
//            collectionReceiptRepository.save(collectionReceiptEntity);
//
//            return "Mapping Successfully Inserted";
//
//    }



}
