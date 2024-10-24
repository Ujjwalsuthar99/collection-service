package com.synoriq.synofin.collection.collectionservice.service;

import com.synoriq.synofin.collection.collectionservice.common.exception.CollectionException;
import com.synoriq.synofin.collection.collectionservice.common.exception.CustomException;
import com.synoriq.synofin.collection.collectionservice.rest.request.CollectionLimitUserWiseDtoRequest;

public interface CollectionLimitUserWiseService {

     Object getCollectionLimitUserWise(String token, String userId) throws CollectionException;
     String createCollectionLimitUserWise(String token, CollectionLimitUserWiseDtoRequest collectionLimitUserWiseDtoRequest) throws CustomException;

}
