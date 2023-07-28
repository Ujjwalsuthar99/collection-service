package com.synoriq.synofin.collection.collectionservice.repository;

import com.synoriq.synofin.collection.collectionservice.entity.ReceiptTransferHistoryEntity;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

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
            "\trt.receipt_transfer_id, rt.created_date, concat(uu.name, ' ', uu.username) as transfer_by_name, (case when rt.transferred_to_user_id is null then (select ba.bank_name from master.bank_accounts ba where ba.bank_account_id = cast(rt.transfer_bank_code as bigint)) else concat(u.name, ' ', u.username) end) as transfer_to_name,\n" +
            "\trt.transfer_type, rt.amount as deposit_amount, ba2.bank_name , ba2.account_number, CAST(cal.geo_location_data as TEXT) as transfer_location_data, CAST(cal2.geo_location_data as text) as approval_location_data,\n" +
            "\tCAST(rt.receipt_image as TEXT) as receipt_image, rt.status as status, COUNT(rt.receipt_transfer_id) OVER () AS total_rows\n" +
            "from\n" +
            "\tcollection.receipt_transfer rt\n" +
            "join collection.receipt_transfer_history rth on rt.receipt_transfer_id = rth.receipt_transfer_id\n" +
            "left join (select user_id, name, username from master.users) as u on u.user_id = rt.transferred_to_user_id\n" +
            "left join (select user_id, name, username from master.users) as uu on uu.user_id = rt.transferred_by \n" +
            "left join (select bank_account_id, bank_name, account_number from master.bank_accounts) as ba2 on cast(ba2.bank_account_id as text) = rt.transfer_bank_code\n" +
            "join collection.collection_activity_logs cal on cal.collection_activity_logs_id = rt.collection_activity_logs_id \n" +
            "left join (select collection_activity_logs_id, geo_location_data from collection.collection_activity_logs) as cal2 on cal2.collection_activity_logs_id = rt.action_activity_logs_id\n" +
            "where rt.transfer_type='bank' and rt.status in (:statusList)")
    List<Map<String, Object>> getAllBankTransfers(@Param("statusList") List<String> statusList, Pageable pageable);

}
