package com.synoriq.synofin.collection.collectionservice.repository;

import com.synoriq.synofin.collection.collectionservice.entity.RepossessionEntity;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface RepossessionRepository extends JpaRepository<RepossessionEntity, Long> {
    public RepossessionEntity findTop1ByLoanIdOrderByCreatedDateDesc(Long loanId);

    @Query(nativeQuery = true, value = "select\n" +
            "\tr.repossession_id,\n" +
            "\tr.created_date,\n" +
            "\t(select \"name\" from master.users where user_id = r.created_by) as repo_created_by,\n" +
            "\tr.loan_id,\n" +
            "\tla.loan_application_number,\n" +
            "\tconcat(c.first_name, ' ', c.last_name) as customer_name,\n" +
            "\tr.status,\n" +
            "\tr.remarks->>'initiated_remarks' as remarks\n" +
            "from\n" +
            "\tcollection.repossession r\n" +
            "join lms.loan_application la on\n" +
            "\tla.loan_application_id = r.loan_id\n" +
            "join lms.customer_loan_mapping clm on\n" +
            "\tclm.loan_id = la.loan_application_id and clm.customer_type = 'applicant'\n" +
            "join customer c on\n" +
            "\tc.customer_id = clm.customer_id\n" +
            "order by\n" +
            "\tr.repossession_id desc")
    public List<Map<String, Object>> getAllRepossession();

    @Query(nativeQuery = true, value = "select * from repossession where loan_id = :loanId and status = 'initiated' and deleted = false")
    public RepossessionEntity findByLoadIdAndInitiatedStatus(Long loanId);

    @Query(nativeQuery = true, value = "select cast(u.\"name\" as text) as name from master.users u where u.user_id = :userId")
    public String getNameFromUsers(@Param("userId") Long userId);

    @Query(nativeQuery = true, value = "select contact_person_mobile as mobile_number from lms.customer where customer_id = :customerId")
    public String getMobileNumber(@Param("customerId") Long customerId);
}