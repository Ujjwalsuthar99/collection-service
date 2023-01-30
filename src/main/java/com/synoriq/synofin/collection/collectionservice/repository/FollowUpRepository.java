package com.synoriq.synofin.collection.collectionservice.repository;

import com.synoriq.synofin.collection.collectionservice.entity.FollowUpEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FollowUpRepository extends JpaRepository<FollowUpEntity, Long> {

    List<FollowUpEntity>  findByLoanId(Long loanId);
    List<FollowUpEntity> findByCreatedBy(Long userId);

}
