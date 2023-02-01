package com.synoriq.synofin.collection.collectionservice.controller;

import com.synoriq.synofin.collection.collectionservice.common.errorcode.ErrorCode;
import com.synoriq.synofin.collection.collectionservice.rest.request.FollowUpDtoRequest;
import com.synoriq.synofin.collection.collectionservice.rest.response.CheckAppUpdateResponse;
import com.synoriq.synofin.collection.collectionservice.service.AppService;
import com.synoriq.synofin.collection.collectionservice.service.FollowUpService;
import com.synoriq.synofin.lms.commondto.dto.collection.FollowUpDTO;
import com.synoriq.synofin.lms.commondto.rest.response.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/v1")
public class CollectionRestController {

    @Autowired
    FollowUpService followUpService;

    @Autowired
    AppService appService;

    @Autowired
    DataSource dataSource;

    @RequestMapping(value = "/getFollowUpDetailsByLoanId", method = RequestMethod.GET)
    public ResponseEntity<Object> getFollowUpByLoanId(@RequestParam("loanId") Long loanId) {
        BaseResponse<Object> baseResponse;
        ResponseEntity<Object> response;
        List<FollowUpDTO> result;
        try {
            result = followUpService.getFollowUpByLoanId(loanId);
            baseResponse = new BaseResponse<>(result);
            response = new ResponseEntity<>(baseResponse, HttpStatus.OK);
            log.info("Get followup details API response success | loanId=[{}]", loanId);
        } catch (Exception e) {
            baseResponse = new BaseResponse<>(ErrorCode.DATA_FETCH_ERROR);
            response = new ResponseEntity<>(baseResponse, HttpStatus.BAD_REQUEST);
        }
        return response;
    }


    @RequestMapping(value = "/getFollowUpDetailsByUserId", method = RequestMethod.GET)
    public ResponseEntity<Object> getFollowUpByUserId(@RequestParam("userId") Long userId) {
        BaseResponse<Object> baseResponse;
        ResponseEntity<Object> response;
        List<FollowUpDTO> result;
        try {
            result = followUpService.getFollowUpByUserId(userId);
            baseResponse = new BaseResponse<>(result);
            response = new ResponseEntity<>(baseResponse, HttpStatus.OK);
            log.info("Get followup details API response success | userId=[{}]", userId);
        } catch (Exception e) {
            baseResponse = new BaseResponse<>(ErrorCode.DATA_FETCH_ERROR);
            response = new ResponseEntity<>(baseResponse, HttpStatus.BAD_REQUEST);
        }
        return response;
    }

    @RequestMapping(value = "/createFollowup", method = RequestMethod.POST)
    public ResponseEntity<Object> createFollowUpLoan(@RequestBody FollowUpDtoRequest followUpDtoRequest){

        BaseResponse<Object> baseResponse;
        ResponseEntity<Object> response = null;

        try{
            followUpService.createFollowUp(followUpDtoRequest);
            log.info(" Followup created for loan id {}", followUpDtoRequest.getLoanId());
            baseResponse = new BaseResponse<>(true);
            response = new ResponseEntity<>(baseResponse, HttpStatus.OK);
        }catch (Exception e){
            baseResponse = new BaseResponse<>(ErrorCode.DATA_FETCH_ERROR);
            response = new ResponseEntity<>(baseResponse, HttpStatus.BAD_REQUEST);
        }

        return response;

    }


    @RequestMapping(value = "/check-update", method = RequestMethod.GET,produces = "application/json")
    public  ResponseEntity<Object> checkAppUpdates(@RequestParam("platform") String platform, @RequestParam("version") String version) throws SQLException {

        BaseResponse<Object> baseResponse;
        ResponseEntity<Object> response = null;
        CheckAppUpdateResponse appUpdateResponse;

//        HashMap<String, CheckAppUpdateResponse> initialResponse = new HashMap<>(){{put("data", new CheckAppUpdateResponse());}};
        try{
            appUpdateResponse = appService.checkAppVersion(platform, version);

            baseResponse = new BaseResponse<>(appUpdateResponse);
            response = new ResponseEntity<>(baseResponse,HttpStatus.OK);

        }catch (Exception ex){
            log.error(ex.getMessage());
            baseResponse = new BaseResponse<>(ErrorCode.DATA_FETCH_ERROR);
            response = new ResponseEntity<>(baseResponse, HttpStatus.BAD_REQUEST);
        }

        return response;

    }





}
