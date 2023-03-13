package com.synoriq.synofin.collection.collectionservice.controller;

import com.synoriq.synofin.collection.collectionservice.common.errorcode.ErrorCode;
import com.synoriq.synofin.collection.collectionservice.rest.response.BaseDTOResponse;
import com.synoriq.synofin.collection.collectionservice.service.ProfileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.*;

import static com.synoriq.synofin.collection.collectionservice.common.GlobalVariables.DEFAULT_PAGE_NUMBER;
import static com.synoriq.synofin.collection.collectionservice.common.GlobalVariables.DEFAULT_PAGE_SIZE;



        import com.synoriq.synofin.collection.collectionservice.common.errorcode.ErrorCode;
        import com.synoriq.synofin.collection.collectionservice.rest.response.BaseDTOResponse;
        import com.synoriq.synofin.collection.collectionservice.service.TaskService;
        import lombok.extern.slf4j.Slf4j;
        import org.springframework.beans.factory.annotation.Autowired;
        import org.springframework.format.annotation.DateTimeFormat;
        import org.springframework.http.HttpStatus;
        import org.springframework.http.ResponseEntity;
        import org.springframework.transaction.annotation.EnableTransactionManagement;
        import org.springframework.web.bind.annotation.*;

        import java.util.Date;

        import static com.synoriq.synofin.collection.collectionservice.common.GlobalVariables.DEFAULT_PAGE_NUMBER;
        import static com.synoriq.synofin.collection.collectionservice.common.GlobalVariables.DEFAULT_PAGE_SIZE;


@RestController
@RequestMapping("/v1")
@EnableTransactionManagement
@Slf4j
public class ProfileController {

    @Autowired

    ProfileService profileService;

    @RequestMapping(value = "users/profile", method = RequestMethod.GET)
    public ResponseEntity<Object> getProfileDetails(@RequestHeader("Authorization") String bearerToken, @RequestParam(value = "username") String username) {

        BaseDTOResponse<Object> baseResponse;
        Object profileDetailResponse;
        ResponseEntity<Object> response;

        try {
            profileDetailResponse = profileService.getProfileDetails(bearerToken, username);
            response = new ResponseEntity<>(profileDetailResponse, HttpStatus.OK);

            log.info("Get Profile Details success", username);

        } catch (Exception e) {
            if (ErrorCode.getErrorCode(Integer.valueOf(e.getMessage())) != null) {
                baseResponse = new BaseDTOResponse<>(ErrorCode.getErrorCode(Integer.valueOf(e.getMessage())));
            } else {
                baseResponse = new BaseDTOResponse<>(ErrorCode.DATA_FETCH_ERROR);
            }
            response = new ResponseEntity<>(baseResponse, HttpStatus.BAD_REQUEST);
        }
        return response;
    }
}

