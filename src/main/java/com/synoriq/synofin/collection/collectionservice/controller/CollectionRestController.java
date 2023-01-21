package com.synoriq.synofin.collection.collectionservice.controller;

import com.synoriq.synofin.collection.collectionservice.service.FollowUpService;
import com.synoriq.synofin.lms.commondto.dto.collection.FollowUpDTO;
import com.synoriq.synofin.lms.commondto.rest.constants.ErrorCode;
import com.synoriq.synofin.lms.commondto.rest.response.BaseResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Slf4j
@AllArgsConstructor
@Controller
public class CollectionRestController {
    private final FollowUpService followUpService;

    @RequestMapping(value = "/getFollowUpDetails")
    public ResponseEntity<Object> getFollowUpByLoanId(@RequestParam("loanId") Long loanId) {
        BaseResponse<Object> baseResponse;
        ResponseEntity<Object> response = null;
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
}
