package com.synoriq.synofin.collection.collectionservice.service.implementation;


import com.synoriq.synofin.collection.collectionservice.common.EnumSQLConstants;
import com.synoriq.synofin.collection.collectionservice.common.errorcode.ErrorCode;
import com.synoriq.synofin.collection.collectionservice.common.exception.CollectionException;
import com.synoriq.synofin.collection.collectionservice.rest.response.profiledetailsdtos.ProfileDetailResponseDTO;
import com.synoriq.synofin.collection.collectionservice.service.ConsumedApiLogService;
import com.synoriq.synofin.collection.collectionservice.service.ProfileService;
import com.synoriq.synofin.collection.collectionservice.service.UtilityService;
import com.synoriq.synofin.collection.collectionservice.service.utilityservice.HTTPRequestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class ProfileServiceImpl implements ProfileService {
    @Autowired
    private ConsumedApiLogService consumedApiLogService;

    @Autowired
    private UtilityService utilityService;


    @Autowired
    private RestTemplate restTemplate;
    @Override
    public ProfileDetailResponseDTO getProfileDetails(String token, String username) throws CollectionException {
        ProfileDetailResponseDTO res;
        try {
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("Authorization", token);
            httpHeaders.add("Content-Type", "application/json");

            res = HTTPRequestService.<Object, ProfileDetailResponseDTO>builder()
                    .httpMethod(HttpMethod.GET)
                    .url("http://localhost:1102/v1/getProfileDetails?username=" + username)
                    .httpHeaders(httpHeaders)
                    .typeResponseType(ProfileDetailResponseDTO.class)
                    .build().call(restTemplate);

            // creating api logs
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.get_profile_details, null, null, res, "success", null, HttpMethod.GET.name(), "getProfileDetails?username=" + username);
        } catch (Exception e) {
            String errorMessage = e.getMessage();
            String modifiedErrorMessage = utilityService.convertToJSON(errorMessage);
            // creating api logs
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.get_profile_details, null, null, modifiedErrorMessage, "failure", null, HttpMethod.GET.name(), "getProfileDetails?username=" + username);
            ErrorCode errCode = ErrorCode.getErrorCode(1017002);
            throw new CollectionException(errCode, 1017002);
        }

        return res;

    }
}
