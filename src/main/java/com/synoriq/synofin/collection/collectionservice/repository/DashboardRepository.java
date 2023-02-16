package com.synoriq.synofin.collection.collectionservice.repository;

import com.synoriq.synofin.collection.collectionservice.entity.FollowUpEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.Map;

@Repository
public interface DashboardRepository extends JpaRepository<FollowUpEntity, Long> {

    @Query(nativeQuery = true, value = "select\n" +
            "SUM(case when cal.activity_name in ('Create Follow Up', 'Reschedule') then 1 else 0 end) as total_count,\n" +
            "SUM(case when cal.activity_name = 'Closed' then 1 else 0 end) as action_count\n" +
            "from collection.followups f join collection.collection_activity_logs cal on cal.collection_activity_logs_id = f.collection_activity_logs_id where f.created_by = :userId " +
            "and f.next_followup_datetime between :fromDate and :toDate")
    Map<String,Object> getFollowupCountByUserIdByDuration(@Param("userId") Long userId, @Param("fromDate") Date fromDate
            , @Param("toDate") Date toDate);

    @Query(nativeQuery = true, value = "select count(*) AS total_count, sum(cast(coalesce(sr.form->>'receipt_amount', '0') as integer)) as total_amount\n" +
            "from lms.service_request sr where sr.request_source = 'm_collect' and sr.form->>'created_by' = :userId " +
            "and date(sr.form->>'transaction_date') between to_date(:fromDate, 'DD-MM-YYYY') and to_date(:toDate, 'DD-MM-YYYY')")
    Map<String,Object> getReceiptCountByUserIdByDuration(@Param("userId") String userId, @Param("fromDate") String fromDate
            , @Param("toDate") String toDate);

    @Query(nativeQuery = true, value = "select \n" +
            "sum(amount) as total_amount, count(*) as total_count\n" +
            "from collection.receipt_transfer rt where rt.deleted = false and rt.action_by = :userId and date(rt.created_date) between :fromDate and :toDate")
    Map<String,Object> getAmountTransferCountByUserIdByDuration(@Param("userId") Long userId, @Param("fromDate") Date fromDate
            , @Param("toDate") Date toDate);

    @Query(nativeQuery = true, value = "select \n" +
            "sum(amount) as total_amount, count(*) as total_count\n" +
            "from collection.receipt_transfer rt where rt.deleted = false and rt.action_by = :userId and rt.status = 'pending' and date(rt.created_date) between :fromDate and :toDate")
    Map<String,Object> getAmountTransferInProcessCountByUserIdByDuration(@Param("userId") Long userId, @Param("fromDate") Date fromDate
            , @Param("toDate") Date toDate);

    @Query(nativeQuery = true, value = "select sum(cast(sr.form->>'receipt_amount' as decimal)) as cash_in_hand\n" +
            "from lms.service_request sr\n" +
            "join collection.receipt_transfer_history rth on sr.service_request_id != rth.collection_receipts_id where sr.request_source = 'm_collect' and sr.form->>'payment_mode' = 'cash' and sr.form->>'created_by' = :userId " +
            "and date(sr.form->>'transaction_date') between to_date(:fromDate, 'DD-MM-YYYY') and to_date(:toDate, 'DD-MM-YYYY')")
    Map<String,Object> getCashInHandByUserIdByDuration(@Param("userId") String userId, @Param("fromDate") String fromDate
            , @Param("toDate") String toDate);


    @Query(nativeQuery = true, value = "select sum(cast(sr.form->>'receipt_amount' as decimal)) as cheque_amount\n" +
            "from lms.service_request sr\n" +
            "join collection.receipt_transfer_history rth on sr.service_request_id != rth.collection_receipts_id where sr.request_source = 'm_collect' and sr.form->>'payment_mode' = 'cheque' and sr.form->>'created_by' = :userId " +
            "and date(sr.form->>'transaction_date') between to_date(:fromDate, 'DD-MM-YYYY') and to_date(:toDate, 'DD-MM-YYYY')")
    Map<String,Object> getChequeByUserIdByDuration(@Param("userId") String userId, @Param("fromDate") String fromDate
            , @Param("toDate") String toDate);
}