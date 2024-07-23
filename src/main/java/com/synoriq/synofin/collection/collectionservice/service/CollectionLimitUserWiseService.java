package com.synoriq.synofin.collection.collectionservice.service;

import com.synoriq.synofin.collection.collectionservice.rest.request.CollectionLimitUserWiseDtoRequest;

public interface CollectionLimitUserWiseService {

    public Object getCollectionLimitUserWise(String token, String userId) throws Exception;
    public String createCollectionLimitUserWise(String token, CollectionLimitUserWiseDtoRequest collectionLimitUserWiseDtoRequest) throws Exception;

}
