package com.synoriq.synofin.collection.collectionservice.service;

import com.synoriq.synofin.collection.collectionservice.common.errorcode.ErrorCode;
import com.synoriq.synofin.collection.collectionservice.common.exception.CollectionException;
import com.synoriq.synofin.collection.collectionservice.constant.PlatformTypeEnum;
import com.synoriq.synofin.collection.collectionservice.repository.CollectionConfigurationsRepository;
import com.synoriq.synofin.collection.collectionservice.rest.response.CheckAppUpdateResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.synoriq.synofin.collection.collectionservice.common.GlobalVariables.CURRENT_APP_VERSION_ANDROID;
import static com.synoriq.synofin.collection.collectionservice.common.GlobalVariables.FORCE_APP_UPDATE_VERSION_ANDROID;

@Service
@Slf4j
public class AppService {


    @Autowired
    CollectionConfigurationsRepository collectionConfigurationsRepository;


    public CheckAppUpdateResponse checkAppVersion(String platform, String currentVersion) {

        log.info(platform);
        log.info(currentVersion);
        if ((platform != null) && (!platform.isBlank()) && (currentVersion != null) && (!currentVersion.isBlank())) {


            DefaultArtifactVersion currentAppVersion = new DefaultArtifactVersion(currentVersion);

            DefaultArtifactVersion forceAppUpdateVersion = new DefaultArtifactVersion(collectionConfigurationsRepository.
                    findConfigurationValueByConfigurationName(FORCE_APP_UPDATE_VERSION_ANDROID));

            DefaultArtifactVersion currentAppUpdateVersion = new DefaultArtifactVersion(collectionConfigurationsRepository.
                    findConfigurationValueByConfigurationName(CURRENT_APP_VERSION_ANDROID));

            boolean isUpdate;
            boolean isForceUpdate;


            String platformType = platform.toLowerCase();
            if (platformType.equals(PlatformTypeEnum.ANDROID.getPlatformType())) {

                if (currentAppVersion.compareTo(forceAppUpdateVersion) < 0) {

                    isUpdate = true;
                    isForceUpdate = true;

                } else if ((currentAppVersion.compareTo(currentAppUpdateVersion) < 0) &&
                        (currentAppUpdateVersion.compareTo(forceAppUpdateVersion) > 0)) {

                    isUpdate = true;
                    isForceUpdate = false;

                } else {

                    isUpdate = false;
                    isForceUpdate = false;

                }
            } else {

                log.error(ErrorCode.CONFIGURATION_NOT_FOUND.getResponseMessage());
                throw new CollectionException(ErrorCode.CONFIGURATION_NOT_FOUND);

            }


                return CheckAppUpdateResponse.builder().
                    currentAppVersion(currentAppVersion.toString()).
                    currentAppUpdateVersion(currentAppUpdateVersion.toString()).
                    forceAppUpdateVersion(forceAppUpdateVersion.toString())
                    .isUpdate(isUpdate)
                    .isForceUpdate(isForceUpdate).build();


        }
        else{
            log.error(ErrorCode.REQUESTED_PARAM_CANNOT_BE_NULL.getResponseMessage());
            throw new CollectionException(ErrorCode.REQUESTED_PARAM_CANNOT_BE_NULL);
        }
    }
}
