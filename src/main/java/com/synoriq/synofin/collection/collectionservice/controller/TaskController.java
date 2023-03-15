package com.synoriq.synofin.collection.collectionservice.controller;

import com.synoriq.synofin.collection.collectionservice.common.errorcode.ErrorCode;
import com.synoriq.synofin.collection.collectionservice.rest.request.FollowUpDtoRequest;
import com.synoriq.synofin.collection.collectionservice.rest.request.taskDetailsDTO.TaskDetailRequestDTO;
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
public class TaskController {

    @Autowired
    TaskService taskService;

    @RequestMapping(value = "users/{userId}/tasks", method = RequestMethod.GET)
    public ResponseEntity<Object> getTaskDetails(@PathVariable("userId") Long userId, @RequestParam(value = "page", defaultValue = DEFAULT_PAGE_NUMBER, required = false) Integer pageNo,
                                                                       @RequestParam(value = "size", defaultValue = DEFAULT_PAGE_SIZE, required = false) Integer pageSize) {

        BaseDTOResponse<Object> baseResponse;
        ResponseEntity<Object> response;

        try {
            baseResponse = taskService.getTaskDetails(userId, pageNo, pageSize);
            response = new ResponseEntity<>(baseResponse, HttpStatus.OK);

            log.info("Get Task Details success | size=[{}]", pageNo);

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


    @RequestMapping(value = "task/detail-summary", method = RequestMethod.POST)
    public ResponseEntity<Object> getTaskDetailByLoanId(@RequestHeader("Authorization") String bearerToken, @RequestBody TaskDetailRequestDTO taskDetailRequestDTO) throws Exception {

        Object baseResponse;
        ResponseEntity<Object> response;

        try {
            baseResponse = taskService.getTaskDetailByLoanId(bearerToken, taskDetailRequestDTO);
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

    @RequestMapping(value = "users/{userId}/tasks/search-tasks", method = RequestMethod.GET)
    public ResponseEntity<Object> getTaskDetailsBySearchKey(@PathVariable("userId") Long userId, @RequestParam(value = "searchKey") String searchKey,
                                                            @RequestParam(value = "page", defaultValue = DEFAULT_PAGE_NUMBER, required = false) Integer pageNo,
                                                            @RequestParam(value = "size", defaultValue = DEFAULT_PAGE_SIZE, required = false) Integer pageSize) throws Exception {

        BaseDTOResponse<Object> baseResponse;
        ResponseEntity<Object> response;

        try {
            baseResponse = taskService.getTaskDetailsBySearchKey(userId, searchKey, pageNo, pageSize);
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
    @RequestMapping(value = "loans", method = RequestMethod.GET)
    public ResponseEntity<Object> getLoanIdsByLoanId(@RequestParam(value = "loanId") Long loanId) {

        BaseDTOResponse<Object> baseResponse;
        ResponseEntity<Object> response;

        try {
            baseResponse = taskService.getLoanIdsByLoanId(loanId);
            response = new ResponseEntity<>(baseResponse, HttpStatus.OK);

            log.info("Get Loan Id {}", loanId);

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

