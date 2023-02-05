package com.synoriq.synofin.collection.collectionservice.controller;

import com.synoriq.synofin.collection.collectionservice.common.errorcode.ErrorCode;
import com.synoriq.synofin.collection.collectionservice.rest.request.CollectionActivityLogRequest;
import com.synoriq.synofin.collection.collectionservice.rest.response.BaseDTOResponse;
import com.synoriq.synofin.collection.collectionservice.service.ActivityLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

import static com.synoriq.synofin.collection.collectionservice.common.GlobalVariables.*;

@RestController
@RequestMapping("/v1")
@Slf4j
public class ActivityLogRestController {

    @Autowired
    ActivityLogService activityLogService;

    @RequestMapping(value = "/activity-logs/{id}", method = RequestMethod.GET)
    public ResponseEntity<Object> getActivityLogsById(@PathVariable("id") Long id) {

        BaseDTOResponse<Object> baseResponse;
        ResponseEntity<Object> response;

        try {

            baseResponse = activityLogService.getActivityLogsById(id);
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

    @RequestMapping(value = "/users/{userId}/activity-logs/", method = RequestMethod.GET)
    public ResponseEntity<Object> getActivityLogsByUserIdWithDuration(@PathVariable("userId") Long userId,
                                                                      @RequestParam(value = "page", defaultValue = DEFAULT_PAGE_NUMBER, required = false) Integer page,
                                                                      @RequestParam(value = "size", defaultValue = DEFAULT_PAGE_SIZE, required = false) Integer size,
                                                                      @RequestParam("fromDate") @DateTimeFormat(pattern = "dd-MM-yyyy")Date fromDate,
                                                                      @RequestParam("toDate") @DateTimeFormat(pattern = "dd-MM-yyyy")Date toDate) {

        BaseDTOResponse<Object> baseResponse;
        ResponseEntity<Object> response;

        try {

            baseResponse = activityLogService.getActivityLogsByUserIdWithDuration(page, size, userId, fromDate, toDate);
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

    @RequestMapping(value = "/loans/{loanId}/activity-logs", method = RequestMethod.GET)
    public ResponseEntity<Object> getActivityLogsByLoanIdWIthDuration(@PathVariable("loanId") Long loanId,
                                                                      @RequestParam(value = "page",defaultValue = DEFAULT_PAGE_NUMBER, required = false) Integer page,
                                                                      @RequestParam(value = "size",defaultValue = DEFAULT_PAGE_SIZE, required = false) Integer size,
                                                                      @RequestParam("fromDate") @DateTimeFormat(pattern = "dd-MM-yyyy")Date fromDate,
                                                                      @RequestParam("toDate") @DateTimeFormat(pattern = "dd-MM-yyyy")Date toDate) {
        BaseDTOResponse<Object> baseResponse;
        ResponseEntity<Object> response;

        try {
            baseResponse = activityLogService.getActivityLogsByLoanIdWithDuration(page, size, loanId, fromDate, toDate);
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

    @RequestMapping(value = "/activity-logs", method = RequestMethod.POST)
    public ResponseEntity<Object> createActivityLogs(@RequestBody CollectionActivityLogRequest collectionActivityLogRequest) {

        BaseDTOResponse<Object> baseResponse;
        ResponseEntity<Object> response;

        try {

            baseResponse = activityLogService.createActivityLogs(collectionActivityLogRequest);
            response = new ResponseEntity<>(baseResponse, HttpStatus.OK);

        } catch (Exception e) {

            baseResponse = new BaseDTOResponse<Object>(ErrorCode.DATA_SAVE_ERROR);
            response = new ResponseEntity<>(baseResponse, HttpStatus.BAD_REQUEST);
        }

        return response;
    }



}
