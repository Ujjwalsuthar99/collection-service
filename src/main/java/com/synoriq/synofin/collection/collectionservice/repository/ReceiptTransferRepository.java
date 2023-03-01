package com.synoriq.synofin.collection.collectionservice.repository;

import com.synoriq.synofin.collection.collectionservice.entity.ReceiptTransferEntity;
import com.synoriq.synofin.collection.collectionservice.entity.RegisteredDeviceInfoEntity;
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


    @Query(nativeQuery = true,value = "select rt.receipt_transfer_id, rt.transfer_mode, rt.transfer_bank_code , rt.transfer_type , rt.transferred_to_user_id , rt.transferred_by, rt.status , rt.created_date , rt.amount , u.name as transferred_to_name \n" +
            "                ,case when rt.transferred_by = :transferredBy then 'transfer' else 'receiver' end as user_type, \n" +
            "                (case \n" +
            "                         when rt.status = 'pending' then '#F2994A'\n" +
            "                         when rt.status = 'approved' then '#229A16'\n" +
            "                         when rt.status = 'rejected' then '#EC1C24'\n" +
            "                         else '#B78103'\n" +
            "                end) as status_text_color_key,\n" +
            "                (case \n" +
            "                         when rt.status = 'pending' then '#FFF5D7'\n" +
            "                         when rt.status = 'approved' then '#E3F8DD'\n" +
            "                         when rt.status = 'rejected' then '#FFCECC'\n" +
            "                         else '#FCEBDB'\n" +
            "                end) as status_bg_color_key" +
            "               from collection.receipt_transfer rt left join master.users u on u.user_id = rt.transferred_to_user_id\n" +
            "               where (rt.transferred_by = :transferredBy or rt.transferred_to_user_id = :transferredBy)\n" +
            "               and rt.created_date between :fromDate and :toDate and rt.status = :status")
    List<Map<String, Object>> getReceiptTransferByUserId(@Param("transferredBy") Long transferredBy, @Param("fromDate") Date fromDate
            , @Param("toDate") Date toDate, @Param("status") String status, Pageable pageRequest);

    @Query(nativeQuery = true,value = "select rt.receipt_transfer_id, rt.transfer_mode, rt.transfer_bank_code , rt.transfer_type , rt.transferred_to_user_id , rt.transferred_by, rt.status , rt.created_date , rt.amount , u.name as transferred_to_name " +
            "            ,case when rt.transferred_by = :transferredBy then 'transfer' else 'receiver' end as user_type, \n" +
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
            "            end) as status_bg_color_key\n" +
            "            from collection.receipt_transfer rt left join master.users u on u.user_id = rt.transferred_to_user_id \n" +
            "            where (rt.transferred_by = :transferredBy or rt.transferred_to_user_id = :transferredBy) and rt.created_date between :fromDate and :toDate\n" +
            "            order by\n" +
            "            case when rt.status = 'pending' then 1\n" +
            "            when rt.status = 'approved' then 2\n" +
            "            else 3 end")
    List<Map<String, Object>> getReceiptTransferByUserIdWithAllStatus(@Param("transferredBy") Long transferredBy, @Param("fromDate") Date fromDate
            , @Param("toDate") Date toDate, Pageable pageRequest);


    @Query(nativeQuery = true,value = "select concat_ws(' ', c.first_name, c.last_name) as name, cast(sr.form->>'receipt_amount' as decimal) as receipt_amount, sr.service_request_id as receipt_id, sr.form->>'payment_mode' as payment_mode\n" +
            "         from collection.receipt_transfer rt \n" +
            "         join (select collection_receipts_id, receipt_transfer_id from collection.receipt_transfer_history) as rth on rt.receipt_transfer_id  = rth.receipt_transfer_id \n" +
            "         join (select service_request_id, loan_id, form from lms.service_request) as sr on sr.service_request_id = rth.collection_receipts_id \n" +
            "         join (select loan_application_id from lms.loan_application) as la on la.loan_application_id = sr.loan_id \n" +
            "         join (select loan_id, customer_id from lms.customer_loan_mapping) as clm on clm.loan_id  = la.loan_application_id \n" +
            "         join (select customer_id, first_name, last_name from lms.customer) as c on c.customer_id = clm.customer_id \n" +
            "         where rt.receipt_transfer_id = :receiptTransferId")
    List<Map<String, Object>> getDataByReceiptTransferId(@Param("receiptTransferId") Long receiptTransferId);
}
