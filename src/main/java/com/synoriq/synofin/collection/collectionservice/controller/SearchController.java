package com.synoriq.synofin.collection.collectionservice.controller;

import com.synoriq.synofin.collection.collectionservice.common.errorcode.ErrorCode;
import com.synoriq.synofin.collection.collectionservice.rest.request.searchDTOs.SearchDtoRequest;
import com.synoriq.synofin.collection.collectionservice.rest.response.BaseDTOResponse;
import com.synoriq.synofin.collection.collectionservice.service.GlobalSearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;

@RestController
@RequestMapping("/v1")
@EnableTransactionManagement
@Slf4j
public class SearchController {

    @Autowired
    GlobalSearchService searchService;

    @RequestMapping(value = "index/search", method = RequestMethod.POST)
    public ResponseEntity<Object> getReceiptsByUserIdWithDuration(@RequestHeader("Authorization") String bearerToken, @RequestBody SearchDtoRequest searchDtoRequest) throws SQLException {
        BaseDTOResponse<Object> baseResponse;
        Object searchResponse;
        ResponseEntity<Object> response = null;

        try {
            searchResponse = searchService.getLoanDataBySearch(bearerToken, searchDtoRequest);
            response = new ResponseEntity<>(searchResponse, HttpStatus.OK);
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
