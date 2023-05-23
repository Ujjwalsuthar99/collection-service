package com.synoriq.synofin.collection.collectionservice.service;

import com.synoriq.synofin.collection.collectionservice.entity.LoanAllocationEntity;
import com.synoriq.synofin.collection.collectionservice.rest.request.LoanAllocationDtoRequest;
import com.synoriq.synofin.collection.collectionservice.rest.response.BaseDTOResponse;

import java.util.List;

public interface LoanAllocationService {

    public BaseDTOResponse<Object> createLoanAllocationByAllocatedToUserId(LoanAllocationDtoRequest loanAllocationDtoRequest) throws Exception;
    public List<LoanAllocationEntity> getLoansByUserId(Long allocatedToUserId) throws Exception;

}
