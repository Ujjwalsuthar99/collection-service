package com.synoriq.synofin.collection.collectionservice.repository;

import com.synoriq.synofin.collection.collectionservice.entity.FollowUpEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Repository
public interface ReceiptRepository extends JpaRepository<FollowUpEntity, Long> {

    @Query(nativeQuery = true, value = "select \n" +
            "                sr.service_request_id ,\n" +
            "                clm.loan_id,\n" +
            "                la.loan_application_number,\n" +
            "                concat_ws(' ', c.first_name, c.last_name) as customer_name,\n" +
            "                c.address1_json->>'address' as address,\n" +
            "                cast(sr.form->>'receipt_amount' as decimal) as receipt_amount,\n" +
            "                sr.status as status,\n" +
            "                (case \n" +
            "                    when sr.status = 'approved' then 'green'\n" +
            "                    when sr.status = 'rejected' then 'red'\n" +
            "                    when sr.status = 'initiated' then 'yellow'\n" +
            "                    else 'black'\n" +
            "                end) as status_color_key\n" +
            "                from lms.service_request sr \n" +
            "                join collection.collection_receipts cr on cr.receipt_id = sr.service_request_id\n" +
            "    join (select loan_application_number, loan_application_id from lms.loan_application) as la on la.loan_application_id = sr.loan_id \n" +
            "    join lms.customer_loan_mapping clm on clm.loan_id = sr.loan_id \n" +
            "    join lms.customer c on clm.customer_id = c.customer_id \n" +
            "    where clm.customer_type = 'applicant' and\n" +
            "    sr.request_source = 'm_collect' and sr.form->>'created_by' = :userId\n" +
            "    and date(sr.form->>'transaction_date') between to_date(:fromDate, 'DD-MM-YYYY') and to_date(:toDate, 'DD-MM-YYYY')")
    List<Map<String, Object>> getReceiptsByUserIdWithDuration(@Param("userId") String userId, @Param("fromDate") String fromDate
            , @Param("toDate") String toDate);


    @Query(nativeQuery = true, value = "select \n" +
            "                sr.service_request_id ,\n" +
            "                clm.loan_id,\n" +
            "                la.loan_application_number,\n" +
            "                concat_ws(' ', c.first_name, c.last_name) as customer_name,\n" +
            "                c.address1_json->>'address' as address,\n" +
            "                cast(sr.form->>'receipt_amount' as decimal) as receipt_amount,\n" +
            "                sr.status as status,\n" +
            "                (case \n" +
            "                    when sr.status = 'approved' then 'green'\n" +
            "                    when sr.status = 'rejected' then 'red'\n" +
            "                    when sr.status = 'initiated' then 'yellow'\n" +
            "                    else 'black'\n" +
            "                end) as status_color_key\n" +
            "                from lms.service_request sr \n" +
            "                join collection.collection_receipts cr on cr.receipt_id = sr.service_request_id\n" +
            "    join (select loan_application_number, loan_application_id from lms.loan_application) as la on la.loan_application_id = sr.loan_id \n" +
            "    join lms.customer_loan_mapping clm on clm.loan_id = sr.loan_id \n" +
            "    join lms.customer c on clm.customer_id = c.customer_id \n" +
            "    where clm.customer_type = 'applicant' \n" +
            "    and sr.request_source = 'm_collect' and sr.loan_id = :loanId\n" +
            "    and date(sr.form->>'transaction_date') between to_date(:fromDate, 'DD-MM-YYYY') and to_date(:toDate, 'DD-MM-YYYY')")
    List<Map<String, Object>> getReceiptsByLoanIdWithDuration(@Param("loanId") Long loanId, @Param("fromDate") String fromDate
            , @Param("toDate") String toDate);
}