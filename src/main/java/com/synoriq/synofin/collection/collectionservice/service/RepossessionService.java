package com.synoriq.synofin.collection.collectionservice.service;

import com.synoriq.synofin.collection.collectionservice.common.exception.CustomException;
import com.synoriq.synofin.collection.collectionservice.rest.request.repossessiondtos.RepossessionRequestDTO;
import com.synoriq.synofin.collection.collectionservice.rest.request.repossessiondtos.lmsrepossession.LmsRepossessionDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.BaseDTOResponse;

public interface RepossessionService {

     BaseDTOResponse<Object> getRepossessionData(Long loanId) throws CustomException;
     BaseDTOResponse<Object> getAllRepossession() throws CustomException;
     BaseDTOResponse<Object> initiateRepossession(String token ,RepossessionRequestDTO requestDto) throws CustomException;
     BaseDTOResponse<Object> yardRepossession(String token ,RepossessionRequestDTO requestDto) throws CustomException;
     BaseDTOResponse<Object> getDataByRepoId(String token ,Long repoId) throws CustomException;
     BaseDTOResponse<Object> lmsRepossession(String token , LmsRepossessionDTO requestDto) throws CustomException;
}
