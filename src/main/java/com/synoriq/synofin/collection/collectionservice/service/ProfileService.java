package com.synoriq.synofin.collection.collectionservice.service;


import com.synoriq.synofin.collection.collectionservice.rest.response.BaseDTOResponse;
import com.synoriq.synofin.collection.collectionservice.rest.response.ProfileDetailResponseDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.TaskDetailResponseDTOs.TaskDetailDTOResponse;
import com.synoriq.synofin.collection.collectionservice.rest.response.TokenDTOResponse;
import com.synoriq.synofin.collection.collectionservice.service.utilityservice.HTTPRequestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ProfileService {

    public ProfileDetailResponseDTO getProfileDetails(String token, String username) throws Exception {
        ProfileDetailResponseDTO res = new ProfileDetailResponseDTO();
        BaseDTOResponse<Object> baseDTOResponse = null;
        try {
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("Authorization", token);
            httpHeaders.add("Content-Type", "application/json");

            res = HTTPRequestService.<Object, ProfileDetailResponseDTO>builder()
                    .httpMethod(HttpMethod.GET)
                    .url("http://localhost:1102/v1/getProfileDetails?username=" + username)
                    .httpHeaders(httpHeaders)
                    .typeResponseType(ProfileDetailResponseDTO.class)
                    .build().call();


            log.info("profile Response {}", res);
        } catch (Exception e) {
            throw new Exception("1017002");
        }

        return res;

    }
}
