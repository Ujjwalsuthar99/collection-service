package com.synoriq.synofin.collection.collectionservice.service;

import com.synoriq.synofin.collection.collectionservice.entity.ReceiptTransferEntity;
import com.synoriq.synofin.collection.collectionservice.rest.request.depositInvoiceDTOs.DepositInvoiceRequestDTO;
import com.synoriq.synofin.collection.collectionservice.rest.request.receiptTransferDTOs.*;
import com.synoriq.synofin.collection.collectionservice.rest.response.BaseDTOResponse;
import com.synoriq.synofin.collection.collectionservice.rest.response.DepositInvoiceResponseDTOs.DepositInvoiceResponseDataDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.ReceiptTransferDTOs.AllBankTransferResponseDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.ReceiptTransferDTOs.ReceiptTransferDataByReceiptIdResponseDTO;
import com.synoriq.synofin.lms.commondto.dto.collection.ReceiptTransferDTO;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface ReceiptTransferService {

    @Transactional
    public BaseDTOResponse<Object> createReceiptTransfer(@RequestBody ReceiptTransferDtoRequest receiptTransferDtoRequest, String token) throws Exception;
    @Transactional
    public BaseDTOResponse<Object> createReceiptTransferNew(Object object, MultipartFile transferProof, String token) throws Exception;
    public List<ReceiptTransferDTO> getReceiptTransferSummary(Long transferredByUserId);
    @Transactional
    public ReceiptTransferEntity statusUpdate(ReceiptTransferStatusUpdateDtoRequest receiptTransferStatusUpdateDtoRequest, String token) throws Exception;

    BaseDTOResponse<Object> getReceiptTransferById(String token , Long receiptTransferId, Long userId) throws Exception;
    BaseDTOResponse<Object> getReceiptTransferForAirtel(String token , ReceiptTransferForAirtelRequestDTO receiptTransferForAirtelRequestDTO) throws Exception;
    List<Map<String, Object>> getReceiptTransferByUserId(Long transferredBy, Date fromDate, Date endDate, String status, Integer pageNo, Integer pageSize) throws Exception;
    Map<String, List<Map<String, Object>>> getReceiptTransferByUserIdWithAllStatus(Long transferredBy, Date fromDate, Date endDate, Integer pageNo, Integer pageSize) throws Exception;
    ReceiptTransferDataByReceiptIdResponseDTO getReceiptTransferByReceiptId(String token , Long receiptId) throws Exception;
    AllBankTransferResponseDTO getAllBankTransfers(String token, String searchKey, String status, Integer pageNo, Integer pageSize) throws Exception;
    Object getReceiptsDataByReceiptTransferId(String token , Long receiptTransferId) throws Exception;
    BaseDTOResponse<Object> disableApproveButtonInLms(String token , Long receiptId) throws Exception;
    DepositInvoiceResponseDataDTO depositInvoice(String token , DepositInvoiceRequestDTO depositInvoiceRequestDTO) throws Exception;
    BaseDTOResponse<Object> airtelDepositStatusUpdate(String token , ReceiptTransferAirtelDepositStatusRequestDTO requestBody) throws Exception;
    BaseDTOResponse<Object> getReceiptTransferByFilter(ReceiptTransferLmsFilterDTO requestBody) throws Exception;

}
