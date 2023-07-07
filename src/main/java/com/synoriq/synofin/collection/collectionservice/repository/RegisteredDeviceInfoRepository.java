package com.synoriq.synofin.collection.collectionservice.repository;

import com.synoriq.synofin.collection.collectionservice.entity.AdditionalContactDetailsEntity;
import com.synoriq.synofin.collection.collectionservice.entity.FollowUpEntity;
import com.synoriq.synofin.collection.collectionservice.entity.RegisteredDeviceInfoEntity;
import com.synoriq.synofin.collection.collectionservice.rest.request.RegisteredDeviceInfoDtoRequest;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface RegisteredDeviceInfoRepository extends JpaRepository<RegisteredDeviceInfoEntity, Long> {

    List<RegisteredDeviceInfoEntity> findDeviceInfoByUserId(Long userId);

    List<RegisteredDeviceInfoEntity> findDeviceInfoByUserIdAndByStatus(Long userId, String status);

    @Query(nativeQuery = true, value = "select\n" +
            "\trdi.registered_device_info_id as id,\n" +
            "\trdi.created_date as created_date,\n" +
            "\trdi.last_app_usages as last_app_usage,\n" +
            "\tu.username as user_name,\n" +
            "\tu.\"name\" as full_name,\n" +
            "\trdi.device_manufacturer_name as device_manufacturer_name,\n" +
            "\trdi.current_app_version as current_app_version,\n" +
            "\trdi.status,\n" +
            "\trdi.device_unique_id as deviceUniqueId\n" +
            "from\n" +
            "\tcollection.registered_device_info rdi\n" +
            "join master.users u on\n" +
            "\tu.user_id = rdi.user_id\n" +
            "where rdi.user_id = :userId")
    List<Map<String, Object>> getDeviceDataByUserId(@Param("userId") Long userId);
    RegisteredDeviceInfoEntity findByRegisteredDeviceInfoId(Long registeredDeviceInfoId);

}
