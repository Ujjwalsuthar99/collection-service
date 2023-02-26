package com.synoriq.synofin.collection.collectionservice.repository;

import com.synoriq.synofin.collection.collectionservice.entity.LoanAllocationEntity;
import com.synoriq.synofin.collection.collectionservice.entity.ReceiptTransferEntity;
import com.synoriq.synofin.collection.collectionservice.entity.ReceiptTransferHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReceiptTransferHistoryRepository extends JpaRepository<ReceiptTransferHistoryEntity, Long> {

    List<ReceiptTransferHistoryEntity> getReceiptTransferHistoryDataByReceiptTransferId(Long receiptTransferId);

}
