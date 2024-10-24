package com.synoriq.synofin.collection.collectionservice.service;


import com.synoriq.synofin.collection.collectionservice.common.exception.CustomException;
import com.synoriq.synofin.collection.collectionservice.rest.response.BaseDTOResponse;

public interface AppService {

     BaseDTOResponse<Object> checkAppVersion(String platform, String currentVersion) throws CustomException;

}
