package com.synoriq.synofin.collection.collectionservice.repository;

import com.synoriq.synofin.collection.collectionservice.entity.ReceiptTransferEntity;
import org.springframework.data.domain.Pageable;
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


    @Query(nativeQuery = true,value = "select rt.receipt_transfer_id, rt.transfer_mode, rt.transfer_bank_code , rt.transfer_type , rt.transferred_to_user_id , rt.transferred_by, rt.status , rt.created_date , rt.amount \n" +
            "            ,(case when rt.transferred_to_user_id is null then (select ba.bank_name from master.bank_accounts ba where ba.bank_account_id = cast(rt.transfer_bank_code as bigint)) else u.name end) as transferred_to_name ,case when rt.transferred_by = :transferredBy then 'transfer' else 'receiver' end as user_type, \n" +
            "                (case \n" +
            "                         when rt.transfer_mode = 'cash' then '#136AD5'\n" +
            "                         when rt.transfer_mode = 'cheque' then '#136AD5'\n" +
            "                         else '#B78103'\n" +
            "                end) as transfer_mode_text_color_key,\n" +
            "                (case \n" +
            "                         when rt.transfer_mode = 'cash' then '#C6DDFA'\n" +
            "                         when rt.transfer_mode = 'cheque' then '#C6DDFA'\n" +
            "                         else '#FCEBDB'\n" +
            "                end) as transfer_mode_bg_color_key, \n" +
            "                (case \n" +
            "                         when rt.status = 'pending' then '#F2994A'\n" +
            "                         when rt.status = 'approved' or rt.status = 'payment_received' then '#229A16'\n" +
            "                         when rt.status = 'rejected' then '#EC1C24'\n" +
            "                         else '#B78103'\n" +
            "                end) as status_text_color_key,\n" +
            "                (case \n" +
            "                         when rt.status = 'pending' then '#FFF5D7'\n" +
            "                         when rt.status = 'approved' or rt.status = 'payment_received' then '#E3F8DD'\n" +
            "                         when rt.status = 'rejected' then '#FFCECC'\n" +
            "                         else '#FCEBDB'\n" +
            "                end) as status_bg_color_key,\n" +
            "               (select count(*) from collection.receipt_transfer_history rth where rth.receipt_transfer_id = rt.receipt_transfer_id) as receipt_count\n" +
            "               from collection.receipt_transfer rt left join master.users u on u.user_id = rt.transferred_to_user_id\n" +
            "               where rt.transferred_by = :transferredBy and rt.deleted = false\n" +
            "               and rt.created_date between :fromDate and :toDate and rt.status = :status order by rt.created_date desc")
    List<Map<String, Object>> getReceiptTransferByUserId(@Param("transferredBy") Long transferredBy, @Param("fromDate") Date fromDate
            , @Param("toDate") Date toDate, @Param("status") String status, Pageable pageRequest);

    @Query(nativeQuery = true,value = "select rt.receipt_transfer_id, rt.transfer_mode, rt.transfer_bank_code , rt.transfer_type , rt.transferred_to_user_id , rt.transferred_by, rt.status , rt.created_date , rt.amount\n" +
            "            ,(case when rt.transferred_to_user_id is null then (select ba.bank_name from master.bank_accounts ba where ba.bank_account_id = cast(rt.transfer_bank_code as bigint)) else u.name end) as transferred_to_name ,uu.name as transferred_by_name\n" +
            "               ,case when rt.transferred_by = :transferredBy then 'transfer' else 'receiver' end as user_type, \n" +
            "                (case \n" +
            "                         when rt.transfer_mode = 'cash' then '#136AD5'\n" +
            "                         when rt.transfer_mode = 'cheque' then '#136AD5'\n" +
            "                         else '#B78103'\n" +
            "                end) as transfer_mode_text_color_key,\n" +
            "                (case \n" +
            "                         when rt.transfer_mode = 'cash' then '#C6DDFA'\n" +
            "                         when rt.transfer_mode = 'cheque' then '#C6DDFA'\n" +
            "                         else '#FCEBDB'\n" +
            "                end) as transfer_mode_bg_color_key, \n" +
            "            (case \n" +
            "                        when rt.status = 'pending' then '#F2994A'\n" +
            "                        when rt.status = 'approved' then '#229A16'\n" +
            "                        when rt.status = 'rejected' then '#EC1C24'\n" +
            "                        else '#B78103'\n" +
            "            end) as status_color_key,\n" +
            "            (case \n" +
            "                        when rt.status = 'pending' then '#FFF5D7'\n" +
            "                        when rt.status = 'approved' then '#E3F8DD'\n" +
            "                        when rt.status = 'rejected' then '#FFCECC'\n" +
            "                        else '#FCEBDB'\n" +
            "            end) as status_bg_color_key,\n" +
            "            (select count(*) from collection.receipt_transfer_history rth where rth.receipt_transfer_id = rt.receipt_transfer_id) as receipt_count\n" +
            "            from collection.receipt_transfer rt left join (select user_id, name from master.users) as u on u.user_id = rt.transferred_to_user_id \n" +
            "            left join (select user_id, name from master.users) as uu on uu.user_id = rt.transferred_by \n" +
            "            where rt.transferred_by = :transferredBy and rt.created_date between :fromDate and :toDate and rt.deleted = false\n" +
            "            order by\n" +
            "            case when rt.status = 'pending' then 1\n" +
            "            when rt.status = 'approved' then 2\n" +
            "            else 3 end, rt.created_date desc")
    List<Map<String, Object>> getReceiptTransferByTransferUserIdWithAllStatus(@Param("transferredBy") Long transferredBy, @Param("fromDate") Date fromDate
            , @Param("toDate") Date toDate, Pageable pageRequest);

    @Query(nativeQuery = true,value = "select rt.receipt_transfer_id, rt.transfer_mode, rt.transfer_bank_code , rt.transfer_type , rt.transferred_to_user_id , rt.transferred_by, rt.status , rt.created_date , rt.amount  " +
            "            ,(case when rt.transferred_to_user_id is null then (select ba.bank_name from master.bank_accounts ba where ba.bank_account_id = cast(rt.transfer_bank_code as bigint)) else u.name end) as transferred_to_name ,uu.name as transferred_by_name\n" +
            "               ,case when rt.transferred_by = :transferredBy then 'transfer' else 'receiver' end as user_type, \n" +
            "                (case \n" +
            "                         when rt.transfer_mode = 'cash' then '#136AD5'\n" +
            "                         when rt.transfer_mode = 'cheque' then '#136AD5'\n" +
            "                         else '#B78103'\n" +
            "                end) as transfer_mode_text_color_key,\n" +
            "                (case \n" +
            "                         when rt.transfer_mode = 'cash' then '#C6DDFA'\n" +
            "                         when rt.transfer_mode = 'cheque' then '#C6DDFA'\n" +
            "                         else '#FCEBDB'\n" +
            "                end) as transfer_mode_bg_color_key, \n" +
            "            (case \n" +
            "                        when rt.status = 'pending' then '#F2994A'\n" +
            "                        when rt.status = 'approved' then '#229A16'\n" +
            "                        when rt.status = 'rejected' then '#EC1C24'\n" +
            "                        else '#B78103'\n" +
            "            end) as status_color_key,\n" +
            "            (case \n" +
            "                        when rt.status = 'pending' then '#FFF5D7'\n" +
            "                        when rt.status = 'approved' then '#E3F8DD'\n" +
            "                        when rt.status = 'rejected' then '#FFCECC'\n" +
            "                        else '#FCEBDB'\n" +
            "            end) as status_bg_color_key,\n" +
            "            (select count(*) from collection.receipt_transfer_history rth where rth.receipt_transfer_id = rt.receipt_transfer_id) as receipt_count\n" +
            "            from collection.receipt_transfer rt left join (select user_id, name from master.users) as u on u.user_id = rt.transferred_to_user_id \n" +
            "            left join (select user_id, name from master.users) as uu on uu.user_id = rt.transferred_by \n" +
            "            where rt.transferred_to_user_id = :transferredBy and rt.created_date between :fromDate and :toDate and rt.deleted = false\n" +
            "            order by\n" +
            "            case when rt.status = 'pending' then 1\n" +
            "            when rt.status = 'approved' then 2\n" +
            "            else 3 end, rt.created_date desc")
    List<Map<String, Object>> getReceiptTransferByReceiverUserIdWithAllStatus(@Param("transferredBy") Long transferredBy, @Param("fromDate") Date fromDate
            , @Param("toDate") Date toDate, Pageable pageRequest);


    @Query(nativeQuery = true,value = "select " +
//            "         concat_ws(' ', c.first_name, c.last_name) as name, " +
            "         concat(lms.decrypt_data(c.first_name, :encryptionKey, :password, :piiPermission), ' ', lms.decrypt_data(c.last_name, :encryptionKey, :password, :piiPermission)) as name,\n" +
            "         cast(sr.form->>'receipt_amount' as decimal) as receipt_amount, sr.service_request_id as receipt_id, sr.form->>'payment_mode' as payment_mode\n" +
            "         from collection.receipt_transfer rt \n" +
            "         join (select collection_receipts_id, receipt_transfer_id from collection.receipt_transfer_history) as rth on rt.receipt_transfer_id  = rth.receipt_transfer_id \n" +
            "         join (select service_request_id, loan_id, form from lms.service_request) as sr on sr.service_request_id = rth.collection_receipts_id \n" +
            "         join (select loan_application_id from lms.loan_application) as la on la.loan_application_id = sr.loan_id \n" +
            "         join (select loan_id, customer_id, customer_type from lms.customer_loan_mapping) as clm on clm.loan_id  = la.loan_application_id \n" +
            "         join (select customer_id, first_name, last_name from lms.customer) as c on c.customer_id = clm.customer_id \n" +
            "         where rt.receipt_transfer_id = :receiptTransferId and clm.customer_type = 'applicant'")
    List<Map<String, Object>> getDataByReceiptTransferId(@Param("receiptTransferId") Long receiptTransferId, @Param("encryptionKey") String encryptionKey, @Param("password") String password, @Param("piiPermission") Boolean piiPermission);

    @Query(nativeQuery = true, value = "select ba.bank_branch as bank_branch, ba.account_number as account_number, ba.bank_name from master.bank_accounts ba where ba.bank_account_id = :bankCode")
    Map<String, Object> getBankData(@Param("bankCode") Long bankCode);

    @Query(nativeQuery = true,value = "select cast(sr.form->>'receipt_amount' as decimal) as receipt_amount, sr.created_date as created_date, initcap(sr.form->>'payment_mode') as payment_mode,\n" +
            "\t\tsr.service_request_id as receipt_id, la.loan_application_number, sr.form->>'created_by' as created_by, sr.loan_id,\n" +
            "\t\tsr.status as status, cast(cal.images as text) as receipt_images, cast(cal.geo_location_data as text) as geo_location_data\n" +
            "         from collection.receipt_transfer rt \n" +
            "         join (select collection_receipts_id, receipt_transfer_id from collection.receipt_transfer_history) as rth on rt.receipt_transfer_id  = rth.receipt_transfer_id \n" +
            "         left join collection.collection_receipts cr on cr.receipt_id = rth.collection_receipts_id\n" +
            "         join (select service_request_id, loan_id, form, created_date, status from lms.service_request) as sr on sr.service_request_id = rth.collection_receipts_id \n" +
            "         join (select loan_application_id, loan_application_number from lms.loan_application) as la on la.loan_application_id = sr.loan_id \n" +
            "         left join collection.collection_activity_logs cal on cal.collection_activity_logs_id = cr.collection_activity_logs_id\n" +
            "         where rt.receipt_transfer_id = :receiptTransferId")
    List<Map<String, Object>> getReceiptsDataByReceiptTransferId(@Param("receiptTransferId") Long receiptTransferId);

    @Query(nativeQuery = true, value = "select\n" +
            "\trth.receipt_transfer_id\n" +
            "from\n" +
            "\tcollection.receipt_transfer_history rth\n" +
            "join collection.receipt_transfer rt on\n" +
            "\trth.receipt_transfer_id = rt.receipt_transfer_id\n" +
            "where\n" +
            "\trth.collection_receipts_id = :receiptId\n" +
            "\tand rt.status = 'pending'\n" +
            "\tand rt.transfer_type in ('bank', 'airtel')\n" +
            "\tand rth.deleted = false")
    String getDepositionOfReceipt(@Param("receiptId") Long receiptId);

    ReceiptTransferEntity findByReceiptTransferId(@Param("receiptTransferId") Long receiptTransferId);

    @Query(nativeQuery = true, value = "select\n" +
            "\tcast(u.\"name\" as text) as executive_name,\n" +
            "\trt.receipt_transfer_id,\n" +
            "\trt.created_date,\n" +
            "\trt.transfer_type,\n" +
            "\trt.transfer_mode,\n" +
            "\trt.transferred_to_user_id,\n" +
            "\trt.amount,\n" +
            "\trt.status,\n" +
            "\trt.remarks,\n" +
            "\trt.transfer_bank_code,\n" +
            "\trt.action_datetime,\n" +
            "\trt.action_reason,\n" +
            "\trt.action_remarks,\n" +
            "\trt.\"action_by\"\n" +
            "from\n" +
            "\tcollection.receipt_transfer rt\n" +
            "join master.users u on\n" +
            "\trt.transferred_by = u.user_id\n" +
            "where\n" +
            "\trt.receipt_transfer_id = :receiptTransferId")
    Map<String, Object> getReceiptTransferById(@Param("receiptTransferId") Long receiptTransferId);
}
