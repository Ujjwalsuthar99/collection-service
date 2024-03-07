package com.synoriq.synofin.collection.collectionservice.repository;

import com.synoriq.synofin.collection.collectionservice.entity.LoanAllocationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Repository
public interface LoanAllocationRepository extends JpaRepository<LoanAllocationEntity, Long> {

    List<LoanAllocationEntity> findByAllocatedToUserIdAndLoanId(Long userId, Long loanId);
    LoanAllocationEntity findByAllocatedToUserIdAndLoanIdAndDeleted(Long userId, Long loanId, boolean deleted);
    List<LoanAllocationEntity> getLoansByAllocatedToUserIdAndDeleted(Long allocatedToUserId, boolean deleted);
    @Query(nativeQuery = true, value = "select count(la) from collection.loan_allocation la where la.allocated_to_user_id = :allocatedToUserId and la.deleted is false")
    int getCountByAllocatedToUserIdAndDeleted(Long allocatedToUserId);
    List<Object> getLoansByLoanIdAndDeleted(Long loanId, boolean deleted);

    @Query(nativeQuery = true, value = "select\n" +
            "\tla.allocated_to_user_id as id,\n" +
            "\tu.\"name\" as \"name\",\n" +
            "\tu.username as \"employeeCode\"\n" +
            "from\n" +
            "\tcollection.loan_allocation la\n" +
            "join (select user_id, username, name from master.users) as u on\n" +
            "\tu.user_id = la.allocated_to_user_id\n" +
            "where\n" +
            "\tla.loan_id = :loanId\n" +
            "\tand la.deleted = false")
    List<Map<String, Object>> getAllocatedToUserIdsByLoanIdAndDeleted(Long loanId);

    @Modifying
    void deleteByCreatedDateBetween(Date fromDate, Date toDate);



}
