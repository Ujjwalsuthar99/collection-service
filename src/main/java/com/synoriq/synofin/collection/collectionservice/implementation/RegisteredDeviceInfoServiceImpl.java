package com.synoriq.synofin.collection.collectionservice.implementation;

import com.synoriq.synofin.collection.collectionservice.entity.RegisteredDeviceInfoEntity;
import com.synoriq.synofin.collection.collectionservice.repository.RegisteredDeviceInfoRepository;
import com.synoriq.synofin.collection.collectionservice.rest.request.DeviceStatusUpdateDTORequest;
import com.synoriq.synofin.collection.collectionservice.rest.request.RegisteredDeviceInfoDtoRequest;
import com.synoriq.synofin.collection.collectionservice.rest.response.BaseDTOResponse;
import com.synoriq.synofin.collection.collectionservice.service.RegisteredDeviceInfoService;
import com.synoriq.synofin.collection.collectionservice.rest.response.RegisteredDeviceInfoDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class RegisteredDeviceInfoServiceImpl implements RegisteredDeviceInfoService {

    @Autowired
    private RegisteredDeviceInfoRepository registeredDeviceInfoRepository;
    @Override
    public List<RegisteredDeviceInfoDTO> findDeviceInfoByUserId(Long userId) {
        List<RegisteredDeviceInfoEntity> registeredDeviceInfoEntityList = registeredDeviceInfoRepository.findDeviceInfoByUserId(userId);
        List<RegisteredDeviceInfoDTO> registeredDeviceInfoDTOList = new ArrayList<>();
        for (RegisteredDeviceInfoEntity registeredDeviceInfoEntity : registeredDeviceInfoEntityList) {
            RegisteredDeviceInfoDTO registeredDeviceInfoEntityDTO = new RegisteredDeviceInfoDTO();
            registeredDeviceInfoEntityDTO.setId(registeredDeviceInfoEntity.getRegisteredDeviceInfoId());
            registeredDeviceInfoEntityDTO.setCreatedDate(registeredDeviceInfoEntity.getCreatedDate());
            registeredDeviceInfoEntityDTO.setCreatedBy(registeredDeviceInfoEntity.getCreatedBy());
            registeredDeviceInfoEntityDTO.setUserId(userId);
            registeredDeviceInfoEntityDTO.setDeleted(registeredDeviceInfoEntity.getDeleted());
            registeredDeviceInfoEntityDTO.setLastAppUsage(registeredDeviceInfoEntity.getLastAppUsage());
            registeredDeviceInfoEntityDTO.setCurrentAppVersion(registeredDeviceInfoEntity.getCurrentAppVersion());
            registeredDeviceInfoEntityDTO.setPlatform(registeredDeviceInfoEntity.getPlatform());
            registeredDeviceInfoEntityDTO.setPlatformVersion(registeredDeviceInfoEntity.getPlatformVersion());
            registeredDeviceInfoEntityDTO.setDeviceUniqueId(registeredDeviceInfoEntity.getDeviceUniqueId());
            registeredDeviceInfoEntityDTO.setDeviceManufacturerName(registeredDeviceInfoEntity.getDeviceManufacturerName());
            registeredDeviceInfoEntityDTO.setDeviceModel(registeredDeviceInfoEntity.getDeviceModel());
            registeredDeviceInfoEntityDTO.setStatus(registeredDeviceInfoEntity.getStatus());
            registeredDeviceInfoDTOList.add(registeredDeviceInfoEntityDTO);
        }
        return registeredDeviceInfoDTOList;
    }
    @Override
    public BaseDTOResponse<Object> createRegisteredDeviceInfo(RegisteredDeviceInfoDtoRequest registeredDeviceInfoDtoRequest, String userId) throws Exception {
        List<RegisteredDeviceInfoEntity> registeredDeviceInfoEntityList = registeredDeviceInfoRepository.findDeviceInfoByUserId(Long.valueOf(userId));
        BaseDTOResponse<Object> response = null;


        if (registeredDeviceInfoEntityList.isEmpty()) {
            // creating a row for the user whc haven't registered yet
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


            for (RegisteredDeviceInfoEntity registeredDeviceInfoEntity : registeredDeviceInfoEntityList) {
                String status = registeredDeviceInfoEntity.getStatus();
                String deviceId = registeredDeviceInfoEntity.getDeviceUniqueId();
                if (status.equals("Active") && deviceId.equals(registeredDeviceInfoDtoRequest.getDeviceUniqueId())) {
                    log.info("Logged In...");
                    response = new BaseDTOResponse<>("Logged In...");
                    break;
                } else if (status.equals(registeredDeviceInfoDtoRequest.getStatus()) && !deviceId.equals(registeredDeviceInfoDtoRequest.getDeviceUniqueId())) {
                    log.info("You are already active with one device!, Please contact IT support for new device registration");
                    throw new Exception("1016027");
//                    response = new BaseDTOResponse<>("You are already active with one device!, Please contact IT support for new device registration");
                } else if (status.equals(registeredDeviceInfoDtoRequest.getStatus()) && deviceId.equals(registeredDeviceInfoDtoRequest.getDeviceUniqueId())) {
                    log.info("No activity since long time!, Please contact IT support for your device activation");
                    throw new Exception("1016026");
//                    response = new BaseDTOResponse<>("No activity since long time!, Please contact IT support for your device activation");
                } else {
                    log.info("No Device Found");
                    response = new BaseDTOResponse<>("No Device Found");
                }
            }
        }
        return response;
    }

    @Override
    public String deviceStatusUpdate(DeviceStatusUpdateDTORequest deviceStatusUpdateDTORequest) {
        RegisteredDeviceInfoEntity registeredDeviceInfoEntityList = registeredDeviceInfoRepository.findByRegisteredDeviceInfoId(deviceStatusUpdateDTORequest.getRegisteredDeviceInfoId());
        if (registeredDeviceInfoEntityList != null) {
            registeredDeviceInfoEntityList.setStatus(deviceStatusUpdateDTORequest.getStatus());
            registeredDeviceInfoRepository.save(registeredDeviceInfoEntityList);
            return "updated";
        } else {
            return "not_updated";
        }
    }

}
