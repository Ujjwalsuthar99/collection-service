package com.synoriq.synofin.collection.collectionservice.repository;

import com.synoriq.synofin.collection.collectionservice.entity.CollectionLimitUserWiseEntity;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CollectionLimitUserWiseRepository extends JpaRepository<CollectionLimitUserWiseEntity, Long> {

    @Query(nativeQuery = true,value = "select * from collection.collection_limit_userwise where user_id = :userId " +
            "and deleted is false and collection_limit_strategies_key = :strategyKey")
    CollectionLimitUserWiseEntity getCollectionLimitUserWiseByUserId(@Param("userId") Long userId, @Param("strategyKey") String strategyKey);

    CollectionLimitUserWiseEntity findByUserIdAndCollectionLimitStrategiesKey(Long userId, String strategyKey);

    @Query(nativeQuery = true,value = "select * from collection.collection_limit_userwise where user_id = :userId " +
            "and deleted is false")
    List<CollectionLimitUserWiseEntity> getAllCollectionLimitUserWiseByUserId(@Param("userId") Long userId);

    @Query(nativeQuery = true, value = "select cast(u.username as text) as name from master.users u where u.user_id = :userId")
    String getUserNameFromUser(@Param("userId") Long userId);

    @Query(nativeQuery = true, value = "select cast(u.name as text) as name from master.users u where u.user_id = :userId")
    String getNameByUserId(@Param("userId") Long userId);

}
