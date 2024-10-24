package com.synoriq.synofin.collection.collectionservice.controller;

import com.synoriq.synofin.collection.collectionservice.common.errorcode.ErrorCode;
import com.synoriq.synofin.collection.collectionservice.rest.request.CollectionLimitUserWiseDtoRequest;
import com.synoriq.synofin.collection.collectionservice.rest.response.BaseDTOResponse;
import com.synoriq.synofin.collection.collectionservice.service.CollectionLimitUserWiseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1")
@EnableTransactionManagement
@Slf4j
public class CollectionLimitUserWiseController {

    private final CollectionLimitUserWiseService collectionLimitUserWiseService;

    public CollectionLimitUserWiseController(CollectionLimitUserWiseService collectionLimitUserWiseService) {
        this.collectionLimitUserWiseService = collectionLimitUserWiseService;
    }
    @GetMapping(value = "/getCollectionLimitUserWise")
    public ResponseEntity<Object> getCollectionLimitUserWise(@RequestHeader("Authorization") String bearerToken, @RequestParam("userId") String userId) {
        BaseDTOResponse<Object> baseResponse;
        ResponseEntity<Object> response = null;
        Object result;

        try {
            result = collectionLimitUserWiseService.getCollectionLimitUserWise(bearerToken, userId);
            response = new ResponseEntity<>(result, HttpStatus.OK);
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


    @PostMapping(value = "/createCollectionLimitUserWise")
    public ResponseEntity<Object> createCollectionLimitUserWise(@RequestHeader("Authorization") String bearerToken, @RequestBody CollectionLimitUserWiseDtoRequest collectionLimitUserWiseDtoRequest) {
        BaseDTOResponse<Object> baseResponse;
        ResponseEntity<Object> response = null;
        String result;

        try {
            result = collectionLimitUserWiseService.createCollectionLimitUserWise(bearerToken, collectionLimitUserWiseDtoRequest);
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
