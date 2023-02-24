package com.synoriq.synofin.collection.collectionservice.service;

import com.synoriq.synofin.collection.collectionservice.common.errorcode.ErrorCode;
import com.synoriq.synofin.collection.collectionservice.entity.CollectionActivityLogsEntity;
import com.synoriq.synofin.collection.collectionservice.entity.ReceiptTransferEntity;
import com.synoriq.synofin.collection.collectionservice.entity.ReceiptTransferHistoryEntity;
import com.synoriq.synofin.collection.collectionservice.repository.CollectionActivityLogsRepository;
import com.synoriq.synofin.collection.collectionservice.repository.ReceiptTransferHistoryRepository;
import com.synoriq.synofin.collection.collectionservice.repository.ReceiptTransferRepository;
import com.synoriq.synofin.collection.collectionservice.rest.request.ReceiptTransferDtoRequest;
import com.synoriq.synofin.collection.collectionservice.rest.request.ReceiptTransferStatusUpdateDtoRequest;
import com.synoriq.synofin.collection.collectionservice.rest.response.BaseDTOResponse;
import com.synoriq.synofin.collection.collectionservice.rest.response.ReceiptTransferResponseDTO;
import com.synoriq.synofin.lms.commondto.dto.collection.ReceiptTransferDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ReceiptTransferService {

    @Autowired
    private ReceiptTransferRepository receiptTransferRepository;

    @Autowired
    private ReceiptTransferHistoryRepository receiptTransferHistoryRepository;

    @Autowired
    private CollectionActivityLogsRepository collectionActivityLogsRepository;

    @Autowired
    private ActivityLogService activityLogService;

    @Transactional
    public BaseDTOResponse<Object> createReceiptTransfer(@RequestBody ReceiptTransferDtoRequest receiptTransferDtoRequest) throws Exception {

        BaseDTOResponse<Object> baseResponse;
        ResponseEntity<Object> response = null;
        try {
            Long collectionActivityId = activityLogService.createActivityLogs(receiptTransferDtoRequest.getActivityData());


            ReceiptTransferEntity receiptTransferEntity = new ReceiptTransferEntity();

            receiptTransferEntity.setCreatedDate(new Date());
            receiptTransferEntity.setTransferredBy(receiptTransferDtoRequest.getTransferredBy());
            receiptTransferEntity.setDeleted(false);
            receiptTransferEntity.setTransferType(receiptTransferDtoRequest.getTransferType());
            receiptTransferEntity.setTransferMode(receiptTransferDtoRequest.getTransferMode());
            receiptTransferEntity.setTransferredToUserId(receiptTransferDtoRequest.getTransferredToUserId());
            receiptTransferEntity.setAmount(receiptTransferDtoRequest.getAmount());
            receiptTransferEntity.setReceiptImage(receiptTransferDtoRequest.getReceiptImage());
            receiptTransferEntity.setStatus(receiptTransferDtoRequest.getStatus());
            receiptTransferEntity.setTransferBankCode(receiptTransferDtoRequest.getTransferBankCode());
            receiptTransferEntity.setActionDatetime(receiptTransferDtoRequest.getActionDatetime());
            receiptTransferEntity.setActionReason(receiptTransferDtoRequest.getActionReason());
            receiptTransferEntity.setActionRemarks(receiptTransferDtoRequest.getActionRemarks());
            receiptTransferEntity.setActionBy(receiptTransferDtoRequest.getActionBy());
            receiptTransferEntity.setCollectionActivityLogsId(collectionActivityId);

            receiptTransferRepository.save(receiptTransferEntity);

            for (Long receiptTransferId : receiptTransferDtoRequest.getReceipts()) {

                ReceiptTransferHistoryEntity receiptTransferHistoryEntity = new ReceiptTransferHistoryEntity();

                receiptTransferHistoryEntity.setReceiptTransferId(receiptTransferEntity.getReceiptTransferId());
                receiptTransferHistoryEntity.setCollectionReceiptsId(receiptTransferId);
                receiptTransferHistoryRepository.save(receiptTransferHistoryEntity);
            }

            baseResponse = new BaseDTOResponse<Object>(receiptTransferEntity);
        } catch (Exception ee) {
            log.error("RestControllers error occurred for vanWebHookDetails: {} ->  {}", ee.getMessage());
            if (ErrorCode.getErrorCode(Integer.valueOf(ee.getMessage().trim())) == null) {
                baseResponse = new BaseDTOResponse<Object>(ErrorCode.DATA_FETCH_ERROR);
            } else {
                baseResponse = new BaseDTOResponse<Object>(ErrorCode.getErrorCode(Integer.valueOf(ee.getMessage().trim())));
            }
        }
        return baseResponse;
    }


    public List<ReceiptTransferDTO> getReceiptTransferSummary(Long transferredByUserId) {
        List<ReceiptTransferEntity> receiptTransferEntityList = receiptTransferRepository.getReceiptTransferSummaryByTransferredBy(transferredByUserId);
        List<ReceiptTransferDTO> receiptTransferDTOList = new ArrayList<>();
        for (ReceiptTransferEntity receiptTransferEntity : receiptTransferEntityList) {
            ReceiptTransferDTO receiptTransferDTO = new ReceiptTransferDTO();
//            followUpDTO.setFollowUpId(followUpEntity.getFollowUpId());
            receiptTransferDTO.setCreatedDate(receiptTransferEntity.getCreatedDate());
            receiptTransferDTO.setTransferredBy(receiptTransferEntity.getTransferredBy());
            receiptTransferDTO.setDeleted(receiptTransferEntity.getDeleted());
            receiptTransferDTO.setTransferType(receiptTransferEntity.getTransferType());
            receiptTransferDTO.setTransferMode(receiptTransferEntity.getTransferMode());
            receiptTransferDTO.setTransferredToUserId(receiptTransferEntity.getReceiptTransferId());
            receiptTransferDTO.setAmount(receiptTransferEntity.getAmount());
            receiptTransferDTO.setReceiptImage(receiptTransferEntity.getReceiptImage());
            receiptTransferDTO.setStatus(receiptTransferEntity.getStatus());
            receiptTransferDTO.setRemarks(receiptTransferEntity.getRemarks());
            receiptTransferDTO.setTransferBankCode(receiptTransferEntity.getTransferBankCode());
            receiptTransferDTO.setActionDatetime(receiptTransferEntity.getActionDatetime());
            receiptTransferDTO.setActionReason(receiptTransferEntity.getActionReason());
            receiptTransferDTO.setActionRemarks(receiptTransferEntity.getActionRemarks());
            receiptTransferDTO.setActionBy(receiptTransferEntity.getActionBy());
            receiptTransferDTO.setCollectionActivityLogsId(receiptTransferEntity.getCollectionActivityLogsId());
            receiptTransferDTOList.add(receiptTransferDTO);
        }
        return receiptTransferDTOList;
    }


    public ReceiptTransferEntity statusUpdate(ReceiptTransferStatusUpdateDtoRequest receiptTransferStatusUpdateDtoRequest) throws Exception {
        BaseDTOResponse<Object> baseResponse = null;
        ResponseEntity<Object> response = null;
        ReceiptTransferEntity receiptTransferEntity;
        try {
            receiptTransferEntity = receiptTransferRepository.findById(receiptTransferStatusUpdateDtoRequest.getReceiptTransferId()).get();
            receiptTransferEntity.setStatus(receiptTransferStatusUpdateDtoRequest.getStatus());
            receiptTransferEntity.setActionDatetime(new Date());
            receiptTransferEntity.setActionReason(receiptTransferStatusUpdateDtoRequest.getActionReason());
            receiptTransferEntity.setActionRemarks(receiptTransferStatusUpdateDtoRequest.getActionReason());
            receiptTransferEntity.setActionBy(receiptTransferStatusUpdateDtoRequest.getActionBy());
            receiptTransferRepository.save(receiptTransferEntity);
        } catch (Exception e) {
            throw new Exception("1016028");
        }
        return receiptTransferEntity;
    }


    public ReceiptTransferResponseDTO getReceiptTransferById(Long receiptTransferId) throws Exception {
        log.info("receipt tranfer idddd {}", receiptTransferId);
        ReceiptTransferEntity receiptTransferEntity;
        ReceiptTransferResponseDTO receiptTransferResponseDTO = new ReceiptTransferResponseDTO();
        try {
            receiptTransferEntity = receiptTransferRepository.findById(receiptTransferId).get();
            List<Map<String, Object>> receiptsData = receiptTransferRepository.getDataByReceiptTransferId(receiptTransferId);
            receiptTransferResponseDTO.setReceiptTransferData(receiptTransferEntity);
            receiptTransferResponseDTO.setReceiptData(receiptsData);

        } catch (Exception e) {
            throw new Exception("1016028");
        }
        return receiptTransferResponseDTO;
    }


    public List<ReceiptTransferEntity> getReceiptTransferByUserId(Long transferredBy, Date fromDate, Date toDate, String status) throws Exception {
        List<ReceiptTransferEntity> receiptTransferEntity;
        try {
            receiptTransferEntity = receiptTransferRepository.getReceiptTransferByUserId(transferredBy, fromDate, toDate, status);
        } catch (Exception e) {
            throw new Exception("1016028");
        }
        return receiptTransferEntity;
    }

}