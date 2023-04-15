package com.synoriq.synofin.collection.collectionservice.repository;

import com.synoriq.synofin.collection.collectionservice.entity.LoanAllocationEntity;
import com.synoriq.synofin.collection.collectionservice.entity.ReceiptTransferEntity;
import com.synoriq.synofin.collection.collectionservice.entity.ReceiptTransferHistoryEntity;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReceiptTransferHistoryRepository extends JpaRepository<ReceiptTransferHistoryEntity, Long> {

    List<ReceiptTransferHistoryEntity> getReceiptTransferHistoryDataByReceiptTransferId(Long receiptTransferId);

    @Query(nativeQuery = true,value = "select * from collection.receipt_transfer_history rth where rth.receipt_transfer_id <> :receiptTransferId and rth.collection_receipts_id = :receiptId and deleted is false")
    List<ReceiptTransferHistoryEntity> buttonRestriction(@Param("receiptTransferId") Long receiptTransferId, @Param("receiptId") Long receiptId);
}
