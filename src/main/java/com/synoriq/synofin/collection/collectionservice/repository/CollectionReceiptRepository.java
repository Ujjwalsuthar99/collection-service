package com.synoriq.synofin.collection.collectionservice.repository;

import com.synoriq.synofin.collection.collectionservice.entity.CollectionReceiptEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public interface CollectionReceiptRepository extends JpaRepository<CollectionReceiptEntity, Long> {

    CollectionReceiptEntity findByReceiptId(Long receiptId);

    @Query(nativeQuery = true,value = "select * from collection.collection_receipts cr where cr.receipt_id = cast(:receiptId as bigint)")
    Map<String, Object> findDataByReceiptId(Long receiptId);
}
