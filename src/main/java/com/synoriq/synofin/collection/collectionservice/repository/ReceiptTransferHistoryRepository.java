package com.synoriq.synofin.collection.collectionservice.repository;

import com.synoriq.synofin.collection.collectionservice.entity.ReceiptTransferHistoryEntity;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Repository
public interface ReceiptTransferHistoryRepository extends JpaRepository<ReceiptTransferHistoryEntity, Long> {

    List<ReceiptTransferHistoryEntity> getReceiptTransferHistoryDataByReceiptTransferId(Long receiptTransferId);

    @Query(nativeQuery = true,value = "select * from collection.receipt_transfer_history rth where rth.receipt_transfer_id <> :receiptTransferId and rth.collection_receipts_id = :receiptId and deleted is false")
    List<ReceiptTransferHistoryEntity> buttonRestriction(@Param("receiptTransferId") Long receiptTransferId, @Param("receiptId") Long receiptId);

    @Query(nativeQuery = true, value = "select\n" +
            "\trt.receipt_transfer_id, rt.created_date, concat(uu.name, ' ', uu.username) as transfer_by_name, (case when rt.transferred_to_user_id is null then (select ba.bank_name from master.bank_accounts ba where ba.bank_account_id = cast(rt.transfer_bank_code as bigint)) else concat(u.name, ' ', u.username) end) as transfer_to_name,\n" +
            "\trt.transfer_type, rt.amount as deposit_amount, ba2.bank_name , ba2.account_number, CAST(cal.geo_location_data as TEXT) as transfer_location_data, CAST(cal2.geo_location_data as text) as approval_location_data,\n" +
            "\tCAST(rt.receipt_image as TEXT) as receipt_image, rt.status as status\n" +
            "from\n" +
            "\tcollection.receipt_transfer rt\n" +
            "join collection.receipt_transfer_history rth on rt.receipt_transfer_id = rth.receipt_transfer_id\n" +
            "left join (select user_id, name, username from master.users) as u on u.user_id = rt.transferred_to_user_id\n" +
            "left join (select user_id, name, username from master.users) as uu on uu.user_id = rt.transferred_by \n" +
            "left join (select bank_account_id, bank_name, account_number from master.bank_accounts) as ba2 on cast(ba2.bank_account_id as text) = rt.transfer_bank_code\n" +
            "join collection.collection_activity_logs cal on cal.collection_activity_logs_id = rt.collection_activity_logs_id \n" +
            "left join (select collection_activity_logs_id, geo_location_data from collection.collection_activity_logs) as cal2 on cal2.collection_activity_logs_id = rt.action_activity_logs_id \n" +
            "where\n" +
            "\trth.collection_receipts_id = :receiptId")
    List<Map<String, Object>> getReceiptTransferByReceiptId(@Param("receiptId") Long receiptId);


    @Query(nativeQuery = true, value = "select\n" +
            "\trt.receipt_transfer_id, rt.created_date, concat(uu.name, ' ', uu.username) as transfer_by_name, (case when rt.transferred_to_user_id is null then (select ba.bank_name from master.bank_accounts ba where cast(ba.bank_account_id as text) = cast(rt.transfer_bank_code as text)) else concat(u.name, ' ', u.username) end) as transfer_to_name,\n" +
            "\trt.transfer_type, rt.amount as deposit_amount, ba2.bank_name , ba2.account_number, CAST(cal.geo_location_data as TEXT) as transfer_location_data, CAST(cal2.geo_location_data as text) as approval_location_data, rt.action_datetime,\n" +
            "\tCAST(rt.receipt_image as TEXT) as receipt_image, rt.status as status, (case when rt.status != 'pending' then (select username from master.users where user_id = rt.\"action_by\") else '' end) as approved_by, COUNT(rt.receipt_transfer_id) OVER () AS total_rows\n" +
            "from\n" +
            "\tcollection.receipt_transfer rt\n" +
            "left join (select user_id, name, username from master.users) as u on u.user_id = rt.transferred_to_user_id\n" +
            "left join (select user_id, name, username from master.users) as uu on uu.user_id = rt.transferred_by \n" +
            "left join (select bank_account_id, bank_name, account_number from master.bank_accounts) as ba2 on cast(ba2.bank_account_id as text) = rt.transfer_bank_code\n" +
            "join collection.collection_activity_logs cal on cal.collection_activity_logs_id = rt.collection_activity_logs_id \n" +
            "left join (select collection_activity_logs_id, geo_location_data from collection.collection_activity_logs) as cal2 on cal2.collection_activity_logs_id = rt.action_activity_logs_id\n" +
            "where rt.transfer_type='bank' and rt.status in (:statusList)\n" +
            "and (\n" +
            "\tLOWER(cast(rt.receipt_transfer_id as text)) like LOWER(concat('%', :searchKey, '%'))\n" +
            "\tor LOWER(uu.username) like LOWER(concat('%', :searchKey, '%'))\n" +
            ")\n" +
            "order by rt.created_date desc")
    List<Map<String, Object>> getAllBankTransfers(@Param("statusList") List<String> statusList, @Param("searchKey") String searchKey, Pageable pageable);


    @Query(nativeQuery = true, value = "select\n" +
            "\tsr.service_request_id, sr.status\n" +
            "from\n" +
            "\tcollection.receipt_transfer rt\n" +
            "join collection.receipt_transfer_history rth on\n" +
            "\trth.receipt_transfer_id = rt.receipt_transfer_id\n" +
            "join lms.service_request sr on\n" +
            "\trth.collection_receipts_id = sr.service_request_id\n" +
            "where\n" +
            "\trt.transfer_type = 'bank'\n" +
            "\tand rt.status = 'pending'\n" +
            "\tand rth.receipt_transfer_id = :receiptTransferId")
    List<Map<String, Object>> getDepositPendingReceipt(@Param("receiptTransferId") Long receiptTransferId);

    @Query(nativeQuery = true, value = "select\n" +
            "\tcast(count(rth.collection_receipts_id) as integer)\n" +
            "from\n" +
            "\tcollection.receipt_transfer_history rth\n" +
            "where\n" +
            "\trth.receipt_transfer_id = :receiptTransferId")
    Long getReceiptCountFromReceiptTransfer(@Param("receiptTransferId") Long receiptTransferId);

    @Query(nativeQuery = true, value = "select\n" +
            "\trth.receipt_transfer_id\n" +
            "from\n" +
            "\tcollection.receipt_transfer_history rth\n" +
            "join collection.receipt_transfer rt on\n" +
            "\trth.receipt_transfer_id = rt.receipt_transfer_id\n" +
            "where\n" +
            "\trth.collection_receipts_id = :receiptId\n" +
            "\tand rt.transfer_type = 'bank'\n" +
            "\tand rt.deleted = false\n" +
            "order by\n" +
            "\trt.created_date asc\n" +
            "limit 1")
    Long getReceiptTransferIdUsingReceiptId(@Param("receiptId") Long receiptId);

    public ReceiptTransferHistoryEntity findByCollectionReceiptsIdAndDeleted(Long receiptId, boolean deleted);

    public List<ReceiptTransferHistoryEntity> findByDeletedAndCollectionReceiptsIdIn(boolean deleted, Set<Long> collectionReceiptsId);

}
