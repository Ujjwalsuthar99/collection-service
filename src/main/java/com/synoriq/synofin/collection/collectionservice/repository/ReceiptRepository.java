package com.synoriq.synofin.collection.collectionservice.repository;

import com.synoriq.synofin.collection.collectionservice.entity.FollowUpEntity;
import org.springframework.data.domain.Pageable;
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
            "                date(sr.form->>'date_of_receipt') as date_of_receipt,\n" +
            "                sr.created_date as created_date,\n" +
            "                clm.loan_id,\n" +
            "                la.loan_application_number,\n" +
            "                concat_ws(' ', c.first_name, c.last_name) as customer_name,\n" +
            "                c.address1_json->>'address' as address,\n" +
            "                cast(sr.form->>'receipt_amount' as decimal) as receipt_amount,\n" +
            "                sr.status as status,\n" +
            "                (case \n" +
            "                    when sr.status = 'approved' then '#229A16'\n" +
            "                    when sr.status = 'rejected' then '#EC1C24'\n" +
            "                    when sr.status = 'initiated' then '#2F80ED'\n" +
            "                    else '#B78103'\n" +
            "                end) as status_text_color_key,\n" +
            "                (case \n" +
            "                    when sr.status = 'approved' then '#E3F8DD'\n" +
            "                    when sr.status = 'rejected' then '#FFCECC'\n" +
            "                    when sr.status = 'initiated' then '#D0E1F7'\n" +
            "                    else '#FCEBDB'\n" +
            "                end) as status_bg_color_key\n" +
            "                from lms.service_request sr \n" +
            "                join collection.collection_receipts cr on cr.receipt_id = sr.service_request_id\n" +
            "    join (select loan_application_number, loan_application_id from lms.loan_application) as la on la.loan_application_id = sr.loan_id \n" +
            "    join lms.customer_loan_mapping clm on clm.loan_id = sr.loan_id \n" +
            "    join lms.customer c on clm.customer_id = c.customer_id \n" +
            "    where clm.customer_type = 'applicant' and\n" +
            "    sr.request_source = 'm_collect' and sr.form->>'created_by' = :userName\n" +
            "    and date(sr.form->>'date_of_receipt') between to_date(:fromDate, 'DD-MM-YYYY') and to_date(:toDate, 'DD-MM-YYYY')")
    List<Map<String, Object>> getReceiptsByUserIdWithDuration(@Param("userName") String userName, @Param("fromDate") String fromDate
            , @Param("toDate") String toDate, Pageable pageRequest);


    @Query(nativeQuery = true, value = "select \n" +
            "                sr.service_request_id ,\n" +
            "                sr.created_date ,\n" +
            "                clm.loan_id,\n" +
            "                la.loan_application_number,\n" +
            "                concat_ws(' ', c.first_name, c.last_name) as customer_name,\n" +
            "                c.address1_json->>'address' as address,\n" +
            "                cast(sr.form->>'receipt_amount' as decimal) as receipt_amount,\n" +
            "                sr.status as status,\n" +
            "                (case \n" +
            "                    when sr.status = 'approved' then '#229A16'\n" +
            "                    when sr.status = 'rejected' then '#EC1C24'\n" +
            "                    when sr.status = 'initiated' then '#2F80ED'\n" +
            "                    else '#B78103'\n" +
            "                end) as status_text_color_key,\n" +
            "                (case \n" +
            "                    when sr.status = 'approved' then '#E3F8DD'\n" +
            "                    when sr.status = 'rejected' then '#FFCECC'\n" +
            "                    when sr.status = 'initiated' then '#D0E1F7'\n" +
            "                    else '#FCEBDB'\n" +
            "                end) as status_bg_color_key\n" +
            "                from lms.service_request sr \n" +
            "                join collection.collection_receipts cr on cr.receipt_id = sr.service_request_id\n" +
            "    join (select loan_application_number, loan_application_id from lms.loan_application) as la on la.loan_application_id = sr.loan_id \n" +
            "    join lms.customer_loan_mapping clm on clm.loan_id = sr.loan_id \n" +
            "    join lms.customer c on clm.customer_id = c.customer_id \n" +
            "    where clm.customer_type = 'applicant' \n" +
            "    and sr.request_source = 'm_collect' and sr.loan_id = :loanId\n" +
            "    and date(sr.form->>'date_of_receipt') between to_date(:fromDate, 'DD-MM-YYYY') and to_date(:toDate, 'DD-MM-YYYY')")
    List<Map<String, Object>> getReceiptsByLoanIdWithDuration(@Param("loanId") Long loanId, @Param("fromDate") String fromDate
            , @Param("toDate") String toDate);



    @Query(nativeQuery = true, value = "select \n" +
            "    sr.service_request_id as id ,\n" +
            "    concat_ws(' ', c.first_name, c.last_name) as customer_name,\n" +
            "    (case when cast(sr.form->>'receipt_amount' as decimal) is null then 0 else cast(sr.form->>'receipt_amount' as decimal) end) as receipt_amount,\n" +
            "    sr.form->>'payment_mode' as payment_mode\n" +
            "    from lms.service_request sr \n" +
            "    join collection.collection_receipts cr on cr.receipt_id = sr.service_request_id \n" +
            "    join (select loan_application_number, loan_application_id from lms.loan_application) as la on la.loan_application_id = sr.loan_id\n" +
            "    join (select loan_id, customer_id, customer_type from lms.customer_loan_mapping) as clm on clm.loan_id = sr.loan_id\n" +
            "    join (select customer_id, first_name, last_name  from lms.customer) as c on clm.customer_id = c.customer_id\n" +
            "    where cr.receipt_id not in (select collection_receipts_id from collection.receipt_transfer_history where collection.receipt_transfer_history.deleted=false) and clm.customer_type = 'applicant' and\n" +
            "    sr.request_source = 'm_collect' and sr.status = 'initiated' and (sr.form->>'payment_mode' = 'cash' or sr.form->>'payment_mode' = 'cheque') and sr.form->>'created_by' = :userName")
    List<Map<String, Object>> getReceiptsByUserIdWhichNotTransferred(@Param("userName") String userName);


    @Query(nativeQuery = true, value = "SELECT case when sum(cast(sr.form->>'receipt_amount' as decimal)) is null then 0.0 else sum(cast(sr.form->>'receipt_amount' as decimal)) end\n" +
            "            from lms.service_request sr where sr.request_source = 'm_collect' and sr.loan_id = cast(:loanId as bigint) and sr.form->>'payment_mode' = 'cash'\n" +
            "            and date(sr.created_date) = date(current_date)")
    double getCollectedAmountToday(@Param("loanId") Long loanId);


    @Query(nativeQuery = true, value = "select cast(sr.service_request_id as text) as service_request_id from lms.service_request sr join collection.collection_receipts cr on sr.service_request_id = cr.receipt_id where sr.service_request_id = cast(:serviceRequestId as bigint) and sr.request_source = 'm_collect' and sr.is_deleted = false")
    String getServiceRequestId(@Param("serviceRequestId") Long serviceRequestId);

    @Query(nativeQuery = true, value = "select sr.service_request_id as id, sr.created_date as created_date from lms.service_request sr join collection.collection_receipts cr on sr.service_request_id = cr.receipt_id where sr.request_source = 'm_collect' and cast(sr.form->>'receipt_amount' as text) = :receiptAmount and sr.loan_id = :loanId and sr.is_deleted = false order by sr.created_date desc limit 1")
    Map<String, Object> getReceiptData(@Param("loanId") String loanId, @Param("receiptAmount") String receiptAmount);

    @Query(nativeQuery = true, value = "select sr.service_request_id as serviceRequestId from lms.service_request where sr.form->>'transaction_reference' = :transactionReferenceNumber and sr.request_source = 'm_collect' and sr.is_deleted = false")
    Map<String, Object> transactionNumberCheck(@Param("transactionReferenceNumber") String transactionReferenceNumber);


    @Query(nativeQuery = true, value = "select\n" +
            "\tb.branch_name as branch_name,\n" +
            "\tsr.created_date as created_date,\n" +
            "\tsr.service_request_id as receipt_no,\n" +
            "\tsr.form->>'received_from' as collected_from,\n" +
            "\tsr.form->>'payment_mode' as payment_mode,\n" +
            "\tconcat_ws(' ', c.first_name, c.last_name) as customer_name,\n" +
            "\tcast(c.phone1_json->>'mobile' as text) as mobile_number,\n" +
            "\tsr.form->>'ifsc' as ifsc,\n" +
            "\tsr.form->>'instrument_number' as cheque_no,\n" +
            "\tsr.form->>'instrument_bank_name' as bank_name,\n" +
            "\tsr.form->>'bank_account_number' as bank_account_number,\n" +
            "\tsr.form->>'transaction_reference' as transaction_reference, \n" +
            "\tla.loan_application_number as loan_number,\n" +
            "\tsr.form->>'created_by' as user_code, \n" +
            "\tu.\"name\" as user_name,\n" +
            "\tla.installment_amount as actual_emi,\n" +
            "\tla.sanctioned_amount as loan_amount,\n" +
            "\tsr.form->>'receipt_amount' as receipt_amount,\n" +
            "\tsr.form->>'receipt_amount' as total\n" +
            "from\n" +
            "\tlms.service_request sr\n" +
            "left join lms.loan_application la on\n" +
            "\tla.loan_application_id = sr.loan_id\n" +
            "left join master.branch b on\n" +
            "\tb.branch_id = la.branch_id\n" +
            "left join lms.customer_loan_mapping clm on\n" +
            "\tclm.loan_id = la.loan_application_id\n" +
            "\tand clm.customer_type = 'applicant'\n" +
            "left join lms.customer c on\n" +
            "\tclm.customer_id = c.customer_id \n" +
            "left join master.users u on \n" +
            "\tu.username = sr.form->>'created_by'\n" +
            "where\n" +
            "\tsr.service_request_id = :receiptId")
    Map<String, Object> getServiceRequestData(@Param("receiptId") Long receiptId);
}