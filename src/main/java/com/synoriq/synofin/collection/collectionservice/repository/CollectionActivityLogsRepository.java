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
    Page<CollectionActivityLogsEntity> getActivityLogsByUserIdAndDuration(@Param("userId") Long userId, @Param("fromDate")Date fromDate
            , @Param("toDate") Date toDate, Pageable pageable);

    @Query(nativeQuery = true,value = "select * from collection.collection_activity_logs where loan_id = :loanId " +
            "and activity_date between :fromDate and :toDate ")
    Page<CollectionActivityLogsEntity> getActivityLogsByLoanIdAndDuration(@Param("loanId") Long loanId, @Param("fromDate")Date fromDate
            , @Param("toDate") Date toDate, Pageable pageable);

    @Query(nativeQuery = true,value = "select * from collection.collection_activity_logs where activity_by = :userId " +
            "and activity_date between :fromDate and :toDate ")
    Page<CollectionActivityLogsEntity> findByActivityBy(@Param("userId") Long userId, @Param("fromDate")Date fromDate
            , @Param("toDate") Date toDate, Pageable pageable);


}
