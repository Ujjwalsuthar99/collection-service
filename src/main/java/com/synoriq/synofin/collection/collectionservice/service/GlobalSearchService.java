package com.synoriq.synofin.collection.collectionservice.service;

import com.synoriq.synofin.collection.collectionservice.rest.request.searchdtos.SearchDtoRequest;
import com.synoriq.synofin.collection.collectionservice.common.exception.CustomException;
import com.synoriq.synofin.collection.collectionservice.rest.response.BaseDTOResponse;

public interface GlobalSearchService {
     BaseDTOResponse<Object> getLoanDataBySearch(String token, SearchDtoRequest requestBody) throws CustomException;

}
