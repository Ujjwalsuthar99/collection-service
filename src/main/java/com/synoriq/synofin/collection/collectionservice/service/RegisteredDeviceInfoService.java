package com.synoriq.synofin.collection.collectionservice.service;

import com.synoriq.synofin.collection.collectionservice.rest.request.RegisteredDeviceInfoDtoRequest;
import com.synoriq.synofin.collection.collectionservice.rest.response.BaseDTOResponse;
import com.synoriq.synofin.lms.commondto.dto.collection.RegisteredDeviceInfoDTO;

import java.util.List;

public interface RegisteredDeviceInfoService {

    public List<RegisteredDeviceInfoDTO> findDeviceInfoByUserId(Long userId);
    public BaseDTOResponse<Object> createRegisteredDeviceInfo(RegisteredDeviceInfoDtoRequest registeredDeviceInfoDtoRequest, String userId) throws Exception;

}
