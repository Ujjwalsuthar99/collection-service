package com.synoriq.synofin.collection.collectionservice.controller;

import com.synoriq.synofin.collection.collectionservice.common.errorcode.ErrorCode;
import com.synoriq.synofin.collection.collectionservice.rest.request.repossessiondtos.RepossessionRequestDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.BaseDTOResponse;
import com.synoriq.synofin.collection.collectionservice.service.RepossessionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/v1")
@EnableTransactionManagement
@Slf4j
public class RepossessionController {

    private final RepossessionService repossessionService;

    public RepossessionController(RepossessionService repossessionService) {
        this.repossessionService = repossessionService;
    }


    @GetMapping(value = "repossession/repossession-activity-data")
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

    @PostMapping(value = "repossession/initiate-repossession")
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

    @PostMapping(value = "repossession/yard-repossession")
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

    @GetMapping(value = "repossession/all-repossession")
    public ResponseEntity<Object> getAllRepossession(@RequestHeader("Authorization") String bearerToken) {

        BaseDTOResponse<Object> baseResponse;
        ResponseEntity<Object> response;

        try {
            baseResponse = repossessionService.getAllRepossession();
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

    @GetMapping(value = "repossession/{repoId}")
    public ResponseEntity<Object> getDataByRepoId(@RequestHeader("Authorization") String bearerToken, @PathVariable("repoId") Long repoId) {

        BaseDTOResponse<Object> baseResponse;
        ResponseEntity<Object> response;

        try {
            baseResponse = repossessionService.getDataByRepoId(bearerToken, repoId);
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

