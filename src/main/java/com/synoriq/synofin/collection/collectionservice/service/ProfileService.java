package com.synoriq.synofin.collection.collectionservice.service;

import com.synoriq.synofin.collection.collectionservice.rest.response.ProfileDetailsDTOs.ProfileDetailResponseDTO;

public interface ProfileService {

    public ProfileDetailResponseDTO getProfileDetails(String token, String username) throws Exception;

}
