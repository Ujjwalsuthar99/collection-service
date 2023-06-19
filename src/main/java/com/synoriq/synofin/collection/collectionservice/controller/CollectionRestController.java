package com.synoriq.synofin.collection.collectionservice.controller;

import com.synoriq.synofin.collection.collectionservice.common.errorcode.ErrorCode;
import com.synoriq.synofin.collection.collectionservice.rest.request.AdditionalContactDetailsDtoRequest;
import com.synoriq.synofin.collection.collectionservice.rest.request.DeviceStatusUpdateDTORequest;
import com.synoriq.synofin.collection.collectionservice.rest.request.RegisteredDeviceInfoDtoRequest;
import com.synoriq.synofin.collection.collectionservice.rest.response.BaseDTOResponse;
import com.synoriq.synofin.collection.collectionservice.service.AdditionalContactDetailsService;
import com.synoriq.synofin.collection.collectionservice.service.AppService;
import com.synoriq.synofin.collection.collectionservice.service.RegisteredDeviceInfoService;
import com.synoriq.synofin.collection.collectionservice.rest.response.RegisteredDeviceInfoDTO;
import com.synoriq.synofin.lms.commondto.rest.response.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/v1")
public class CollectionRestController {

    @Autowired
    RegisteredDeviceInfoService registeredDeviceInfoService;

    @Autowired
    AppService appService;

    @Autowired
    AdditionalContactDetailsService additionalContactDetailsService;

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
//        log.info("my request body {}", registeredDeviceInfoDtoRequest);

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

    @RequestMapping(value = "/device/device-update", method = RequestMethod.POST)
    public ResponseEntity<Object> deviceStatusUpdate(@RequestBody DeviceStatusUpdateDTORequest deviceStatusUpdateDTORequest) {

        BaseDTOResponse<Object> baseResponse;
        ResponseEntity<Object> response = null;
        Object result;

        try {
            result = registeredDeviceInfoService.deviceStatusUpdate(deviceStatusUpdateDTORequest);
            baseResponse = new BaseDTOResponse<>(result);
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


    @GetMapping(value = "/loans/{loanId}/additional-contacts")
    public ResponseEntity<Object> getAdditionalContactDetailsByLoanId(@PathVariable(value = "loanId") Long loanId) throws SQLException {
        BaseDTOResponse<Object> baseResponse;
        ResponseEntity<Object> response = null;
        List<AdditionalContactDetailsDtoRequest> result;
        try{
            result = additionalContactDetailsService.getAdditionalContactDetailsByLoanId(loanId);
            baseResponse = new BaseDTOResponse<>(result);
            response = new ResponseEntity<>(baseResponse, HttpStatus.OK);
            log.info("Get Additional Contact Details By Loan Id API response success | loanId=[{}]", loanId);
        } catch (Exception e){
            if (com.synoriq.synofin.collection.collectionservice.common.errorcode.ErrorCode.getErrorCode(Integer.valueOf(e.getMessage())) != null) {
                baseResponse = new BaseDTOResponse<>(com.synoriq.synofin.collection.collectionservice.common.errorcode.ErrorCode.getErrorCode(Integer.valueOf(e.getMessage())));
            } else {
                baseResponse = new BaseDTOResponse<>(ErrorCode.DATA_FETCH_ERROR);
            }
            response = new ResponseEntity<>(baseResponse, HttpStatus.BAD_REQUEST);
        }
        return response;
    }

    @GetMapping(value = "/additional-contacts/{id}")
    public ResponseEntity<Object> getAdditionalContactDetailsById(@PathVariable(value = "id") Long id) throws SQLException {
        BaseDTOResponse<Object> baseResponse;
        ResponseEntity<Object> response = null;
        AdditionalContactDetailsDtoRequest result;

        try{
            result = additionalContactDetailsService.getAdditionalContactDetailsById(id);
            baseResponse = new BaseDTOResponse<>(result);
            response = new ResponseEntity<>(baseResponse, HttpStatus.OK);
            log.info("Get Additional Contact Details By Id API response success | loanId=[{}]", id);
        } catch (Exception e){
            if (com.synoriq.synofin.collection.collectionservice.common.errorcode.ErrorCode.getErrorCode(Integer.valueOf(e.getMessage())) != null) {
                baseResponse = new BaseDTOResponse<>(com.synoriq.synofin.collection.collectionservice.common.errorcode.ErrorCode.getErrorCode(Integer.valueOf(e.getMessage())));
            } else {
                baseResponse = new BaseDTOResponse<>(ErrorCode.DATA_FETCH_ERROR);
            }
            response = new ResponseEntity<>(baseResponse, HttpStatus.BAD_REQUEST);
        }
        return response;
    }

    @RequestMapping(value = "/additional-contact", method = RequestMethod.POST)
    public ResponseEntity<Object> createAdditionalContactDetail(@RequestBody AdditionalContactDetailsDtoRequest additionalContactDetailsDtoRequest) {
        log.info("my request body {}", additionalContactDetailsDtoRequest);

        BaseDTOResponse<Object> baseResponse;
        ResponseEntity<Object> response = null;

        try {
            BaseDTOResponse result = additionalContactDetailsService.createAdditionalContactDetail(additionalContactDetailsDtoRequest);
            baseResponse = new BaseDTOResponse<>(result.getData());
            response = new ResponseEntity<>(baseResponse, HttpStatus.OK);

        } catch (Exception e) {
            if (com.synoriq.synofin.collection.collectionservice.common.errorcode.ErrorCode.getErrorCode(Integer.valueOf(e.getMessage())) != null) {
                baseResponse = new BaseDTOResponse<>(com.synoriq.synofin.collection.collectionservice.common.errorcode.ErrorCode.getErrorCode(Integer.valueOf(e.getMessage())));
            } else {
                baseResponse = new BaseDTOResponse<>(ErrorCode.DATA_SAVE_ERROR);
            }
            response = new ResponseEntity<>(baseResponse, HttpStatus.BAD_REQUEST);
        }

        return response;

    }


}
