package com.synoriq.synofin.collection.collectionservice.controller;

import com.synoriq.synofin.collection.collectionservice.common.errorcode.ErrorCode;
import com.synoriq.synofin.collection.collectionservice.common.exception.CollectionException;
import com.synoriq.synofin.collection.collectionservice.rest.request.taskdetailsdto.TaskDetailRequestDTO;
import com.synoriq.synofin.collection.collectionservice.rest.request.taskdetailsdto.TaskFilterRequestDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.BaseDTOResponse;
import com.synoriq.synofin.collection.collectionservice.service.TaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.synoriq.synofin.collection.collectionservice.common.GlobalVariables.DEFAULT_PAGE_NUMBER;
import static com.synoriq.synofin.collection.collectionservice.common.GlobalVariables.DEFAULT_PAGE_SIZE;


@RestController
@RequestMapping("/v1")
@EnableTransactionManagement
@Slf4j
@Validated
public class TaskController {

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }
    private final TaskService taskService;

    @PostMapping(value = "users/{userId}/tasks")
    public ResponseEntity<Object> getTaskDetails(@PathVariable("userId") Long userId, @RequestParam(value = "page", defaultValue = DEFAULT_PAGE_NUMBER, required = false) Integer pageNo,
                                                 @RequestParam(value = "size", defaultValue = DEFAULT_PAGE_SIZE, required = false) Integer pageSize, @RequestBody @Valid TaskFilterRequestDTO taskFilterRequestDTO) {

        BaseDTOResponse<Object> baseResponse;
        ResponseEntity<Object> response;

        try {
            baseResponse = taskService.getTaskDetails(userId, pageNo, pageSize, taskFilterRequestDTO);
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


    @PostMapping(value = "task/detail-summary")
    public ResponseEntity<Object> getTaskDetailByLoanId(@RequestHeader("Authorization") String bearerToken, @RequestBody TaskDetailRequestDTO taskDetailRequestDTO) throws CollectionException {

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

    @GetMapping(value = "users/{userId}/tasks/search-tasks")
    public ResponseEntity<Object> getTaskDetailsBySearchKey(@PathVariable("userId") Long userId, @RequestParam(value = "searchKey") String searchKey,
                                                            @RequestParam(value = "page", defaultValue = DEFAULT_PAGE_NUMBER, required = false) Integer pageNo,
                                                            @RequestParam(value = "size", defaultValue = DEFAULT_PAGE_SIZE, required = false) Integer pageSize) throws CollectionException {

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
    @GetMapping(value = "loans")
    public ResponseEntity<Object> getLoanIdsByLoanId(@RequestParam(value = "loanId") Long loanId) {

        BaseDTOResponse<Object> baseResponse;
        ResponseEntity<Object> response;

        try {
            baseResponse = taskService.getLoanIdsByLoanId(loanId);
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

