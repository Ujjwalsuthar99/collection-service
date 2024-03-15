package com.synoriq.synofin.collection.collectionservice.controller;

import com.synoriq.synofin.collection.collectionservice.common.errorcode.ErrorCode;
import com.synoriq.synofin.collection.collectionservice.rest.request.followUpDTOs.FollowUpDtoRequest;
import com.synoriq.synofin.collection.collectionservice.rest.request.followUpDTOs.FollowUpStatusRequestDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.BaseDTOResponse;
import com.synoriq.synofin.collection.collectionservice.service.FollowUpService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.synoriq.synofin.collection.collectionservice.common.GlobalVariables.DEFAULT_PAGE_NUMBER;
import static com.synoriq.synofin.collection.collectionservice.common.GlobalVariables.DEFAULT_PAGE_SIZE;

@RestController
@RequestMapping("/v1")
@Slf4j
@Validated
public class FollowupRestController {

    @Autowired
    FollowUpService followUpService;

    @RequestMapping(value = "/followups/{followupId}", method = RequestMethod.GET)
    public ResponseEntity<Object> getFollowupDetailsByFollowupId(@PathVariable("followupId") Long followupId) {

        BaseDTOResponse<Object> baseResponse;
        ResponseEntity<Object> response;
        Map<String, Object> result;

        try {
//            baseResponse = followUpService.getFollowupById(followupId);
            result = followUpService.getFollowupDetailsById(followupId);
            baseResponse = new BaseDTOResponse<Object>(result);
            response = new ResponseEntity<>(baseResponse, HttpStatus.OK);

//            log.info("Get followup details API response success | followup id=[{}]", followupId);

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

    @RequestMapping(value = "loans/{loanId}/followups", method = RequestMethod.GET)
    public ResponseEntity<Object> getFollowupDetailsLoanWiseByDuration(@PathVariable("loanId") Long loanId,@RequestParam(value = "page", defaultValue = DEFAULT_PAGE_NUMBER, required = false) Integer page,
                                                                       @RequestParam(value = "size", defaultValue = DEFAULT_PAGE_SIZE, required = false) Integer size,
                                                                       @RequestParam("fromDate") @DateTimeFormat(pattern = "dd-MM-yyyy") Date fromDate,
                                                                       @RequestParam("toDate") @DateTimeFormat(pattern = "dd-MM-yyyy")Date toDate) {

        BaseDTOResponse<Object> baseResponse;
        ResponseEntity<Object> response;

        try {
            baseResponse = followUpService.getFollowupLoanWiseWithDuration(page, size, loanId, fromDate, toDate);
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
    @RequestMapping(value = "users/{userId}/followups", method = RequestMethod.GET)
    public ResponseEntity<Object> getFollowupDetailsUserWiseByDuration(@PathVariable("userId") Long userId,@RequestParam(value = "page", defaultValue = DEFAULT_PAGE_NUMBER, required = false) Integer page,
                                                                       @RequestParam(value = "size", defaultValue = DEFAULT_PAGE_SIZE, required = false) Integer size,
                                                                       @RequestParam("fromDate") @DateTimeFormat(pattern = "dd-MM-yyyy") Date fromDate,
                                                                       @RequestParam("toDate") @DateTimeFormat(pattern = "dd-MM-yyyy")Date toDate,
                                                                       @RequestParam("type") String type) {

        BaseDTOResponse<Object> baseResponse;
        ResponseEntity<Object> response;

        try {
            baseResponse = followUpService.getFollowupUserWiseWithDuration(page, size, userId, fromDate, toDate, type);
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

    @RequestMapping(value = "/followups", method = RequestMethod.POST)
    public ResponseEntity<Object> createFollowups(@RequestBody FollowUpDtoRequest followUpDtoRequest, @RequestHeader("Authorization") String bearerToken) {

        BaseDTOResponse<Object> baseResponse;
        ResponseEntity<Object> response;

        try {
            baseResponse = followUpService.createFollowup(followUpDtoRequest, bearerToken);
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

    @RequestMapping(value = "/followups/status", method = RequestMethod.PUT)
    public ResponseEntity<Object> updateStatus(@Valid @RequestBody FollowUpStatusRequestDTO followUpDtoRequest, @RequestHeader("Authorization") String bearerToken) throws Exception {

        BaseDTOResponse<Object> baseResponse;
        ResponseEntity<Object> response;

        try {
            baseResponse = followUpService.updateStatus(followUpDtoRequest, bearerToken);
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



}
