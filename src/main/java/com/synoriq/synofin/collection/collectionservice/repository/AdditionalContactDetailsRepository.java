package com.synoriq.synofin.collection.collectionservice.repository;

import com.synoriq.synofin.collection.collectionservice.entity.AdditionalContactDetailsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface AdditionalContactDetailsRepository extends JpaRepository<AdditionalContactDetailsEntity, Long> {
    List<AdditionalContactDetailsEntity> findAllByLoanId(Long loanId);
}