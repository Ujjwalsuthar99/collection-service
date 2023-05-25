package com.synoriq.synofin.collection.collectionservice.repository;

import com.synoriq.synofin.collection.collectionservice.entity.LoanAllocationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface LoanAllocationRepository extends JpaRepository<LoanAllocationEntity, Long> {

    LoanAllocationEntity findByAllocatedToUserIdAndDeleted(Long userId, boolean deleted);
    List<LoanAllocationEntity> getLoansByAllocatedToUserIdAndDeleted(Long allocatedToUserId, boolean deleted);

    List<Object> getLoansByLoanIdAndDeleted(Long loanId, boolean deleted);

    @Query(nativeQuery = true, value = "select\n" +
            "\tla.allocated_to_user_id as id,\n" +
            "\tu.\"name\" as full_name,\n" +
            "\tu.employee_code as employee_code\n" +
            "from\n" +
            "\tcollection.loan_allocation la\n" +
            "join (select user_id, employee_code, name from master.users) as u on\n" +
            "\tu.user_id = la.allocated_to_user_id\n" +
            "where\n" +
            "\tla.loan_id = :loanId\n" +
            "\tand la.deleted = false")
    List<Map<String, Object>> getAllocatedToUserIdsByLoanIdAndDeleted(Long loanId);

}
