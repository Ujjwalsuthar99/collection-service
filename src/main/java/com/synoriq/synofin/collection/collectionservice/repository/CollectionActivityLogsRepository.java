package com.synoriq.synofin.collection.collectionservice.repository;

import com.synoriq.synofin.collection.collectionservice.entity.CollectionActivityLogsEntity;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface CollectionActivityLogsRepository extends PagingAndSortingRepository<CollectionActivityLogsEntity, Long> {

    CollectionActivityLogsEntity findByCollectionActivityLogsId(Long activityLogId);

    @Query(nativeQuery = true,value = "select * from collection.collection_activity_logs where activity_by = :userId " +
            "and activity_date between :fromDate and :toDate ")
    List<CollectionActivityLogsEntity> getActivityLogsUserWIseByDuration(@Param("userId") Long userId, @Param("fromDate")Date fromDate
            , @Param("toDate") Date toDate, Pageable pageable);

    @Query(nativeQuery = true,value = "select cal.collection_activity_logs_id , cal.activity_date , cal.activity_by , cal.activity_name, cal.distance_from_user_branch , cal.address , cal.images , cal.remarks , cal.loan_id , cal.geo_location_data,cr.receipt_id  \n" +
            "from collection.collection_activity_logs cal left join collection.collection_receipts cr on cr.collection_activity_logs_id = cal.collection_activity_logs_id  " +
            "where loan_id = :loanId and activity_date between :fromDate and :toDate ")
    List<CollectionActivityLogsEntity> getActivityLogsLoanWiseByDuration(@Param("loanId") Long loanId, @Param("fromDate")Date fromDate
            , @Param("toDate") Date toDate, Pageable pageable);



}
