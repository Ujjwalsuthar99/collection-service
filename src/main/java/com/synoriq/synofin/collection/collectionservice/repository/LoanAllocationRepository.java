package com.synoriq.synofin.collection.collectionservice.repository;

import com.synoriq.synofin.collection.collectionservice.entity.LoanAllocationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanAllocationRepository extends JpaRepository<LoanAllocationEntity, Long> {

    List<LoanAllocationEntity> getLoansByAllocatedToUserId(Long allocatedToUserId);

    List<Object> getLoansByLoanId(Long loanId);

}
