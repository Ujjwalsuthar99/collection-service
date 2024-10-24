package com.synoriq.synofin.collection.collectionservice.controller;

import com.synoriq.synofin.collection.collectionservice.common.errorcode.ErrorCode;
import com.synoriq.synofin.collection.collectionservice.entity.LoanAllocationEntity;
import com.synoriq.synofin.collection.collectionservice.rest.request.loanallocationdtos.LoanAllocationDtoRequest;
import com.synoriq.synofin.collection.collectionservice.rest.request.loanallocationdtos.LoanAllocationMultiUsersDtoRequest;
import com.synoriq.synofin.collection.collectionservice.rest.response.BaseDTOResponse;
import com.synoriq.synofin.collection.collectionservice.service.LoanAllocationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/v1")
@EnableTransactionManagement
public class LoanAllocationServiceController {

    private final LoanAllocationService loanAllocationService;

    public LoanAllocationServiceController(LoanAllocationService loanAllocationService) {
        this.loanAllocationService = loanAllocationService;
    }

    @PostMapping(value = "/loan-allocation/create")
    public ResponseEntity<Object> createLoanAllocationByAllocatedToUserId(@RequestBody LoanAllocationDtoRequest loanAllocationDtoRequest) {

        BaseDTOResponse<Object> baseResponse;
        ResponseEntity<Object> response = null;

        try {
            BaseDTOResponse<Object> result = loanAllocationService.createLoanAllocationByAllocatedToUserId(loanAllocationDtoRequest);
            baseResponse = new BaseDTOResponse<>(result.getData());
            response = new ResponseEntity<>(baseResponse, HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            if (com.synoriq.synofin.collection.collectionservice.common.errorcode.ErrorCode.getErrorCode(Integer.valueOf(e.getMessage())) != null) {
                baseResponse = new BaseDTOResponse<>(com.synoriq.synofin.collection.collectionservice.common.errorcode.ErrorCode.getErrorCode(Integer.valueOf(e.getMessage())));
            } else {
                baseResponse = new BaseDTOResponse<>(ErrorCode.DATA_SAVE_ERROR);
            }
            response = new ResponseEntity<>(baseResponse, HttpStatus.BAD_REQUEST);
        }

        return response;

    }

    @PostMapping(value = "/loan-allocation/multi-users/create")
    public ResponseEntity<Object> createLoanAllocationToMultipleUserId(@RequestBody LoanAllocationMultiUsersDtoRequest loanAllocationMultiUsersDtoRequest) {

        BaseDTOResponse<Object> baseResponse;
        ResponseEntity<Object> response = null;

        try {
            BaseDTOResponse<Object> result = loanAllocationService.createLoanAllocationToMultipleUserId(loanAllocationMultiUsersDtoRequest);
            response = new ResponseEntity<>(result, HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            if (com.synoriq.synofin.collection.collectionservice.common.errorcode.ErrorCode.getErrorCode(Integer.valueOf(e.getMessage())) != null) {
                baseResponse = new BaseDTOResponse<>(com.synoriq.synofin.collection.collectionservice.common.errorcode.ErrorCode.getErrorCode(Integer.valueOf(e.getMessage())));
            } else {
                baseResponse = new BaseDTOResponse<>(ErrorCode.DATA_SAVE_ERROR);
            }
            response = new ResponseEntity<>(baseResponse, HttpStatus.BAD_REQUEST);
        }

        return response;

    }



    @GetMapping(value = "/users/{allocatedToUserId}/loans")
    public ResponseEntity<Object> getLoansByAllocatedToUserId(@PathVariable("allocatedToUserId") Long allocatedToUserId) {

        BaseDTOResponse<Object> baseResponse;
        ResponseEntity<Object> response;
        List<LoanAllocationEntity> result;
        try {
            result = loanAllocationService.getLoansByUserId(allocatedToUserId);
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
    @GetMapping(value = "/loan-allocation/loans/{loanId}")
    public ResponseEntity<Object> getAllocatedUsersByLoanId(@PathVariable("loanId") Long loanId) {

        BaseDTOResponse<Object> baseResponse;
        ResponseEntity<Object> response;
        List<Map<String, Object>> result;
        try {
            result = loanAllocationService.getAllocatedUsersByLoanId(loanId);
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

    @GetMapping(value = "/loan-allocation/delete")
    public ResponseEntity<Object> deleteAllAllocatedLoans(@RequestParam("fromDate") @DateTimeFormat(pattern = "dd-MM-yyyy")Date fromDate,
                                                          @RequestParam("toDate") @DateTimeFormat(pattern = "dd-MM-yyyy")Date toDate) {

        BaseDTOResponse<Object> baseResponse;
        ResponseEntity<Object> response;
        String result;
        try {
            result = loanAllocationService.deleteAllAllocatedLoans(fromDate, toDate);
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


}






