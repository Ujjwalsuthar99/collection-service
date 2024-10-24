package com.synoriq.synofin.collection.collectionservice.service;

import com.synoriq.synofin.collection.collectionservice.common.exception.CollectionException;
import com.synoriq.synofin.collection.collectionservice.rest.response.profiledetailsdtos.ProfileDetailResponseDTO;

public interface ProfileService {

     ProfileDetailResponseDTO getProfileDetails(String token, String username) throws CollectionException;

}
