package com.synoriq.synofin.collection.collectionservice.repository;

import com.synoriq.synofin.collection.collectionservice.entity.ReceiptTransferEntity;
import com.synoriq.synofin.collection.collectionservice.entity.RegisteredDeviceInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface ReceiptTransferRepository extends JpaRepository<ReceiptTransferEntity, Long> {

    List<ReceiptTransferEntity> getReceiptTransferSummaryByTransferredBy(Long transferredBy);


    @Query(nativeQuery = true,value = "select * from collection.receipt_transfer where transferred_by = :transferredBy " +
            "and created_date between :fromDate and :toDate and status = :status")
    List<ReceiptTransferEntity> getReceiptTransferByUserId(@Param("transferredBy") Long transferredBy, @Param("fromDate") Date fromDate
            , @Param("toDate") Date toDate, @Param("status") String status);

}
