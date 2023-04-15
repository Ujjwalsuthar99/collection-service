package com.synoriq.synofin.collection.collectionservice.repository;

import com.synoriq.synofin.collection.collectionservice.entity.AdditionalContactDetailsEntity;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface AdditionalContactDetailsRepository extends JpaRepository<AdditionalContactDetailsEntity, Long> {
    List<AdditionalContactDetailsEntity> findAllByLoanId(Long loanId);

    @Query(nativeQuery = true, value = "select * from collection.additional_contact_details where mobile_no = :mobileNumber and loan_id = :loanId and relation_with_applicant = :relation")
    AdditionalContactDetailsEntity getDetailByLoanIdByRelationByMobileNumber(@Param("loanId") Long loanId, @Param("relation") String relation, @Param("mobileNumber") Long mobileNumber);
}