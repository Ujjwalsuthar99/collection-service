package com.synoriq.synofin.collection.collectionservice.controller;

import com.synoriq.synofin.collection.collectionservice.common.errorcode.ErrorCode;
import com.synoriq.synofin.collection.collectionservice.rest.request.AdditionalContactDetailsDtoRequest;
import com.synoriq.synofin.collection.collectionservice.rest.response.BaseDTOResponse;
import com.synoriq.synofin.collection.collectionservice.rest.response.DashboardDTOs.DashboardResponseDTO;
import com.synoriq.synofin.collection.collectionservice.service.DashboardService;
import com.synoriq.synofin.collection.collectionservice.service.ReceiptTransferService;
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

@RestController
@RequestMapping("/v1")
@EnableTransactionManagement
@Slf4j
public class DashboardController {

    @Autowired
    DashboardService dashboardService;

    @RequestMapping(value = "/dashboard-count/{userId}/{userName}", method = RequestMethod.GET)
    public ResponseEntity<Object> getDashboardCountByUserId(@PathVariable(value = "userId") Long userId,@PathVariable(value = "userName") String userName,
                                                            @RequestParam("fromDate") @DateTimeFormat(pattern = "dd-MM-yyyy") String fromDate,
                                                            @RequestParam("toDate") @DateTimeFormat(pattern = "dd-MM-yyyy") String toDate)
                                                            throws SQLException {
        BaseDTOResponse<Object> baseResponse;
        ResponseEntity<Object> response = null;
        DashboardResponseDTO result;

        try{
            result = dashboardService.getDashboardCountByUserId(userId, userName, fromDate, toDate);
            baseResponse = new BaseDTOResponse<>(result);
            response = new ResponseEntity<>(baseResponse, HttpStatus.OK);
        } catch (Exception e){
            if (com.synoriq.synofin.collection.collectionservice.common.errorcode.ErrorCode.getErrorCode(Integer.valueOf(e.getMessage())) != null) {
                baseResponse = new BaseDTOResponse<>(com.synoriq.synofin.collection.collectionservice.common.errorcode.ErrorCode.getErrorCode(Integer.valueOf(e.getMessage())));
            } else {
                baseResponse = new BaseDTOResponse<>(ErrorCode.DATA_FETCH_ERROR);
            }
            response = new ResponseEntity<>(baseResponse, HttpStatus.BAD_REQUEST);
        }
        return response;
    }
}