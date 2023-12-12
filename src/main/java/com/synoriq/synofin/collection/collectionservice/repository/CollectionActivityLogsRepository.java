package com.synoriq.synofin.collection.collectionservice.repository;

import com.synoriq.synofin.collection.collectionservice.entity.CollectionActivityLogsEntity;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Repository
public interface CollectionActivityLogsRepository extends PagingAndSortingRepository<CollectionActivityLogsEntity, Long> {

    CollectionActivityLogsEntity findByCollectionActivityLogsId(Long activityLogId);

    @Query(nativeQuery = true,value = "select * from collection.collection_activity_logs where activity_by = :userId " +
            "and activity_date between :fromDate and :toDate order by activity_date desc")
    List<CollectionActivityLogsEntity> getActivityLogsUserWIseByDuration(@Param("userId") Long userId, @Param("fromDate")Date fromDate
            , @Param("toDate") Date toDate, Pageable pageable);

    @Query(nativeQuery = true,value = "select\n" +
            "\tcal.collection_activity_logs_id as collection_activity_logs_id,\n" +
            "\t(select u.username from master.users u where u.user_id = cal.activity_by) as user_name,\n" +
            "\tcal.battery_percentage,\n" +
            "\tcal.activity_date as activity_date,\n" +
            "\tcal.activity_by as activity_by,\n" +
            "\tcal.activity_name as activity_name,\n" +
            "\tcal.remarks as remarks,\n" +
            "\tcal.loan_id as loan_id,\n" +
            "\tcal.distance_from_user_branch as distance_from_user_branch,\n" +
            "\tcast(cal.geo_location_data as text) as geo_location_data,\n" +
            "\tcast(cal.address as text) as address,\n" +
            "\tcast(cal.images as text) as images,\n" +
            "\t(case\n" +
            "\t\twhen cal.activity_name = 'create_receipt' then true\n" +
            "\t\telse false\n" +
            "\tend) as is_receipt,\n" +
            "\tcr.receipt_id as receipt_id,\n" +
            "\tCOUNT(cal.collection_activity_logs_id) OVER () AS total_rows\n" +
            "from\n" +
            "\tcollection.collection_activity_logs cal\n" +
            "left join collection.collection_receipts cr on\n" +
            "\tcr.collection_activity_logs_id = cal.collection_activity_logs_id\n" +
            "where\n" +
            "\tcal.loan_id = :loanId and cal.activity_name = :filterBy\n" +
            "\tand cal.activity_date between :fromDate and :toDate\n" +
            "order by\n" +
            "\tcal.activity_date desc")
    List<Map<String, Object>> getActivityLogsLoanWiseByDurationByFilter(@Param("loanId") Long loanId, @Param("fromDate") Date fromDate
            , @Param("toDate") Date toDate, @Param("filter") String filterBy, Pageable pageable);

    @Query(nativeQuery = true,value = "select cal.collection_activity_logs_id as collection_activity_logs_id, (select u.username from master.users u where u.user_id = cal.activity_by) as user_name,\n" +
            " cal.battery_percentage, cal.activity_date as activity_date, cal.activity_by as activity_by, cal.activity_name as activity_name, cal.distance_from_user_branch as distance_from_user_branch,\n" +
            " CAST(cal.address as TEXT) as address, CAST(cal.images as TEXT) as images, cal.remarks as remarks, cal.loan_id as loan_id, CAST(cal.geo_location_data as TEXT) as geo_location_data, (case when cal.activity_name = 'create_receipt' then true else false end) as is_receipt, cr.receipt_id as receipt_id, COUNT(cal.collection_activity_logs_id) OVER () AS total_rows\n" +
            " from collection.collection_activity_logs cal left join collection.collection_receipts cr on cr.collection_activity_logs_id = cal.collection_activity_logs_id \n" +
            " where cal.loan_id = :loanId and cal.activity_date between :fromDate and :toDate order by cal.activity_date desc")
    List<Map<String, Object>> getActivityLogsLoanWiseByDuration(@Param("loanId") Long loanId, @Param("fromDate")Date fromDate
            , @Param("toDate") Date toDate, Pageable pageable);

    @Query(nativeQuery = true,value = "select * from collection.collection_activity_logs cal where cal.remarks like concat('%', :serviceRequestId, '%') and cal.activity_name in ('receipt_approved', 'receipt_rejected')")
    CollectionActivityLogsEntity getActivityLogsKafkaByReceiptId(@Param("serviceRequestId") String serviceRequestId);


    @Query(nativeQuery = true, value = "select (select l.\"label\" from master.lists l where l.list_name = 'followup_reason' and l.key = f.followup_reason) as reason \n" +
            "from collection.followups f \n" +
            "where f.collection_activity_logs_id = :activityLogId")
    String getFollowUpReason(@Param("activityLogId") Long activityLogId);

    public List<CollectionActivityLogsEntity> findActivityLogByReferenceId(Long referenceId);

    @Query(nativeQuery = true, value = "select * from collection.collection_activity_logs cal where cal.loan_id = :loanId and cal.activity_name like 'repossession_%' order by cal.activity_date desc")
    public List<CollectionActivityLogsEntity> getActivityLogsDataByLoanIdWithRepossession(@Param("loanId") Long loanId);

}
