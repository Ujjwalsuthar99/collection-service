package com.synoriq.synofin.collection.collectionservice.controller;

import com.synoriq.synofin.collection.collectionservice.rest.request.FollowUpDtoRequest;
import com.synoriq.synofin.collection.collectionservice.rest.request.RegisteredDeviceInfoDtoRequest;
import com.synoriq.synofin.collection.collectionservice.rest.response.BaseDTOResponse;
import com.synoriq.synofin.collection.collectionservice.service.FollowUpService;
import com.synoriq.synofin.collection.collectionservice.service.RegisteredDeviceInfoService;
import com.synoriq.synofin.lms.commondto.dto.collection.FollowUpDTO;
import com.synoriq.synofin.lms.commondto.dto.collection.RegisteredDeviceInfoDTO;
import com.synoriq.synofin.lms.commondto.rest.constants.ErrorCode;
import com.synoriq.synofin.lms.commondto.rest.response.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/v1")
public class CollectionRestController {

    @Autowired
    FollowUpService followUpService;

    @Autowired
    DataSource dataSource;
    @Autowired
    RegisteredDeviceInfoService registeredDeviceInfoService;

    @RequestMapping(value = "/getFollowUpDetailsByLoanId", method = RequestMethod.GET)
    public ResponseEntity<Object> getFollowUpByLoanId(@RequestParam("loanId") Long loanId) {
        BaseResponse<Object> baseResponse;
        ResponseEntity<Object> response;
        List<FollowUpDTO> result;
        try {
            result = followUpService.getFollowUpByLoanId(loanId);
            baseResponse = new BaseResponse<>(result);
            response = new ResponseEntity<>(baseResponse, HttpStatus.OK);
            log.info("Get followup details API response success | loanId=[{}]", loanId);
        } catch (Exception e) {
            baseResponse = new BaseResponse<>(ErrorCode.DATA_FETCH_ERROR);
            response = new ResponseEntity<>(baseResponse, HttpStatus.BAD_REQUEST);
        }
        return response;
    }


    @RequestMapping(value = "/getFollowUpDetailsByUserId", method = RequestMethod.GET)
    public ResponseEntity<Object> getFollowUpByUserId(@RequestParam("userId") Long userId) {
        BaseResponse<Object> baseResponse;
        ResponseEntity<Object> response;
        List<FollowUpDTO> result;
        try {
            result = followUpService.getFollowUpByUserId(userId);
            baseResponse = new BaseResponse<>(result);
            response = new ResponseEntity<>(baseResponse, HttpStatus.OK);
            log.info("Get followup details API response success | userId=[{}]", userId);
        } catch (Exception e) {
            baseResponse = new BaseResponse<>(ErrorCode.DATA_FETCH_ERROR);
            response = new ResponseEntity<>(baseResponse, HttpStatus.BAD_REQUEST);
        }
        return response;
    }

    @RequestMapping(value = "/createFollowup", method = RequestMethod.POST)
    public ResponseEntity<Object> createFollowUpLoan(@RequestBody FollowUpDtoRequest followUpDtoRequest){

        BaseResponse<Object> baseResponse;
        ResponseEntity<Object> response = null;

        try{
            followUpService.createFollowUp(followUpDtoRequest);
            log.info(" Followup created for loan id {}", followUpDtoRequest.getLoanId());
            baseResponse = new BaseResponse<>(true);
            response = new ResponseEntity<>(baseResponse, HttpStatus.OK);
        }catch (Exception e){
            baseResponse = new BaseResponse<>(ErrorCode.DATA_SAVE_ERROR);
            response = new ResponseEntity<>(baseResponse, HttpStatus.BAD_REQUEST);
        }

        return response;

    }

    // General APIs and functions structure

    // On application startup, all basic configurations are loaded in Redis db from database
    // A mechanism should be there to refresh or reload a certain configuration if the same is not there.
    // Proper error handling and error reporting mechanism should be there.
    // A seperate utility service can be made for all REDIS operations.

    @RequestMapping(value = "/checkAppUpdates", method = RequestMethod.GET)
    public void checkAppUpdates(@RequestParam("current_app_version") Long currentAppVersion) {

        // Get the current device version in the argument from front end
        // Load the device configurations from db and place it in Redis
        // Check if the current version is compatible with the required version

        // two fields are maintained in the config with current version and force update version and logic is maintained
        // backend accordingly
    }


    @RequestMapping(value = "/users/{userId}/device-info", method = RequestMethod.GET)
    public ResponseEntity<Object> getDeviceInfoByUserId (@PathVariable("userId") Long userId) throws SQLException {

        BaseResponse<Object> baseResponse;
        ResponseEntity<Object> response;
        List<RegisteredDeviceInfoDTO> result;

        log.info(" URL {}", dataSource.getConnection().getMetaData().getURL());
        try {
            result = registeredDeviceInfoService.findDeviceInfoByUserId(userId);
            baseResponse = new BaseResponse<>(result);
            response = new ResponseEntity<>(baseResponse, HttpStatus.OK);
            log.info("Get followup details API response success | userId=[{}]", userId);
        } catch (Exception e) {
            baseResponse = new BaseResponse<>(ErrorCode.DATA_FETCH_ERROR);
            response = new ResponseEntity<>(baseResponse, HttpStatus.BAD_REQUEST);
        }
        return response;
    }


    @RequestMapping(value = "/users/{userId}/device-register", method = RequestMethod.POST)
    public ResponseEntity<Object> createRegisteredDeviceInfo(@RequestBody RegisteredDeviceInfoDtoRequest registeredDeviceInfoDtoRequest, @PathVariable("userId") String userId){
        log.info("my request body {}", registeredDeviceInfoDtoRequest);

        BaseDTOResponse<Object> baseResponse;
        ResponseEntity<Object> response = null;

        try{
            BaseDTOResponse result = registeredDeviceInfoService.createRegisteredDeviceInfo(registeredDeviceInfoDtoRequest, userId);
            baseResponse = new BaseDTOResponse<>(result.getData());
            response = new ResponseEntity<>(baseResponse, HttpStatus.OK);
        }catch (Exception e){
            if(com.synoriq.synofin.collection.collectionservice.common.errorcode.ErrorCode.getErrorCode(Integer.valueOf(e.getMessage())) != null){
                baseResponse = new BaseDTOResponse<>(com.synoriq.synofin.collection.collectionservice.common.errorcode.ErrorCode.getErrorCode(Integer.valueOf(e.getMessage())));
            }else {
                baseResponse = new BaseDTOResponse<>(ErrorCode.DATA_SAVE_ERROR);
            }
            response = new ResponseEntity<>(baseResponse, HttpStatus.BAD_REQUEST);
        }

        return response;

    }


}
