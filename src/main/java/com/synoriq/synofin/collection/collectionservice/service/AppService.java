package com.synoriq.synofin.collection.collectionservice.service;


import com.synoriq.synofin.collection.collectionservice.rest.response.BaseDTOResponse;

public interface AppService {

    public BaseDTOResponse<Object> checkAppVersion(String platform, String currentVersion) throws Exception;

}
