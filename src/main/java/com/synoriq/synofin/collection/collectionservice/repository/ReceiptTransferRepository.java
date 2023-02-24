package com.synoriq.synofin.collection.collectionservice.repository;

import com.synoriq.synofin.collection.collectionservice.entity.ReceiptTransferEntity;
import com.synoriq.synofin.collection.collectionservice.entity.RegisteredDeviceInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Repository
public interface ReceiptTransferRepository extends JpaRepository<ReceiptTransferEntity, Long> {

    List<ReceiptTransferEntity> getReceiptTransferSummaryByTransferredBy(Long transferredBy);


    @Query(nativeQuery = true,value = "select * from collection.receipt_transfer where transferred_by = :transferredBy " +
            "and created_date between :fromDate and :toDate and status = :status")
    List<ReceiptTransferEntity> getReceiptTransferByUserId(@Param("transferredBy") Long transferredBy, @Param("fromDate") Date fromDate
            , @Param("toDate") Date toDate, @Param("status") String status);

    @Query(nativeQuery = true,value = "select * from collection.receipt_transfer \n" +
            "where transferred_by = :transferredBy and created_date between :fromDate and :toDate\n" +
            "order by \n" +
            "    case when status = 'pending' then 1 \n" +
            "         when status = 'approved' then 2 \n" +
            "         else 3 end")
    List<ReceiptTransferEntity> getReceiptTransferByUserIdWithAllStatus(@Param("transferredBy") Long transferredBy, @Param("fromDate") Date fromDate
            , @Param("toDate") Date toDate);


    @Query(nativeQuery = true,value = "select concat_ws(' ', c.first_name, c.last_name) as name, sr.form->>'receipt_amount' as receipt_amount, sr.service_request_id as receipt_id\n" +
            "         from collection.receipt_transfer rt \n" +
            "         join (select collection_receipts_id, receipt_transfer_id from collection.receipt_transfer_history) as rth on rt.receipt_transfer_id  = rth.receipt_transfer_id \n" +
            "         join (select service_request_id, loan_id, form from lms.service_request) as sr on sr.service_request_id = rth.collection_receipts_id \n" +
            "         join (select loan_application_id from lms.loan_application) as la on la.loan_application_id = sr.loan_id \n" +
            "         join (select loan_id, customer_id from lms.customer_loan_mapping) as clm on clm.loan_id  = la.loan_application_id \n" +
            "         join (select customer_id, first_name, last_name from lms.customer) as c on c.customer_id = clm.customer_id \n" +
            "         where rt.receipt_transfer_id = :receiptTransferId")
    List<Map<String, Object>> getDataByReceiptTransferId(@Param("receiptTransferId") Long receiptTransferId);
}
