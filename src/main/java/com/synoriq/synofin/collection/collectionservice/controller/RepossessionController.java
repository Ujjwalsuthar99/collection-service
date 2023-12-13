package com.synoriq.synofin.collection.collectionservice.controller;

import com.synoriq.synofin.collection.collectionservice.common.errorcode.ErrorCode;
import com.synoriq.synofin.collection.collectionservice.rest.request.repossessionDTOs.RepossessionRequestDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.BaseDTOResponse;
import com.synoriq.synofin.collection.collectionservice.service.RepossessionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/v1")
@EnableTransactionManagement
@Slf4j
public class RepossessionController {

    @Autowired
    private RepossessionService repossessionService;



    @RequestMapping(value = "repossession/repossession-activity-data", method = RequestMethod.GET)
    public ResponseEntity<Object> getRepossession(@RequestHeader("Authorization") String bearerToken, @RequestParam("loan_id") Long loanId) {

        BaseDTOResponse<Object> baseResponse;
        ResponseEntity<Object> response;

        try {
            baseResponse = repossessionService.getRepossessionData(loanId);
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

    @RequestMapping(value = "repossession/initiate-repossession", method = RequestMethod.POST)
    public ResponseEntity<Object> initiateRepossession(@RequestHeader("Authorization") String bearerToken, @RequestBody RepossessionRequestDTO requestBody) {

        BaseDTOResponse<Object> baseResponse;
        ResponseEntity<Object> response;

        try {
            baseResponse = repossessionService.initiateRepossession(bearerToken, requestBody);
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

    @RequestMapping(value = "repossession/yard-repossession", method = RequestMethod.POST)
    public ResponseEntity<Object> yardRepossession(@RequestHeader("Authorization") String bearerToken, @RequestBody RepossessionRequestDTO requestBody) {

        BaseDTOResponse<Object> baseResponse;
        ResponseEntity<Object> response;

        try {
            baseResponse = repossessionService.yardRepossession(bearerToken, requestBody);
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

