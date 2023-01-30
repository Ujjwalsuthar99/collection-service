package com.synoriq.synofin.collection.collectionservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AppService {

    //return type boolean
    public void checkAppVersion(Long userAppVersion){

        // if userAppVersion is less than forceUpdateVersion then function should return true and user needs to update
        // the app accordingly

        // else if the user app version is less than the current version then user may skip the update and the function
        // should return true

        // else it will return false and user don't need to update the app
    }
}
