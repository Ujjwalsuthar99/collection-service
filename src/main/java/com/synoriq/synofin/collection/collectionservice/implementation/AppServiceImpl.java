package com.synoriq.synofin.collection.collectionservice.implementation;

import com.synoriq.synofin.collection.collectionservice.common.errorcode.ErrorCode;
import com.synoriq.synofin.collection.collectionservice.common.exception.CollectionException;
import com.synoriq.synofin.collection.collectionservice.constant.PlatformTypeEnum;
import com.synoriq.synofin.collection.collectionservice.repository.CollectionConfigurationsRepository;
import com.synoriq.synofin.collection.collectionservice.rest.response.BaseDTOResponse;
import com.synoriq.synofin.collection.collectionservice.rest.response.UtilsDTOs.CheckAppUpdateResponseDTO;
import com.synoriq.synofin.collection.collectionservice.service.AppService;
import lombok.extern.slf4j.Slf4j;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.synoriq.synofin.collection.collectionservice.common.GlobalVariables.CURRENT_APP_VERSION_ANDROID;
import static com.synoriq.synofin.collection.collectionservice.common.GlobalVariables.FORCE_APP_UPDATE_VERSION_ANDROID;

@Service
@Slf4j
public class AppServiceImpl implements AppService {
    @Autowired
    CollectionConfigurationsRepository collectionConfigurationsRepository;

    @Override
    public BaseDTOResponse<Object> checkAppVersion(String platform, String currentVersion) throws Exception {

        BaseDTOResponse<Object> response;
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
                } else if ((currentAppVersion.compareTo(currentAppUpdateVersion) < 0) && (currentAppUpdateVersion.compareTo(forceAppUpdateVersion) > 0)) {
                    isUpdate = true;
                    isForceUpdate = false;
                } else {
                    isUpdate = false;
                    isForceUpdate = false;
                }
            } else {
                log.error(ErrorCode.RECORD_NOT_FOUND.getResponseMessage());
                throw new CollectionException(ErrorCode.RECORD_NOT_FOUND);
            }
            CheckAppUpdateResponseDTO checkAppUpdateResponseDTO =  CheckAppUpdateResponseDTO.builder().
                    currentAppVersion(currentAppVersion.toString()).
                    currentAppUpdateVersion(currentAppUpdateVersion.toString()).
                    forceAppUpdateVersion(forceAppUpdateVersion.toString())
                    .isUpdate(isUpdate)
                    .isForceUpdate(isForceUpdate).build();
            response = new BaseDTOResponse<>(checkAppUpdateResponseDTO);
            return response;
        } else {
            log.error("Requested parameters cannot be null");
            throw new Exception("101809");
        }
    }
}
