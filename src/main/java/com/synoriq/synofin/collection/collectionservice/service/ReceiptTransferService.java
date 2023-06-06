package com.synoriq.synofin.collection.collectionservice.service;

import com.synoriq.synofin.collection.collectionservice.entity.ReceiptTransferEntity;
import com.synoriq.synofin.collection.collectionservice.rest.request.ReceiptTransferDtoRequest;
import com.synoriq.synofin.collection.collectionservice.rest.request.ReceiptTransferStatusUpdateDtoRequest;
import com.synoriq.synofin.collection.collectionservice.rest.response.BaseDTOResponse;
import com.synoriq.synofin.collection.collectionservice.rest.response.ReceiptTransferDTOs.ReceiptTransferDataByReceiptIdResponseDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.ReceiptTransferResponseDTO;
import com.synoriq.synofin.lms.commondto.dto.collection.ReceiptTransferDTO;
import org.springframework.web.bind.annotation.RequestBody;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface ReceiptTransferService {

    @Transactional
    public BaseDTOResponse<Object> createReceiptTransfer(@RequestBody ReceiptTransferDtoRequest receiptTransferDtoRequest, String token) throws Exception;
    public List<ReceiptTransferDTO> getReceiptTransferSummary(Long transferredByUserId);
    @Transactional
    public ReceiptTransferEntity statusUpdate(ReceiptTransferStatusUpdateDtoRequest receiptTransferStatusUpdateDtoRequest, String token) throws Exception;
    public void saveReceiptTransferData(ReceiptTransferStatusUpdateDtoRequest receiptTransferStatusUpdateDtoRequest, ReceiptTransferEntity receiptTransferEntity, Long collectionActivityLogsId) throws Exception;
    public void setRemarks(ReceiptTransferStatusUpdateDtoRequest receiptTransferStatusUpdateDtoRequest, Long collectionActivityId);
    public ReceiptTransferResponseDTO getReceiptTransferById(String token , Long receiptTransferId, Long userId) throws Exception;
    public List<Map<String, Object>> getReceiptTransferByUserId(Long transferredBy, Date fromDate, Date endDate, String status, Integer pageNo, Integer pageSize) throws Exception;
    public Map<String, List<Map<String, Object>>> getReceiptTransferByUserIdWithAllStatus(Long transferredBy, Date fromDate, Date endDate, Integer pageNo, Integer pageSize) throws Exception;
    public ReceiptTransferDataByReceiptIdResponseDTO getReceiptTransferByReceiptId(String token , Long receiptId) throws Exception;
    public Object getReceiptsDataByReceiptTransferId(String token , Long receiptTransferId) throws Exception;

}
