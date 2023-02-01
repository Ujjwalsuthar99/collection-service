package com.synoriq.synofin.collection.collectionservice.repository;

import com.synoriq.synofin.collection.collectionservice.entity.CollectionConfigurationsEntity;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CollectionConfigurationsRepository extends JpaRepository<CollectionConfigurationsEntity, Long> {

    @Query(value = "Select c.configurationValue from CollectionConfigurationsEntity c where c.configurationName = :configurationName")
     String findConfigurationValueByConfigurationName(@Param("configurationName") String configurationName);


}
