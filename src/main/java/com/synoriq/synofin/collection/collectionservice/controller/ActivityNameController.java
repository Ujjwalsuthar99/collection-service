package com.synoriq.synofin.collection.collectionservice.controller;
import com.synoriq.synofin.collection.collectionservice.rest.request.CollectionConfigurationDtoRequest;
import com.synoriq.synofin.collection.collectionservice.service.ActivityNameService;
import com.synoriq.synofin.collection.collectionservice.rest.request.searchDTOs.SearchDtoRequest;
import com.synoriq.synofin.collection.collectionservice.rest.response.BaseDTOResponse;
import com.synoriq.synofin.collection.collectionservice.common.errorcode.ErrorCode;
import com.synoriq.synofin.collection.collectionservice.service.CollectionConfigurationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
@RestController
@RequestMapping("/v1")
@EnableTransactionManagement
@Slf4j
public class ActivityNameController {
    @Autowired
    ActivityNameService activityNameService;

    @RequestMapping(value = "/activity-name-masters", method = RequestMethod.GET)
    public ResponseEntity<Object> getActivityDetails() throws SQLException {
        BaseDTOResponse<Object> baseResponse;
        ResponseEntity<Object> response = null;
        Object result;

        try {
            result = activityNameService.getActivityDetails();
            response = new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
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
