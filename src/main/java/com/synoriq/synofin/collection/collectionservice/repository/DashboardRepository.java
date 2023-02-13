package com.synoriq.synofin.collection.collectionservice.repository;

import com.synoriq.synofin.collection.collectionservice.entity.FollowUpEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
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


}