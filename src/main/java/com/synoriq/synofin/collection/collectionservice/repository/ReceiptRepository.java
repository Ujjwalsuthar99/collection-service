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

    @Query(nativeQuery = true, value = "select\n" +
            "\tsr.service_request_id ,\n" +
            "\tdate(sr.form->>'date_of_receipt') as date_of_receipt,\n" +
            "\tsr.created_date as created_date,\n" +
            "\tclm.loan_id,\n" +
            "\tla.loan_application_number,\n" +
//            "\tconcat_ws(' ', c.first_name, c.last_name) as customer_name,\n" +
            " concat(lms.decrypt_data(c.first_name, :encryptionKey, :password, :piiPermission), ' ', lms.decrypt_data(c.last_name, :encryptionKey, :password, :piiPermission)) as customer_name,\n" +
            "\tc.address1_json->>'address' as address,\n" +
            "\tcast(sr.form->>'receipt_amount' as decimal) as receipt_amount,\n" +
            "\tsr.status as status,\n" +
            "\tsr.form->>'payment_mode' as payment_mode,\n" +
            "\t(case\n" +
            "\t\twhen sr.status = 'approved' then '#229A16'\n" +
            "\t\twhen sr.status = 'rejected' then '#EC1C24'\n" +
            "\t\twhen sr.status = 'initiated' then '#2F80ED'\n" +
            "\t\telse '#B78103'\n" +
            "\tend) as status_text_color_key,\n" +
            "\t(case\n" +
            "\t\twhen sr.status = 'approved' then '#E3F8DD'\n" +
            "\t\twhen sr.status = 'rejected' then '#FFCECC'\n" +
            "\t\twhen sr.status = 'initiated' then '#D0E1F7'\n" +
            "\t\telse '#FCEBDB'\n" +
            "\tend) as status_bg_color_key\n" +
            "from\n" +
            "\tlms.service_request sr\n" +
            "join collection.collection_receipts cr on\n" +
            "\tcr.receipt_id = sr.service_request_id\n" +
            "join (\n" +
            "\tselect\n" +
            "\t\tloan_application_number,\n" +
            "\t\tloan_application_id\n" +
            "\tfrom\n" +
            "\t\tlms.loan_application) as la on\n" +
            "\tla.loan_application_id = sr.loan_id\n" +
            "join lms.customer_loan_mapping clm on\n" +
            "\tclm.loan_id = sr.loan_id\n" +
            "\tand clm.customer_type = 'applicant'\n" +
            "join lms.customer c on\n" +
            "\tclm.customer_id = c.customer_id\n" +
            "where\n" +
            "\tsr.request_source = 'm_collect'\n" +
            "\tand sr.created_by = (\n" +
            "\tselect\n" +
            "\t\tuser_id\n" +
            "\tfrom\n" +
            "\t\tmaster.users\n" +
            "\twhere\n" +
            "\t\tusername = :userName)\n" +
            "\tand date(sr.form->>'date_of_receipt') between to_date(:fromDate, 'DD-MM-YYYY') and to_date(:toDate, 'DD-MM-YYYY')")
    List<Map<String, Object>> getReceiptsByUserIdWithDuration(@Param("userName") String userName, @Param("fromDate") String fromDate
            , @Param("toDate") String toDate, @Param("encryptionKey") String encryptionKey, @Param("password") String password, @Param("piiPermission") Boolean piiPermission, Pageable pageRequest);


    @Query(nativeQuery = true, value = "select \n" +
            "                sr.service_request_id ,\n" +
            "                sr.created_date ,\n" +
            "                clm.loan_id,\n" +
            "                la.loan_application_number,\n" +
            "                concat_ws(' ', c.first_name, c.last_name) as customer_name,\n" +
            "                c.address1_json->>'address' as address,\n" +
            "                cast(sr.form->>'receipt_amount' as decimal) as receipt_amount,\n" +
            "                sr.status as status,\n" +
            "                sr.form->>'payment_mode' as payment_mode,\n" +
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
//            "    concat_ws(' ', c.first_name, c.last_name) as customer_name,\n" +
            "    concat(lms.decrypt_data(c.first_name, :encryptionKey, :password, :piiPermission), ' ', lms.decrypt_data(c.last_name, :encryptionKey, :password, :piiPermission)) as customer_name,\n" +
            "    (case when cast(sr.form->>'receipt_amount' as decimal) is null then 0 else cast(sr.form->>'receipt_amount' as decimal) end) as receipt_amount,\n" +
            "    sr.form->>'payment_mode' as payment_mode\n" +
            "    from lms.service_request sr \n" +
            "    join collection.collection_receipts cr on cr.receipt_id = sr.service_request_id \n" +
            "    join (select loan_application_number, loan_application_id from lms.loan_application) as la on la.loan_application_id = sr.loan_id\n" +
            "    join (select loan_id, customer_id, customer_type from lms.customer_loan_mapping) as clm on clm.loan_id = sr.loan_id and clm.customer_type = 'applicant' \n" +
            "    join (select customer_id, first_name, last_name  from lms.customer) as c on clm.customer_id = c.customer_id\n" +
            "    where sr.request_source = 'm_collect' and sr.status = 'initiated' and (sr.form->>'payment_mode' = 'cash' or sr.form->>'payment_mode' = 'cheque') \n" +
            "    and (cr.receipt_holder_user_id = (select user_id from master.users where username = :userName)\n" +
            "    and cr.receipt_id not in (select rth.collection_receipts_id from collection.receipt_transfer_history rth join collection.receipt_transfer rt on rth.receipt_transfer_id = rt.receipt_transfer_id where rt.status = 'pending'))")
    List<Map<String, Object>> getReceiptsByUserIdWhichNotTransferred(@Param("userName") String userName, @Param("encryptionKey") String encryptionKey, @Param("password") String password, @Param("piiPermission") Boolean piiPermission);


    @Query(nativeQuery = true, value = "SELECT case when sum(cast(sr.form->>'receipt_amount' as decimal)) is null then 0.0 else sum(cast(sr.form->>'receipt_amount' as decimal)) end\n" +
            "            from lms.service_request sr where sr.request_source = 'm_collect' and sr.loan_id = cast(:loanId as bigint) and sr.form->>'payment_mode' = 'cash'\n" +
            "            and date(sr.created_date) = date(current_date)")
    double getCollectedAmountToday(@Param("loanId") Long loanId);

    @Query(nativeQuery = true, value = "SELECT case when sum(cast(sr.form->>'receipt_amount' as decimal)) is null then 0.0 else sum(cast(sr.form->>'receipt_amount' as decimal)) end\n" +
            "            from lms.service_request sr where sr.request_source = 'm_collect' and sr.loan_id = cast(:loanId as bigint) and sr.form->>'payment_mode' = 'cash'\n" +
            "            and date(sr.form->>'date_of_receipt') between to_date(:fromDate, 'DD-MM-YYYY') and to_date(:toDate, 'DD-MM-YYYY')")
    double getCollectedAmountWithinMonth(@Param("loanId") Long loanId, @Param("fromDate") String fromDate, @Param("toDate") String toDate);


    @Query(nativeQuery = true, value = "select sr.loan_id as loanId, sr.status as status, sr.form->>'receipt_amount' as receiptAmount from lms.service_request sr join collection.collection_receipts cr on sr.service_request_id = cr.receipt_id where sr.service_request_id = cast(:serviceRequestId as bigint) and sr.request_source = 'm_collect' and sr.is_deleted = false")
    Map<String, Object> getLoanIdByServiceId(@Param("serviceRequestId") Long serviceRequestId);

    @Query(nativeQuery = true, value = "select sr.service_request_id as id, sr.created_date as created_date from lms.service_request sr join collection.collection_receipts cr on sr.service_request_id = cr.receipt_id where sr.request_source = 'm_collect' and sr.form->>'receipt_amount' = :receiptAmount and sr.loan_id = cast(:loanId as bigint) and sr.is_deleted = false order by sr.created_date desc limit 1")
    Map<String, Object> getReceiptData(@Param("loanId") Long loanId, @Param("receiptAmount") String receiptAmount);

    @Query(nativeQuery = true, value = "select sr.service_request_id as serviceRequestId from lms.service_request sr join collection.collection_receipts cr on sr.service_request_id = cr.receipt_id where sr.form->>'transaction_reference' = :transactionReferenceNumber and sr.request_source = 'm_collect' and sr.form->>'payment_mode' = 'upi' and sr.status in ('approved', 'initiated') and sr.is_deleted = false")
    Map<String, Object> transactionNumberCheck(@Param("transactionReferenceNumber") String transactionReferenceNumber);


    @Query(nativeQuery = true, value = "select\n" +
            "\tsr.service_request_id\n" +
            "from\n" +
            "\tlms.service_request sr\n" +
            "join collection.collection_receipts cr on\n" +
            "\tcr.receipt_id = sr.service_request_id\n" +
            "where\n" +
            "\t(cr.receipt_holder_user_id = :userId\n" +
            "\t\tand sr.form->>'payment_mode' = 'cash'\n" +
            "\t\tand sr.status = 'initiated')\n" +
            "\tor (cr.receipt_id in (\n" +
            "\tselect\n" +
            "\t\trth.collection_receipts_id\n" +
            "\tfrom\n" +
            "\t\tcollection.receipt_transfer_history rth\n" +
            "\tjoin collection.receipt_transfer rt on\n" +
            "\t\trth.receipt_transfer_id = rt.receipt_transfer_id\n" +
            "\twhere\n" +
            "\t\trt.status = 'pending') and cr.receipt_holder_user_id = :userId and sr.form->>'payment_mode' = 'cash')\n" +
            "\tand (extract('epoch'\n" +
            "from\n" +
            "\t(now() - sr.created_date))/ 3600) > cast(:depositReminderHours as numeric)")
    List<Map<String, Object>> depositReminderData(@Param("userId") Long userId, @Param("depositReminderHours") String depositReminderHours);

    @Query(nativeQuery = true, value = "select\n" +
            "\tsr.service_request_id,\n" +
            "\tsr.created_date\n" +
            "from\n" +
            "\tlms.service_request sr\n" +
            "join collection.collection_receipts cr on\n" +
            "\tcr.receipt_id = sr.service_request_id\n" +
            "where\n" +
            "\t(sr.form->>'payment_mode' = 'cash'\n" +
            "\t\tand sr.status = 'initiated'\n" +
            "\t\tand cr.receipt_holder_user_id = :userId)\n" +
            "\tor (cr.receipt_id in (\n" +
            "\tselect\n" +
            "\t\trth.collection_receipts_id\n" +
            "\tfrom\n" +
            "\t\tcollection.receipt_transfer_history rth\n" +
            "\tjoin collection.receipt_transfer rt on\n" +
            "\t\trth.receipt_transfer_id = rt.receipt_transfer_id\n" +
            "\twhere\n" +
            "\t\trt.status = 'pending')\n" +
            "\tand cr.receipt_holder_user_id = :userId\n" +
            "\tand sr.form->>'payment_mode' = 'cash')\n" +
            "order by\n" +
            "\tsr.created_date asc\n" +
            "limit 1")
    Map<String, Object> depositReminderDataByDayTime(@Param("userId") Long userId);

    @Query(nativeQuery = true, value = "select cast(cal.images as text) as images, cast(cal.geo_location_data as text) as geo_location_data, sr.form->>'created_by' as created_by,\n" +
            " (select u.\"name\" from master.users u where u.username = sr.form->>'created_by') as full_name from collection.collection_receipts cr\n" +
            " join collection.collection_activity_logs cal on cal.collection_activity_logs_id = cr.collection_activity_logs_id\n" +
            " join lms.service_request sr on sr.service_request_id = cr.receipt_id where cr.receipt_id = :receiptId")
    Map<String, Object> getReceiptDataByReceiptId(@Param("receiptId") Long receiptId);

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

    @Query(nativeQuery = true, value = "select \n" +
            "sr.service_request_id ,\n" +
            "date(sr.form->>'date_of_receipt') as date_of_receipt,\n" +
            "sr.created_date as created_date,\n" +
            "clm.loan_id,\n" +
            "la.loan_application_number,\n" +
//            "concat_ws(' ', c.first_name, c.last_name) as customer_name,\n" +
            " concat(lms.decrypt_data(c.first_name, :encryptionKey, :password, :piiPermission), ' ', lms.decrypt_data(c.last_name, :encryptionKey, :password, :piiPermission)) as customer_name,\n" +
            "c.address1_json->>'address' as address,\n" +
            "cast(sr.form->>'receipt_amount' as decimal) as receipt_amount,\n" +
            "sr.status as status,\n" +
            "sr.form->>'payment_mode' as payment_mode,\n" +
            "(case \n" +
            "    when sr.status = 'approved' then '#229A16'\n" +
            "    when sr.status = 'rejected' then '#EC1C24'\n" +
            "    when sr.status = 'initiated' then '#2F80ED'\n" +
            "    else '#B78103'\n" +
            "end) as status_text_color_key,\n" +
            "(case \n" +
            "    when sr.status = 'approved' then '#E3F8DD'\n" +
            "    when sr.status = 'rejected' then '#FFCECC'\n" +
            "    when sr.status = 'initiated' then '#D0E1F7'\n" +
            "    else '#FCEBDB'\n" +
            "end) as status_bg_color_key\n" +
            "from lms.service_request sr \n" +
            "join collection.collection_receipts cr on cr.receipt_id = sr.service_request_id\n" +
            "join (select loan_application_number, loan_application_id from lms.loan_application) as la on la.loan_application_id = sr.loan_id \n" +
            "join lms.customer_loan_mapping clm on clm.loan_id = sr.loan_id \n" +
            "join lms.customer c on clm.customer_id = c.customer_id \n" +
            "where clm.customer_type = 'applicant' and sr.request_source = 'm_collect' \n" +
            "and sr.created_by = (select user_id from master.users where username = :userName)\n" +
            "and (\n" +
            "\tLOWER(la.loan_application_number) like LOWER(concat('%', :searchKey, '%'))\n" +
            "\tor LOWER(cast(sr.status as text)) like LOWER(concat('%', :searchKey, '%'))\n" +
            "\tor LOWER(sr.form->>'payment_mode') like LOWER(concat('%',:searchKey, '%'))\n" +
            "\tor LOWER(cast(sr.service_request_id as text)) like LOWER(concat('%', :searchKey, '%'))\n" +
            ")")
    List<Map<String, Object>> getReceiptsBySearchKey(@Param("userName") String userName, @Param("searchKey") String searchKey, @Param("encryptionKey") String encryptionKey, @Param("password") String password, @Param("piiPermission") Boolean piiPermission, Pageable pageRequest);

    @Query(nativeQuery = true, value = "select\n" +
            "\tcast(sr.form->>'payment_mode' as text) as payment_mode\n" +
            "from\n" +
            "\tlms.service_request sr\n" +
            "where\n" +
            "\tsr.service_request_id=:receiptId")
    String getPaymentModeByReceiptId(@Param("receiptId") Long receiptId);

    @Query(nativeQuery = true, value = "select \n" +
            "    sr.service_request_id,\n" +
            "    concat(lms.decrypt_data(c.first_name, :encryptionKey, :password, :piiPermission), ' ', lms.decrypt_data(c.last_name, :encryptionKey, :password, :piiPermission)) as customer_name,\n" +
            "    (case when cast(sr.form->>'receipt_amount' as decimal) is null then 0 else cast(sr.form->>'receipt_amount' as decimal) end) as receipt_amount,\n" +
            "    sr.form->>'payment_mode' as payment_mode,\n" +
            "    case when sr.request_source = 'm_collect' then 'Syno Collect'\n" +
            "    when sr.request_source = 'manual_entry' then 'Manual Entry'\n" +
            "    when sr.request_source = 'bulk_upload' then 'Bulk Upload'\n" +
            "    when sr.request_source = 'bank_recon' then 'Bank Recon'\n" +
            "    else sr.request_source end as receipt_source,\n" +
            "    (select concat(u.\"name\", ' - (', u.username, ')') from master.users u where u.user_id = sr.created_by) as created_by,\n" +
            "    COUNT(sr.service_request_id) OVER () AS total_rows\n" +
            "from lms.service_request sr \n" +
            "join (select loan_application_number, loan_application_id from lms.loan_application) as la on la.loan_application_id = sr.loan_id\n" +
            "join (select loan_id, customer_id, customer_type from lms.customer_loan_mapping) as clm on clm.loan_id = sr.loan_id and clm.customer_type = 'applicant' \n" +
            "join (select customer_id, first_name, last_name  from lms.customer) as c on clm.customer_id = c.customer_id\n" +
            "where sr.status = 'initiated' and sr.form->>'payment_mode' = :paymentMode\n" +
            "and sr.service_request_id not in (select rth.collection_receipts_id from collection.receipt_transfer_history rth join collection.receipt_transfer rt on rth.receipt_transfer_id = rt.receipt_transfer_id where rt.status = 'pending')")
    List<Map<String, Object>> getReceiptsByUserIdWhichNotTransferredForPortal(@Param("paymentMode") String paymentMode, @Param("encryptionKey") String encryptionKey, @Param("password") String password, @Param("piiPermission") Boolean piiPermission, Pageable pageRequest);
}