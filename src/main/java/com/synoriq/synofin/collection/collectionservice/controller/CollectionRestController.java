package com.synoriq.synofin.collection.collectionservice.controller;

import com.synoriq.synofin.collection.collectionservice.common.errorcode.ErrorCode;
import com.synoriq.synofin.collection.collectionservice.rest.request.AdditionalContactDetailsDtoRequest;
import com.synoriq.synofin.collection.collectionservice.rest.request.DeviceStatusUpdateDTORequest;
import com.synoriq.synofin.collection.collectionservice.rest.request.RegisteredDeviceInfoDtoRequest;
import com.synoriq.synofin.collection.collectionservice.rest.response.BaseDTOResponse;
import com.synoriq.synofin.collection.collectionservice.service.AdditionalContactDetailsService;
import com.synoriq.synofin.collection.collectionservice.service.AppService;
import com.synoriq.synofin.collection.collectionservice.service.RegisteredDeviceInfoService;
import com.synoriq.synofin.lms.commondto.rest.response.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
//@CheckPermission
@RequestMapping("/v1")
public class CollectionRestController {

    private final RegisteredDeviceInfoService registeredDeviceInfoService;
    private final AppService appService;
    private final AdditionalContactDetailsService additionalContactDetailsService;

    public CollectionRestController(RegisteredDeviceInfoService registeredDeviceInfoService, AppService appService, AdditionalContactDetailsService additionalContactDetailsService) {
        this.registeredDeviceInfoService = registeredDeviceInfoService;
        this.appService = appService;
        this.additionalContactDetailsService = additionalContactDetailsService;
    }
    @GetMapping(value = "/check-update", produces = "application/json")
    public ResponseEntity<Object> checkAppUpdates(@RequestParam("platform") String platform, @RequestParam("version") String version) {

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


    @GetMapping(value = "/users/{userId}/device-info")
    public ResponseEntity<Object> getDeviceInfoByUserId(@PathVariable("userId") Long userId) {

        BaseResponse<Object> baseResponse;
        ResponseEntity<Object> response;
        List<Map<String, Object>> result;
        try {
            result = registeredDeviceInfoService.findDeviceInfoByUserId(userId);
            baseResponse = new BaseResponse<>(result);
            response = new ResponseEntity<>(baseResponse, HttpStatus.OK);
        } catch (Exception e) {
            baseResponse = new BaseResponse<>(ErrorCode.DATA_FETCH_ERROR);
            response = new ResponseEntity<>(baseResponse, HttpStatus.BAD_REQUEST);
        }
        return response;
    }


    @PostMapping(value = "/users/{userId}/device-register")
    public ResponseEntity<Object> createRegisteredDeviceInfo(@RequestBody RegisteredDeviceInfoDtoRequest registeredDeviceInfoDtoRequest, @PathVariable("userId") String userId) {

        BaseDTOResponse<Object> baseResponse;
        ResponseEntity<Object> response = null;

        try {
            BaseDTOResponse<Object>result = registeredDeviceInfoService.createRegisteredDeviceInfo(registeredDeviceInfoDtoRequest, userId);
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

    @PostMapping(value = "/device/device-update")
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
    public ResponseEntity<Object> getAdditionalContactDetailsByLoanId(@PathVariable(value = "loanId") Long loanId) {
        BaseDTOResponse<Object> baseResponse;
        ResponseEntity<Object> response = null;
        List<AdditionalContactDetailsDtoRequest> result;
        try{
            result = additionalContactDetailsService.getAdditionalContactDetailsByLoanId(loanId);
            baseResponse = new BaseDTOResponse<>(result);
            response = new ResponseEntity<>(baseResponse, HttpStatus.OK);
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
    public ResponseEntity<Object> getAdditionalContactDetailsById(@PathVariable(value = "id") Long id) {
        BaseDTOResponse<Object> baseResponse;
        ResponseEntity<Object> response = null;
        AdditionalContactDetailsDtoRequest result;

        try{
            result = additionalContactDetailsService.getAdditionalContactDetailsById(id);
            baseResponse = new BaseDTOResponse<>(result);
            response = new ResponseEntity<>(baseResponse, HttpStatus.OK);
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

    @PostMapping(value = "/additional-contact")
    public ResponseEntity<Object> createAdditionalContactDetail(@RequestBody AdditionalContactDetailsDtoRequest additionalContactDetailsDtoRequest) {

        BaseDTOResponse<Object> baseResponse;
        ResponseEntity<Object> response = null;

        try {
            BaseDTOResponse<Object>result = additionalContactDetailsService.createAdditionalContactDetail(additionalContactDetailsDtoRequest);
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
