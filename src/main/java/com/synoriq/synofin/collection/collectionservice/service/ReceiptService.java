package com.synoriq.synofin.collection.collectionservice.service;


import com.synoriq.synofin.collection.collectionservice.rest.request.createReceiptDTOs.ReceiptServiceDtoRequest;
import com.synoriq.synofin.collection.collectionservice.rest.request.receiptTransferDTOs.ReceiptTransferLmsFilterDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.BaseDTOResponse;
import com.synoriq.synofin.collection.collectionservice.rest.response.CreateReceiptLmsDTOs.ServiceRequestSaveResponse;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.util.Map;

public interface ReceiptService {

    BaseDTOResponse<Object> getReceiptsByUserIdWithDuration(String userName, String fromDate, String toDate, String searchKey, Integer page, Integer size) throws Exception;

    BaseDTOResponse<Object> getReceiptsByUserIdWhichNotTransferred(String userName) throws Exception;

    BaseDTOResponse<Object> getReceiptsByLoanIdWithDuration(Long loanId, String fromDate, String toDate, String status, String paymentMode) throws Exception;
    @Transactional
    ServiceRequestSaveResponse createReceiptNew(Object object, MultipartFile paymentReferenceImage, MultipartFile selfieImage, String bearerToken, boolean receiptFromQR) throws Exception;
    Object getReceiptDate(String bearerToken) throws Exception;
    void getPdf(String token, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception;
    BaseDTOResponse<Object> getReceiptsByUserIdWhichNotTransferredForPortal(ReceiptTransferLmsFilterDTO filterDTO) throws Exception;
    String createCollectionReceipt(Map<String, Object> requestBody, String token) throws Exception;

}
