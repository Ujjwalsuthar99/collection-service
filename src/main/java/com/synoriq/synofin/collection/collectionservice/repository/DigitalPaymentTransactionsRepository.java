package com.synoriq.synofin.collection.collectionservice.repository;

import com.synoriq.synofin.collection.collectionservice.entity.DigitalPaymentTransactionsEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface DigitalPaymentTransactionsRepository extends JpaRepository<DigitalPaymentTransactionsEntity, Long> {

    DigitalPaymentTransactionsEntity findByDigitalPaymentTransactionsId(Long digitalPaymentTransactionsId);
    @Query(nativeQuery = true, value="select\n" +
            "\tdpt.digital_payment_trans_id,\n" +
            "\tdpt.created_date,\n" +
            "\tdpt.modified_date,\n" +
            "\tdpt.loan_id,\n" +
            "\tdpt.payment_service_name,\n" +
            "\tdpt.status,\n" +
            "\tdpt.amount,\n" +
            "\tdpt.utr_number,\n" +
            "\tdpt.mobile_no,\n" +
            "\tdpt.vendor,\n" +
            "\tdpt.receipt_generated,\n" +
            "\tCOUNT(dpt.digital_payment_trans_id) over () as total_rows \n" +
            "from\n" +
            "\tcollection.digital_payment_transactions dpt\n" +
            "where\n" +
            "\tdpt.created_by = :createdBy")
    List<Map<String, Object>> getDigitalPaymentTransactionsByCreatedBy(Long createdBy, Pageable pageable);

}
