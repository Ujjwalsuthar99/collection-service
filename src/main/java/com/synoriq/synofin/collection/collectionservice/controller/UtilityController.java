package com.synoriq.synofin.collection.collectionservice.controller;

import com.synoriq.synofin.collection.collectionservice.common.errorcode.ErrorCode;
import com.synoriq.synofin.collection.collectionservice.rest.request.masterDTOs.MasterDtoRequest;
import com.synoriq.synofin.collection.collectionservice.rest.request.uploadImageOnS3.UploadImageOnS3RequestDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.BaseDTOResponse;
import com.synoriq.synofin.collection.collectionservice.rest.response.UploadImageResponseDTO.UploadImageOnS3ResponseDTO;
import com.synoriq.synofin.collection.collectionservice.service.UtilityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.sql.SQLException;

import static com.synoriq.synofin.collection.collectionservice.common.GlobalVariables.*;

@RestController
@RequestMapping("/v1")
@EnableTransactionManagement
@Slf4j
public class UtilityController {

    @Autowired
    UtilityService utilityService;

    @RequestMapping(value = "getMasterType", method = RequestMethod.POST)
    public ResponseEntity<Object> getMasterData(@RequestHeader("Authorization") String bearerToken, @RequestBody MasterDtoRequest masterDtoRequest) throws SQLException {
        BaseDTOResponse<Object> baseResponse;
        Object masterResponse;
        ResponseEntity<Object> response = null;

        try {
            masterResponse = utilityService.getMasterData(bearerToken, masterDtoRequest);
            response = new ResponseEntity<>(masterResponse, HttpStatus.OK);
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
    @RequestMapping(value = "getAllUserData", method = RequestMethod.GET)
    public ResponseEntity<Object> getAllUserDetail(@RequestHeader("Authorization") String bearerToken, @RequestParam(value = "page", defaultValue = DEFAULT_PAGE_NUMBER, required = false) Integer page,
                                                   @RequestParam(value = "size", defaultValue = DEFAULT_PAGE_SIZE, required = false) Integer size,
                                                   @RequestParam(value = "key", defaultValue = "", required = false) String key) throws SQLException {
        BaseDTOResponse<Object> baseResponse;
        Object userResponse;
        ResponseEntity<Object> response = null;
        Object result;

        try {
            result = utilityService.getUserDetail(bearerToken, page, size, key);
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
    @RequestMapping(value = "getContactSupport", method = RequestMethod.GET)
    public ResponseEntity<Object> getContactSupport(@RequestHeader("Authorization") String bearerToken, @RequestParam(value = "keyword") String keyword, @RequestParam(value = "model") String model) throws SQLException {
        BaseDTOResponse<Object> baseResponse;
        Object userResponse;
        ResponseEntity<Object> response = null;
        Object result;

        try {
            result = utilityService.getContactSupport(bearerToken, keyword, model);
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

    @RequestMapping(value = "getBankNameByIFSC", method = RequestMethod.GET)
    public ResponseEntity<Object> getBankNameByIFSC(@RequestParam(value = "keyword") String keyword) throws SQLException {
        BaseDTOResponse<Object> baseResponse;
        ResponseEntity<Object> response = null;
        Object result;

        try {
            result = utilityService.getBankNameByIFSC(keyword);
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


    @RequestMapping(value = "uploadImageOnS3", method = RequestMethod.POST)
    public ResponseEntity<Object> uploadImageOnS3(@RequestHeader("Authorization") String token, @RequestBody UploadImageOnS3RequestDTO uploadImageOnS3RequestDTO, @RequestParam("imageData") MultipartFile imageData) throws SQLException {
        BaseDTOResponse<Object> baseResponse;
        ResponseEntity<Object> response = null;
        UploadImageOnS3ResponseDTO result;

        try {
            result = utilityService.uploadImageOnS3(token, uploadImageOnS3RequestDTO, imageData);
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

}
