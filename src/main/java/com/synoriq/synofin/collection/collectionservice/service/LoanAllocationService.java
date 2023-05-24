package com.synoriq.synofin.collection.collectionservice.service;

import com.synoriq.synofin.collection.collectionservice.entity.LoanAllocationEntity;
import com.synoriq.synofin.collection.collectionservice.rest.request.loanAllocationDTOs.LoanAllocationDtoRequest;
import com.synoriq.synofin.collection.collectionservice.rest.request.loanAllocationDTOs.LoanAllocationMultiUsersDtoRequest;
import com.synoriq.synofin.collection.collectionservice.rest.response.BaseDTOResponse;

import java.util.List;
import java.util.Map;

public interface LoanAllocationService {

    public BaseDTOResponse<Object> createLoanAllocationByAllocatedToUserId(LoanAllocationDtoRequest loanAllocationDtoRequest) throws Exception;
    public BaseDTOResponse<Object> createLoanAllocationToMultipleUserId(LoanAllocationMultiUsersDtoRequest loanAllocationMultiUsersDtoRequest) throws Exception;
    public List<LoanAllocationEntity> getLoansByUserId(Long allocatedToUserId) throws Exception;
    public List<Map<String, Object>> getAllocatedUsersByLoanId(Long loanId) throws Exception;

}
