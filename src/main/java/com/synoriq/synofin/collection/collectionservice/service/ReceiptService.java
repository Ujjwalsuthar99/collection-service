package com.synoriq.synofin.collection.collectionservice.service;

import com.synoriq.synofin.collection.collectionservice.entity.LoanAllocationEntity;
import com.synoriq.synofin.collection.collectionservice.rest.request.createReceiptDTOs.ReceiptServiceDtoRequest;
import com.synoriq.synofin.collection.collectionservice.rest.response.BaseDTOResponse;
import com.synoriq.synofin.collection.collectionservice.rest.response.createReceiptLms.ServiceRequestSaveResponse;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.util.List;

public interface ReceiptService {

    public BaseDTOResponse<Object> getReceiptsByUserIdWithDuration(String userName, String fromDate, String toDate, String searchKey, Integer page, Integer size) throws Exception;

    public BaseDTOResponse<Object> getReceiptsByUserIdWhichNotTransferred(String userName) throws Exception;

    public BaseDTOResponse<Object> getReceiptsByLoanIdWithDuration(Long loanId, String fromDate, String toDate, String status, String paymentMode) throws Exception;
    @Transactional
    public ServiceRequestSaveResponse createReceipt(@RequestBody ReceiptServiceDtoRequest receiptServiceDtoRequest, String bearerToken) throws Exception;
    public List<LoanAllocationEntity> getLoansByUserId(Long allocatedToUserId) throws Exception;
    public Object getReceiptDate(String bearerToken) throws Exception;
    public void getPdf(String token, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception;

}
