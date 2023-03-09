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

    @Query(nativeQuery = true,value = "select cal.collection_activity_logs_id as collection_activity_logs_id, cal.activity_date as activity_date, cal.activity_by as activity_by, cal.activity_name as activity_name, cal.distance_from_user_branch as distance_from_user_branch,\n" +
            "CAST(cal.address as TEXT) as address, CAST(cal.images as TEXT) as images, cal.remarks as remarks, cal.loan_id as loan_id, CAST(cal.geo_location_data as TEXT) as geo_location_data, (case when cal.activity_name = 'create_receipt' then true else false end) as is_receipt, cr.receipt_id as receipt_id\n" +
            " from collection.collection_activity_logs cal left join collection.collection_receipts cr on cr.collection_activity_logs_id = cal.collection_activity_logs_id \n" +
            " where cal.loan_id = :loanId and cal.activity_date between :fromDate and :toDate order by cal.activity_date desc")
    List<Map<String, Object>> getActivityLogsLoanWiseByDuration(@Param("loanId") Long loanId, @Param("fromDate")Date fromDate
            , @Param("toDate") Date toDate, Pageable pageable);



}
