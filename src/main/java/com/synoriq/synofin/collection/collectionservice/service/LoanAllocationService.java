package com.synoriq.synofin.collection.collectionservice.service;

import com.synoriq.synofin.collection.collectionservice.common.exception.CollectionException;
import com.synoriq.synofin.collection.collectionservice.common.exception.CustomException;
import com.synoriq.synofin.collection.collectionservice.entity.LoanAllocationEntity;
import com.synoriq.synofin.collection.collectionservice.rest.request.loanallocationdtos.LoanAllocationDtoRequest;
import com.synoriq.synofin.collection.collectionservice.rest.request.loanallocationdtos.LoanAllocationMultiUsersDtoRequest;
import com.synoriq.synofin.collection.collectionservice.rest.response.BaseDTOResponse;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface LoanAllocationService {

     BaseDTOResponse<Object> createLoanAllocationByAllocatedToUserId(LoanAllocationDtoRequest loanAllocationDtoRequest) throws CustomException;
     BaseDTOResponse<Object> createLoanAllocationToMultipleUserId(LoanAllocationMultiUsersDtoRequest loanAllocationMultiUsersDtoRequest) throws CustomException;
     List<LoanAllocationEntity> getLoansByUserId(Long allocatedToUserId) throws CollectionException;
     List<Map<String, Object>> getAllocatedUsersByLoanId(Long loanId) throws CollectionException;
     String deleteAllAllocatedLoans(Date fromDate, Date toDate) throws CollectionException;

}
