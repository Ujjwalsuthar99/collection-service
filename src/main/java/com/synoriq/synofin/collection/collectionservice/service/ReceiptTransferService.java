package com.synoriq.synofin.collection.collectionservice.service;

import com.synoriq.synofin.collection.collectionservice.common.exception.CollectionException;
import com.synoriq.synofin.collection.collectionservice.common.exception.CustomException;
import com.synoriq.synofin.collection.collectionservice.entity.ReceiptTransferEntity;
import com.synoriq.synofin.collection.collectionservice.rest.request.depositinvoicedtos.DepositInvoiceRequestDTO;
import com.synoriq.synofin.collection.collectionservice.rest.request.receipttransferdtos.*;
import com.synoriq.synofin.collection.collectionservice.rest.response.BaseDTOResponse;
import com.synoriq.synofin.collection.collectionservice.rest.response.depositinvoiceresponsedtos.DepositInvoiceResponseDataDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.receipttransferdtos.AllBankTransferResponseDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.receipttransferdtos.ReceiptTransferDataByReceiptIdResponseDTO;
import com.synoriq.synofin.lms.commondto.dto.collection.ReceiptTransferDTO;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface ReceiptTransferService {

    @Transactional
     BaseDTOResponse<Object> createReceiptTransfer(@RequestBody ReceiptTransferDtoRequest receiptTransferDtoRequest, String token) throws CustomException;
    @Transactional
     BaseDTOResponse<Object> createReceiptTransferNew(Object object, MultipartFile transferProof, String token) throws CustomException;
     List<ReceiptTransferDTO> getReceiptTransferSummary(Long transferredByUserId);
    @Transactional
     ReceiptTransferEntity statusUpdate(ReceiptTransferStatusUpdateDtoRequest receiptTransferStatusUpdateDtoRequest, String token) throws CustomException;

    BaseDTOResponse<Object> getReceiptTransferById(String token , Long receiptTransferId, Long userId) throws CollectionException;
    BaseDTOResponse<Object> getReceiptTransferForAirtel(String token , ReceiptTransferForAirtelRequestDTO receiptTransferForAirtelRequestDTO) throws CollectionException;
    List<Map<String, Object>> getReceiptTransferByUserId(Long transferredBy, Date fromDate, Date endDate, String status, Integer pageNo, Integer pageSize) throws CollectionException;
    Map<String, List<Map<String, Object>>> getReceiptTransferByUserIdWithAllStatus(Long transferredBy, Date fromDate, Date endDate, Integer pageNo, Integer pageSize) throws CollectionException;
    ReceiptTransferDataByReceiptIdResponseDTO getReceiptTransferByReceiptId(String token , Long receiptId) throws CollectionException;
    AllBankTransferResponseDTO getAllBankTransfers(String token, String searchKey, String status, Integer pageNo, Integer pageSize) throws CollectionException;
    Object getReceiptsDataByReceiptTransferId(String token , Long receiptTransferId) throws CollectionException;
    BaseDTOResponse<Object> disableApproveButtonInLms(String token , Long receiptId) throws CustomException;
    DepositInvoiceResponseDataDTO depositInvoice(String token , DepositInvoiceRequestDTO depositInvoiceRequestDTO) throws CustomException;
    BaseDTOResponse<Object> airtelDepositStatusUpdate(String token , ReceiptTransferAirtelDepositStatusRequestDTO requestBody) throws CustomException;
    BaseDTOResponse<Object> getReceiptTransferByFilter(ReceiptTransferLmsFilterDTO requestBody) throws CollectionException;

}
