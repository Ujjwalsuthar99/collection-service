package com.synoriq.synofin.collection.collectionservice.repository;

import com.synoriq.synofin.collection.collectionservice.entity.AdditionalContactDetailsEntity;
import com.synoriq.synofin.collection.collectionservice.entity.FollowUpEntity;
import com.synoriq.synofin.collection.collectionservice.entity.RegisteredDeviceInfoEntity;
import com.synoriq.synofin.collection.collectionservice.rest.request.RegisteredDeviceInfoDtoRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RegisteredDeviceInfoRepository extends JpaRepository<RegisteredDeviceInfoEntity, Long> {

    List<RegisteredDeviceInfoEntity> findDeviceInfoByUserId(Long userId);

}
