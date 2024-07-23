package com.synoriq.synofin.collection.collectionservice.service;

import com.synoriq.synofin.collection.collectionservice.rest.request.searchDTOs.SearchDtoRequest;
import com.synoriq.synofin.collection.collectionservice.rest.response.BaseDTOResponse;

public interface GlobalSearchService {
    public BaseDTOResponse<Object> getLoanDataBySearch(String token, SearchDtoRequest requestBody) throws Exception;

}
