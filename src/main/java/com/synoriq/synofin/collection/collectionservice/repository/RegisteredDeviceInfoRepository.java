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

    @Query(nativeQuery = true, value = "select\n" +
            "\tu.username\n" +
            "from\n" +
            "\tmaster.users u\n" +
            "where\n" +
            "\tu.mobile_number = :mobileNumber\n" +
            "\tor u.phone_number = :mobileNumber\n" +
            "\tor cast(u.phone1_json as text) like concat('%', :mobileNumber, '%')\n" +
            "\tor cast(u.phone2_json as text) like concat('%', :mobileNumber, '%')\n" +
            "\tor cast(u.phone3_json as text) like concat('%', :mobileNumber, '%')")
    String getEmployeeMobileNumber(@Param("mobileNumber") String mobileNumber);

}
