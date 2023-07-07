package com.synoriq.synofin.collection.collectionservice.implementation;

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

    @Autowired
    private RegisteredDeviceInfoRepository registeredDeviceInfoRepository;
    @Override
    public List<Map<String, Object>> findDeviceInfoByUserId(Long userId) {
        List<Map<String, Object>> getDeviceDataByUserIdList;
        getDeviceDataByUserIdList = registeredDeviceInfoRepository.getDeviceDataByUserId(userId);
        return getDeviceDataByUserIdList;
    }
    @Override
    public BaseDTOResponse<Object> createRegisteredDeviceInfo(RegisteredDeviceInfoDtoRequest registeredDeviceInfoDtoRequest, String userId) throws Exception {
        List<RegisteredDeviceInfoEntity> registeredDeviceInfoEntityList = registeredDeviceInfoRepository.findDeviceInfoByUserIdAndByStatus(Long.valueOf(userId), "active");
        BaseDTOResponse<Object> response = null;


        if (registeredDeviceInfoEntityList.isEmpty()) {
            // creating a row for the user who haven't registered yet
            log.info("No device registered on this user Id");
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
            return new BaseDTOResponse<Object>(registeredDeviceInfoEntity);
        } else {
            //update logic for device
            log.info("Device Found");
            response = new BaseDTOResponse<>("Active Device Found");

//            for (RegisteredDeviceInfoEntity registeredDeviceInfoEntity : registeredDeviceInfoEntityList) {
//                String status = registeredDeviceInfoEntity.getStatus();
//                String deviceId = registeredDeviceInfoEntity.getDeviceUniqueId();
//                if (status.equals("Active") && deviceId.equals(registeredDeviceInfoDtoRequest.getDeviceUniqueId())) {
//                    log.info("Logged In...");
//                    response = new BaseDTOResponse<>("Logged In...");
//                    break;
//                } else if (status.equals(registeredDeviceInfoDtoRequest.getStatus()) && !deviceId.equals(registeredDeviceInfoDtoRequest.getDeviceUniqueId())) {
//                    log.info("You are already active with one device!, Please contact IT support for new device registration");
//                    throw new Exception("1016027");
////                    response = new BaseDTOResponse<>("You are already active with one device!, Please contact IT support for new device registration");
//                } else if (status.equals(registeredDeviceInfoDtoRequest.getStatus()) && deviceId.equals(registeredDeviceInfoDtoRequest.getDeviceUniqueId())) {
//                    log.info("No activity since long time!, Please contact IT support for your device activation");
//                    throw new Exception("1016026");
////                    response = new BaseDTOResponse<>("No activity since long time!, Please contact IT support for your device activation");
//                } else {
//                    log.info("No Device Found");
//                    response = new BaseDTOResponse<>("No Device Found");
//                }
//            }
        }
        return response;
    }

    @Override
    public String deviceStatusUpdate(DeviceStatusUpdateDTORequest deviceStatusUpdateDTORequest) {
        List<Map<String, Object>> getDeviceDataByUserIdList = findDeviceInfoByUserId(deviceStatusUpdateDTORequest.getUserId());
        RegisteredDeviceInfoEntity registeredDeviceInfoData;
        for (Map<String, Object> data: getDeviceDataByUserIdList) {
            if (data.get("id") != deviceStatusUpdateDTORequest.getRegisteredDeviceInfoId() && Objects.equals(data.get("status").toString(), "active")) {
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
