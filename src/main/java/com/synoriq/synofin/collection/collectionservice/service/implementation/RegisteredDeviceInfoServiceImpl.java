package com.synoriq.synofin.collection.collectionservice.service.implementation;

import com.synoriq.synofin.collection.collectionservice.entity.RegisteredDeviceInfoEntity;
import com.synoriq.synofin.collection.collectionservice.repository.RegisteredDeviceInfoRepository;
import com.synoriq.synofin.collection.collectionservice.rest.request.DeviceStatusUpdateDTORequest;
import com.synoriq.synofin.collection.collectionservice.rest.request.RegisteredDeviceInfoDtoRequest;
import com.synoriq.synofin.collection.collectionservice.rest.response.BaseDTOResponse;
import com.synoriq.synofin.collection.collectionservice.service.RegisteredDeviceInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@Slf4j
public class RegisteredDeviceInfoServiceImpl implements RegisteredDeviceInfoService {

    private static final String ACTIVE_STATUS =  "active";

    @Autowired
    private RegisteredDeviceInfoRepository registeredDeviceInfoRepository;

    @Override
    public List<Map<String, Object>> findDeviceInfoByUserId(Long userId) {
        List<Map<String, Object>> getDeviceDataByUserIdList;
        getDeviceDataByUserIdList = registeredDeviceInfoRepository.getDeviceDataByUserId(userId);
        return getDeviceDataByUserIdList;
    }

    @Override
    public BaseDTOResponse<Object> createRegisteredDeviceInfo(RegisteredDeviceInfoDtoRequest registeredDeviceInfoDtoRequest, String userId) {
        List<RegisteredDeviceInfoEntity> registeredDeviceInfoEntityList = registeredDeviceInfoRepository.findDeviceInfoByUserId(Long.valueOf(userId));
        if (!registeredDeviceInfoEntityList.isEmpty()) {
            for (RegisteredDeviceInfoEntity registeredDeviceInfoEntity : registeredDeviceInfoEntityList) {
                if (Objects.equals(registeredDeviceInfoEntity.getStatus(), ACTIVE_STATUS)) {
                    return new BaseDTOResponse<>("Active Device Found");
                }
                if (Objects.equals(registeredDeviceInfoEntity.getDeviceUniqueId(), registeredDeviceInfoDtoRequest.getDeviceUniqueId())) {
                    registeredDeviceInfoEntity.setStatus(ACTIVE_STATUS);
                    registeredDeviceInfoRepository.save(registeredDeviceInfoEntity);
                }
            }
            RegisteredDeviceInfoEntity registeredDeviceInfoEntity = new RegisteredDeviceInfoEntity();
            registeredDeviceInfoEntity.setCreatedDate(new Date());
            registeredDeviceInfoEntity.setCreatedBy(registeredDeviceInfoDtoRequest.getCreatedBy());
            registeredDeviceInfoEntity.setUserId(Long.valueOf(userId));
            registeredDeviceInfoEntity.setLastAppUsage(new Date());
            registeredDeviceInfoEntity.setCurrentAppVersion(registeredDeviceInfoDtoRequest.getCurrentAppVersion());
            registeredDeviceInfoEntity.setPlatform(registeredDeviceInfoDtoRequest.getPlatform());
            registeredDeviceInfoEntity.setPlatformVersion(registeredDeviceInfoDtoRequest.getPlatformVersion());
            registeredDeviceInfoEntity.setDeviceUniqueId(registeredDeviceInfoDtoRequest.getDeviceUniqueId());
            registeredDeviceInfoEntity.setDeviceManufacturerName(registeredDeviceInfoDtoRequest.getDeviceManufacturerName());
            registeredDeviceInfoEntity.setDeviceModel(registeredDeviceInfoDtoRequest.getDeviceModel());
            registeredDeviceInfoEntity.setStatus(registeredDeviceInfoDtoRequest.getStatus());

            registeredDeviceInfoRepository.save(registeredDeviceInfoEntity);
            return new BaseDTOResponse<>(registeredDeviceInfoEntity);
        } else {
            RegisteredDeviceInfoEntity registeredDeviceInfoEntity = new RegisteredDeviceInfoEntity();
            registeredDeviceInfoEntity.setCreatedDate(new Date());
            registeredDeviceInfoEntity.setCreatedBy(registeredDeviceInfoDtoRequest.getCreatedBy());
            registeredDeviceInfoEntity.setUserId(Long.valueOf(userId));
            registeredDeviceInfoEntity.setLastAppUsage(new Date());
            registeredDeviceInfoEntity.setCurrentAppVersion(registeredDeviceInfoDtoRequest.getCurrentAppVersion());
            registeredDeviceInfoEntity.setPlatform(registeredDeviceInfoDtoRequest.getPlatform());
            registeredDeviceInfoEntity.setPlatformVersion(registeredDeviceInfoDtoRequest.getPlatformVersion());
            registeredDeviceInfoEntity.setDeviceUniqueId(registeredDeviceInfoDtoRequest.getDeviceUniqueId());
            registeredDeviceInfoEntity.setDeviceManufacturerName(registeredDeviceInfoDtoRequest.getDeviceManufacturerName());
            registeredDeviceInfoEntity.setDeviceModel(registeredDeviceInfoDtoRequest.getDeviceModel());
            registeredDeviceInfoEntity.setStatus(registeredDeviceInfoDtoRequest.getStatus());

            registeredDeviceInfoRepository.save(registeredDeviceInfoEntity);
            return new BaseDTOResponse<>(registeredDeviceInfoEntity);
        }
    }

    @Override
    public String deviceStatusUpdate(DeviceStatusUpdateDTORequest deviceStatusUpdateDTORequest) {
        List<Map<String, Object>> getDeviceDataByUserIdList = findDeviceInfoByUserId(deviceStatusUpdateDTORequest.getUserId());
        RegisteredDeviceInfoEntity registeredDeviceInfoData;
        for (Map<String, Object> data : getDeviceDataByUserIdList) {
            if (data.get("id") != deviceStatusUpdateDTORequest.getRegisteredDeviceInfoId() && Objects.equals(data.get("status").toString(), ACTIVE_STATUS)) {
                registeredDeviceInfoData = registeredDeviceInfoRepository.findByRegisteredDeviceInfoId(Long.parseLong(String.valueOf(data.get("id"))));
                registeredDeviceInfoData.setStatus("inactive");
                registeredDeviceInfoRepository.save(registeredDeviceInfoData);
            }
        }
        registeredDeviceInfoData = registeredDeviceInfoRepository.findByRegisteredDeviceInfoId(deviceStatusUpdateDTORequest.getRegisteredDeviceInfoId());
        if (registeredDeviceInfoData != null) {
            registeredDeviceInfoData.setStatus(deviceStatusUpdateDTORequest.getStatus());
            registeredDeviceInfoRepository.save(registeredDeviceInfoData);
            return "updated";
        } else {
            return "not_updated";
        }
    }

}
