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

    @Query(nativeQuery = true, value = "select count(f.*) as total_count , 0 as action_count\n" +
            "from collection.followups f join (select loan_application_id ,days_past_due,product,sanctioned_amount from lms.loan_application) as la on la.loan_application_id = f.loan_id\n" +
            "where f.created_by = :userId \n" +
            "and f.created_date between :fromDate and :toDate")
    Map<String,Object> getFollowupCountByUserIdByDuration(@Param("userId") Long userId, @Param("fromDate") Date fromDate
            , @Param("toDate") Date toDate);

    @Query(nativeQuery = true, value = "select count(*) AS total_count, coalesce(sum(cast(coalesce(sr.form->>'receipt_amount', '0') as decimal)), '0') as total_amount\n" +
            "from lms.service_request sr join collection.collection_receipts cr on cr.receipt_id = sr.service_request_id where sr.request_source = 'm_collect' and sr.created_by = :userId " +
            "and date(sr.form->>'date_of_receipt') between to_date(:fromDate, 'DD-MM-YYYY') and to_date(:toDate, 'DD-MM-YYYY')")
    Map<String,Object> getReceiptCountByUserIdByDuration(@Param("userId") Long userId, @Param("fromDate") String fromDate
            , @Param("toDate") String toDate);

    @Query(nativeQuery = true, value = "select \n" +
            "coalesce(sum(amount), '0') as total_amount, count(*) as total_count\n" +
            "from collection.receipt_transfer rt where rt.deleted = false and rt.transferred_by = :userId and rt.status = 'approved' and rt.created_date between :fromDate and :toDate")
    Map<String,Object> getAmountTransferCountByUserIdByDuration(@Param("userId") Long userId, @Param("fromDate") Date fromDate
            , @Param("toDate") Date toDate);

    @Query(nativeQuery = true, value = "select \n" +
            "coalesce(sum(amount), '0') as total_amount, count(*) as total_count\n" +
            "from collection.receipt_transfer rt where rt.deleted = false and rt.transferred_by = :userId and rt.status = 'pending' and rt.created_date between :fromDate and :toDate")
    Map<String,Object> getAmountTransferInProcessCountByUserIdByDuration(@Param("userId") Long userId, @Param("fromDate") Date fromDate
            , @Param("toDate") Date toDate);


    @Query(nativeQuery = true, value = "select clu.utilized_limit_value as cash_in_hand,\n" +
            "clu.total_limit_value as cash_in_hand_limit\n" +
            "from collection.collection_limit_userwise clu\n" +
            "where clu.user_id = :userId and clu.collection_limit_strategies_key = 'cash'")
    Map<String,Object> getCashInHandByUserIdByDuration(@Param("userId") Long userId);


    @Query(nativeQuery = true, value = "select clu.utilized_limit_value as cheque_amount,\n" +
            "clu.total_limit_value as cheque_limit\n" +
            "from collection.collection_limit_userwise clu\n" +
            "where clu.user_id = :userId and clu.collection_limit_strategies_key = 'cheque'")
    Map<String,Object> getChequeByUserIdByDuration(@Param("userId") Long userId);

    @Query(nativeQuery = true, value = "select clu.utilized_limit_value as upi_amount,\n" +
            "clu.total_limit_value as upi_limit\n" +
            "from collection.collection_limit_userwise clu\n" +
            "where clu.user_id = :userId and clu.collection_limit_strategies_key = 'upi'")
    Map<String,Object> getUpiByUserIdByDuration(@Param("userId") Long userId);

    @Query(nativeQuery = true, value = "select clu.utilized_limit_value as neft_amount,\n" +
            "clu.total_limit_value as neft_limit\n" +
            "from collection.collection_limit_userwise clu\n" +
            "where clu.user_id = :userId and clu.collection_limit_strategies_key = 'neft'")
    Map<String,Object> getNeftByUserIdByDuration(@Param("userId") Long userId);

    @Query(nativeQuery = true, value = "select clu.utilized_limit_value as rtgs_amount,\n" +
            "clu.total_limit_value as rtgs_limit\n" +
            "from collection.collection_limit_userwise clu\n" +
            "where clu.user_id = :userId and clu.collection_limit_strategies_key = 'rtgs'")
    Map<String,Object> getRtgsByUserIdByDuration(@Param("userId") Long userId);

}