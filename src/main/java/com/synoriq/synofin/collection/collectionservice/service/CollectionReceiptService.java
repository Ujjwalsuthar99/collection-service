package com.synoriq.synofin.collection.collectionservice.service;

import com.synoriq.synofin.collection.collectionservice.entity.CollectionReceiptEntity;
import com.synoriq.synofin.collection.collectionservice.repository.CollectionReceiptRepository;
import com.synoriq.synofin.lms.commondto.dto.collection.CollectionReceiptDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Slf4j
@Service
public class CollectionReceiptService {

    @Autowired
    CollectionReceiptRepository collectionReceiptRepository;


    public String createCollectionReceiptMapping(CollectionReceiptDTO collectionReceiptDTO) throws Exception {

        CollectionReceiptEntity collectionReceiptEntity = new CollectionReceiptEntity();

            collectionReceiptEntity.setCreatedBy(collectionReceiptDTO.getCreated_by());
            collectionReceiptEntity.setReceiptHolderUserId(collectionReceiptDTO.getReceipt_holder_user_id());
            collectionReceiptEntity.setLastReceiptTransferId(collectionReceiptDTO.getLast_receipt_transfer_id());
            collectionReceiptEntity.setCollectionActivityLogsId(collectionReceiptDTO.getCollection_activity_logs_id());

            collectionReceiptRepository.save(collectionReceiptEntity);

            return "Mapping Successfully Inserted";

    }



}
