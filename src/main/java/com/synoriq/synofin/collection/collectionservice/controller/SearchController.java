package com.synoriq.synofin.collection.collectionservice.controller;

import com.synoriq.synofin.collection.collectionservice.common.errorcode.ErrorCode;
import com.synoriq.synofin.collection.collectionservice.rest.request.searchdtos.SearchDtoRequest;
import com.synoriq.synofin.collection.collectionservice.rest.response.BaseDTOResponse;
import com.synoriq.synofin.collection.collectionservice.service.GlobalSearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@RestController
@RequestMapping("/v1")
@EnableTransactionManagement
@Slf4j
@ControllerAdvice
public class SearchController {

    private final GlobalSearchService searchService;

    public SearchController(GlobalSearchService searchService) {
        this.searchService = searchService;
    }
    @PostMapping(value = "index/search")
    public ResponseEntity<Object> getReceiptsByUserIdWithDuration(@RequestHeader("Authorization") String bearerToken, @RequestBody @Valid SearchDtoRequest searchDtoRequest) {
        Object searchResponse = searchService.getLoanDataBySearch(bearerToken, searchDtoRequest);
        return new ResponseEntity<>(searchResponse, HttpStatus.BAD_REQUEST);
    }
}
