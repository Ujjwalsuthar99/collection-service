package com.synoriq.synofin.collection.collectionservice.service;

import com.synoriq.synofin.collection.collectionservice.rest.request.DeviceStatusUpdateDTORequest;
import com.synoriq.synofin.collection.collectionservice.rest.request.RegisteredDeviceInfoDtoRequest;
import com.synoriq.synofin.collection.collectionservice.rest.response.BaseDTOResponse;

import java.util.List;
import java.util.Map;

public interface RegisteredDeviceInfoService {

     List<Map<String, Object>> findDeviceInfoByUserId(Long userId);
     BaseDTOResponse<Object> createRegisteredDeviceInfo(RegisteredDeviceInfoDtoRequest registeredDeviceInfoDtoRequest, String userId);
     String deviceStatusUpdate(DeviceStatusUpdateDTORequest deviceStatusUpdateDTORequest);

}
