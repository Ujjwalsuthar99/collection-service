package com.synoriq.synofin.collection.collectionservice.repository;

import com.synoriq.synofin.collection.collectionservice.entity.DigitalPaymentTransactionsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DigitalPaymentTransactionsRepository extends JpaRepository<DigitalPaymentTransactionsEntity, Long> {

    DigitalPaymentTransactionsEntity findByDigitalPaymentTransactionsId(Long digitalPaymentTransactionsId);

}
