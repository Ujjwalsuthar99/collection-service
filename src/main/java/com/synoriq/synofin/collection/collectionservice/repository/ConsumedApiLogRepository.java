package com.synoriq.synofin.collection.collectionservice.repository;

import com.synoriq.synofin.collection.collectionservice.common.EnumSQLConstants;
import com.synoriq.synofin.collection.collectionservice.entity.ConsumedApiLogsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConsumedApiLogRepository extends JpaRepository<ConsumedApiLogsEntity, Long> {

    ConsumedApiLogsEntity findFirstByLoanIdAndLogNameAndResponseStatusOrderByConsumedApiLogsIdDesc(Long loanId, EnumSQLConstants.LogNames logName, String responseStatus);

}
