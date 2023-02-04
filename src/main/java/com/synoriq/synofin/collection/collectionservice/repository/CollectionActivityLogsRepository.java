package com.synoriq.synofin.collection.collectionservice.repository;

import com.synoriq.synofin.collection.collectionservice.entity.CollectionActivityLogsEntity;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface CollectionActivityLogsRepository extends JpaRepository<CollectionActivityLogsEntity, Long> {

    CollectionActivityLogsEntity findByCollectionActivityLogsId(Long activityLogId);

    @Query(nativeQuery = true,value = "select * from collection.collection_activity_logs where activity_by = :userId " +
            "and activity_date between :fromDate and :toDate ")
    List<CollectionActivityLogsEntity> getActivityLogsByUserIdAndDuration(@Param("userId") Long userId, @Param("fromDate")Date fromDate
            , @Param("toDate") Date toDate );

    @Query(nativeQuery = true,value = "select * from collection.collection_activity_logs where loan_id = :loanId " +
            "and activity_date between :fromDate and :toDate ")
    List<CollectionActivityLogsEntity> getActivityLogsByLoanIdAndDuration(@Param("loanId") Long loanId, @Param("fromDate")Date fromDate
            , @Param("toDate") Date toDate);


}
