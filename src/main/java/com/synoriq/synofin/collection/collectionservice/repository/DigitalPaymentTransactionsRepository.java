package com.synoriq.synofin.collection.collectionservice.repository;

import com.synoriq.synofin.collection.collectionservice.entity.DigitalPaymentTransactionsEntity;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Repository
public interface DigitalPaymentTransactionsRepository extends JpaRepository<DigitalPaymentTransactionsEntity, Long> {

    DigitalPaymentTransactionsEntity findByDigitalPaymentTransactionsId(Long digitalPaymentTransactionsId);

    DigitalPaymentTransactionsEntity findByMerchantTranId(String merchantTranId);

    DigitalPaymentTransactionsEntity findFirstByLoanIdAndAmount(Long loanId, Float amount);

    @Query(nativeQuery = true, value="select\n" +
            "\tdpt.merchant_tran_id as merchantTransId,\n" +
            "\tdpt.status,\n" +
            "\tdpt.amount,\n" +
            "\tdpt.vendor\n" +
            "from\n" +
            "\tcollection.digital_payment_transactions dpt\n" +
            "where\n" +
            "\tdpt.digital_payment_trans_id = :digitalPaymentTransactionsId and dpt.merchant_tran_id = :merchantTransId")
    Map<String, Object> findByDigitalPaymentTransactionsIdForCheckStatusResponse(Long digitalPaymentTransactionsId, String merchantTransId);
    @Query(nativeQuery = true, value="select\n" +
            "\tdpt.digital_payment_trans_id,\n" +
            "\tdpt.created_date,\n" +
            "\tdpt.modified_date,\n" +
            "\tdpt.loan_id,\n" +
            "\t(select la.loan_application_number from lms.loan_application la where la.loan_application_id=dpt.loan_id) as loan_number,\n" +
            "\t(select concat(c.first_name, ' ', c.last_name) from lms.customer_loan_mapping clm join lms.customer c on clm.customer_id = c.customer_id  where clm.loan_id = dpt.loan_id and clm.customer_type ='applicant') as customer_name,\n" +
            "\tdpt.payment_service_name,\n" +
            "\tcase when dpt.status = 'expired' and dpt.payment_service_name = 'dynamic_qr_code' then 'qr_expired'\n" +
            "\t\t else dpt.status end as status,\n" +
            "\tdpt.merchant_tran_id,\n" +
            "\tdpt.amount,\n" +
            "\tdpt.utr_number,\n" +
            "\tdpt.mobile_no,\n" +
            "\tdpt.vendor,\n" +
            "\tdpt.receipt_generated,\n" +
            "\tCOUNT(dpt.digital_payment_trans_id) over () as total_rows \n" +
            "from\n" +
            "\tcollection.digital_payment_transactions dpt\n" +
            "where\n" +
            "\tdpt.created_by = :createdBy and dpt.created_date between :fromDate and :toDate order by dpt.created_by desc")
    List<Map<String, Object>> getDigitalPaymentTransactionsByCreatedBy(@Param("createdBy") Long createdBy, Pageable pageable, @Param("fromDate") Date fromDate, @Param("toDate") Date toDate);
    @Query(nativeQuery = true, value = "select * from collection.digital_payment_transactions dpt where dpt.utr_number = :utrNumber limit 1")
    DigitalPaymentTransactionsEntity checkUtrNumberValidation(@Param("utrNumber") String utrNumber);

}
