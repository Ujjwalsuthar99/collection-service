package com.synoriq.synofin.collection.collectionservice.controller;

import com.synoriq.synofin.collection.collectionservice.common.errorcode.ErrorCode;
import com.synoriq.synofin.collection.collectionservice.rest.request.AdditionalContactDetailsDtoRequest;
import com.synoriq.synofin.collection.collectionservice.rest.request.FollowUpDtoRequest;
import com.synoriq.synofin.collection.collectionservice.rest.request.RegisteredDeviceInfoDtoRequest;
import com.synoriq.synofin.collection.collectionservice.rest.response.BaseDTOResponse;
import com.synoriq.synofin.collection.collectionservice.rest.response.CheckAppUpdateResponse;
import com.synoriq.synofin.collection.collectionservice.service.AdditionalContactDetailsService;
import com.synoriq.synofin.collection.collectionservice.service.AppService;
import com.synoriq.synofin.collection.collectionservice.service.FollowUpService;
import com.synoriq.synofin.collection.collectionservice.service.RegisteredDeviceInfoService;
import com.synoriq.synofin.lms.commondto.dto.collection.FollowUpDTO;
import com.synoriq.synofin.lms.commondto.dto.collection.RegisteredDeviceInfoDTO;
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
    RegisteredDeviceInfoService registeredDeviceInfoService;

    @Autowired
    AppService appService;

    @Autowired
    AdditionalContactDetailsService additionalContactDetailsService;

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
    public ResponseEntity<Object> createFollowUpLoan(@RequestBody FollowUpDtoRequest followUpDtoRequest) {

        BaseResponse<Object> baseResponse;
        ResponseEntity<Object> response = null;

        try {
            followUpService.createFollowUp(followUpDtoRequest);
            log.info(" Followup created for loan id {}", followUpDtoRequest.getLoanId());
            baseResponse = new BaseResponse<>(true);
            response = new ResponseEntity<>(baseResponse, HttpStatus.OK);
        } catch (Exception e) {
            baseResponse = new BaseResponse<>(ErrorCode.DATA_FETCH_ERROR);
            response = new ResponseEntity<>(baseResponse, HttpStatus.BAD_REQUEST);
        }

        return response;

    }


    @RequestMapping(value = "/check-update", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<Object> checkAppUpdates(@RequestParam("platform") String platform, @RequestParam("version") String version) throws SQLException {

        BaseDTOResponse<Object> baseResponse;
        ResponseEntity<Object> response;

        try {
            baseResponse = appService.checkAppVersion(platform, version);
            response = new ResponseEntity<>(baseResponse, HttpStatus.OK);

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


    @RequestMapping(value = "/users/{userId}/device-info", method = RequestMethod.GET)
    public ResponseEntity<Object> getDeviceInfoByUserId(@PathVariable("userId") Long userId) throws SQLException {

        BaseResponse<Object> baseResponse;
        ResponseEntity<Object> response;
        List<RegisteredDeviceInfoDTO> result;
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
    public ResponseEntity<Object> createRegisteredDeviceInfo(@RequestBody RegisteredDeviceInfoDtoRequest registeredDeviceInfoDtoRequest, @PathVariable("userId") String userId) {
        log.info("my request body {}", registeredDeviceInfoDtoRequest);

        BaseDTOResponse<Object> baseResponse;
        ResponseEntity<Object> response = null;

        try {
            BaseDTOResponse result = registeredDeviceInfoService.createRegisteredDeviceInfo(registeredDeviceInfoDtoRequest, userId);
            baseResponse = new BaseDTOResponse<>(result.getData());
            response = new ResponseEntity<>(baseResponse, HttpStatus.OK);

        } catch (Exception e) {
            if (ErrorCode.getErrorCode(Integer.valueOf(e.getMessage())) != null) {
                baseResponse = new BaseDTOResponse<>(ErrorCode.getErrorCode(Integer.valueOf(e.getMessage())));
            } else {
                baseResponse = new BaseDTOResponse<>(ErrorCode.DATA_SAVE_ERROR);
            }
            response = new ResponseEntity<>(baseResponse, HttpStatus.BAD_REQUEST);
        }

        return response;

    }


    //    @GetMapping(value = "/loans/{loanId}/additional-contacts")
//    public ResponseEntity<List<AdditionalContactDetailsEntity>> getAdditionalContactDetailsByLoanId(@PathVariable(value = "loanId") Long loanId) {
//        return additionalContactService.getAdditionalContactDetailsByLoanId(loanId);
//    }

    @GetMapping(value = "/loans/{loanId}/additional-contacts")
    public ResponseEntity<List<AdditionalContactDetailsDtoRequest>> getAdditionalContactDetailsByLoanId(@PathVariable(value = "loanId") Long loanId) {
        return additionalContactDetailsService.getAdditionalContactDetailsByLoanId(loanId);
    }


}
