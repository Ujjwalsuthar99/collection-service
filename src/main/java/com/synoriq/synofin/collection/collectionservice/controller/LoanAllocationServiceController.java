package com.synoriq.synofin.collection.collectionservice.controller;

import com.synoriq.synofin.collection.collectionservice.common.errorcode.ErrorCode;
import com.synoriq.synofin.collection.collectionservice.entity.LoanAllocationEntity;
import com.synoriq.synofin.collection.collectionservice.rest.request.loanAllocationDTOs.LoanAllocationDtoRequest;
import com.synoriq.synofin.collection.collectionservice.rest.request.loanAllocationDTOs.LoanAllocationMultiUsersDtoRequest;
import com.synoriq.synofin.collection.collectionservice.rest.response.BaseDTOResponse;
import com.synoriq.synofin.collection.collectionservice.service.LoanAllocationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/v1")
@EnableTransactionManagement
public class LoanAllocationServiceController {

    @Autowired
    LoanAllocationService loanAllocationService;


    @RequestMapping(value = "/loan-allocation/create", method = RequestMethod.POST)
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

    @RequestMapping(value = "/loan-allocation/multi-users/create", method = RequestMethod.POST)
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



    @RequestMapping(value = "/users/{allocatedToUserId}/loans", method = RequestMethod.GET)
    public ResponseEntity<Object> getLoansByAllocatedToUserId(@PathVariable("allocatedToUserId") Long allocatedToUserId) throws SQLException {

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
    @RequestMapping(value = "/loan-allocation/loans/{loanId}", method = RequestMethod.GET)
    public ResponseEntity<Object> getAllocatedUsersByLoanId(@PathVariable("loanId") Long loanId) throws SQLException {

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

    @RequestMapping(value = "/loan-allocation/delete", method = RequestMethod.GET)
    public ResponseEntity<Object> deleteAllAllocatedLoans(@RequestParam("fromDate") @DateTimeFormat(pattern = "dd-MM-yyyy")Date fromDate,
                                                          @RequestParam("toDate") @DateTimeFormat(pattern = "dd-MM-yyyy")Date toDate) throws SQLException {

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






