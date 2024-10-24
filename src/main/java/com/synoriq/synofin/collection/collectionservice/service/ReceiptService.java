package com.synoriq.synofin.collection.collectionservice.service;


import com.synoriq.synofin.collection.collectionservice.rest.request.receipttransferdtos.ReceiptTransferLmsFilterDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.synoriq.synofin.collection.collectionservice.common.exception.CollectionException;
import com.synoriq.synofin.collection.collectionservice.common.exception.ConnectorException;
import com.synoriq.synofin.collection.collectionservice.common.exception.CustomException;
import com.synoriq.synofin.collection.collectionservice.common.exception.DataLockException;
import com.synoriq.synofin.collection.collectionservice.rest.response.BaseDTOResponse;
import com.synoriq.synofin.collection.collectionservice.rest.response.createreceiptlmsdtos.ServiceRequestSaveResponse;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.util.Map;

public interface ReceiptService {

    BaseDTOResponse<Object> getReceiptsByUserIdWithDuration(String userName, String fromDate, String toDate, String searchKey, Integer page, Integer size) throws CollectionException;

    BaseDTOResponse<Object> getReceiptsByUserIdWhichNotTransferred(String userName) throws CollectionException;

    BaseDTOResponse<Object> getReceiptsByLoanIdWithDuration(Long loanId, String fromDate, String toDate, String status, String paymentMode) throws CollectionException;
    @Transactional
    ServiceRequestSaveResponse createReceiptNew(Object object, MultipartFile paymentReferenceImage, MultipartFile selfieImage, String bearerToken, boolean receiptFromQR) throws CustomException, InterruptedException, ConnectorException, JsonProcessingException, DataLockException ;
    Object getReceiptDate(String bearerToken) throws CustomException;
    void getPdf(String token, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws CustomException;
    BaseDTOResponse<Object> getReceiptsByUserIdWhichNotTransferredForPortal(ReceiptTransferLmsFilterDTO filterDTO) throws CollectionException;
    String createCollectionReceipt(Map<String, Object> requestBody, String token) throws CustomException;

}
