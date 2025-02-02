package com.synoriq.synofin.collection.collectionservice.controller;

import com.synoriq.synofin.collection.collectionservice.common.errorcode.ErrorCode;
import com.synoriq.synofin.collection.collectionservice.rest.response.activitylogdtos.ActivityLogBaseResponseDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.BaseDTOResponse;
import com.synoriq.synofin.collection.collectionservice.service.ActivityLogService;
import com.synoriq.synofin.collection.collectionservice.rest.request.CollectionActivityLogDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

import static com.synoriq.synofin.collection.collectionservice.common.GlobalVariables.DEFAULT_PAGE_NUMBER;
import static com.synoriq.synofin.collection.collectionservice.common.GlobalVariables.DEFAULT_PAGE_SIZE;

@RestController
@RequestMapping("/v1")
@Slf4j
public class ActivityLogRestController {

    private final ActivityLogService activityLogService;

    public ActivityLogRestController(ActivityLogService activityLogService) {
        this.activityLogService = activityLogService;
    }

    @GetMapping(value = "/activity-logs/{id}")
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

    @GetMapping(value = "/users/{userId}/activity-logs/")
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

    @GetMapping(value = "/loans/{loanId}/activity-logs")
    public ResponseEntity<Object> getActivityLogsByLoanIdWIthDuration(@PathVariable("loanId") Long loanId,
                                                                      @RequestParam(value = "page",defaultValue = DEFAULT_PAGE_NUMBER, required = false) Integer page,
                                                                      @RequestParam(value = "size",defaultValue = DEFAULT_PAGE_SIZE, required = false) Integer size,
                                                                      @RequestParam("fromDate") @DateTimeFormat(pattern = "dd-MM-yyyy")Date fromDate,
                                                                      @RequestParam("toDate") @DateTimeFormat(pattern = "dd-MM-yyyy")Date toDate,
                                                                      @RequestParam(value = "filterBy", defaultValue = "", required = false) String filterBy) {
        BaseDTOResponse<Object> baseResponse;
        ActivityLogBaseResponseDTO result;
        ResponseEntity<Object> response;

        try {
            result = activityLogService.getActivityLogsByLoanIdWithDuration(page, size, loanId, fromDate, toDate, filterBy);
            baseResponse = new BaseDTOResponse<>(result.getData());
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

    @PostMapping(value = "/activity-logs")
    public ResponseEntity<Object> createActivityLog(@RequestBody CollectionActivityLogDTO collectionActivityLogDTO, @RequestHeader("Authorization") String bearerToken) {
        BaseDTOResponse<Object> baseResponse;
        ResponseEntity<Object> response;
        Long result;

        try {
            result = activityLogService.createActivityLogs(collectionActivityLogDTO, bearerToken);
            baseResponse = new BaseDTOResponse<>(result);
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
