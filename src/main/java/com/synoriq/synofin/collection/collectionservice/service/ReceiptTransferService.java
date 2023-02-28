package com.synoriq.synofin.collection.collectionservice.service;

import com.synoriq.synofin.collection.collectionservice.common.errorcode.ErrorCode;
import com.synoriq.synofin.collection.collectionservice.entity.*;
import com.synoriq.synofin.collection.collectionservice.repository.*;
import com.synoriq.synofin.collection.collectionservice.rest.request.ReceiptTransferDtoRequest;
import com.synoriq.synofin.collection.collectionservice.rest.request.ReceiptTransferStatusUpdateDtoRequest;
import com.synoriq.synofin.collection.collectionservice.rest.response.BaseDTOResponse;
import com.synoriq.synofin.collection.collectionservice.rest.response.ReceiptTransferResponseDTO;
import com.synoriq.synofin.lms.commondto.dto.collection.ReceiptTransferDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.*;

import static com.synoriq.synofin.collection.collectionservice.common.ActivityEvent.*;
import static com.synoriq.synofin.collection.collectionservice.common.errorcode.ErrorCode.*;

@Service
@Slf4j
public class ReceiptTransferService {
    @Autowired
    private CollectionReceiptRepository collectionReceiptRepository;

    @Autowired
    private ReceiptTransferRepository receiptTransferRepository;

    @Autowired
    private ReceiptTransferHistoryRepository receiptTransferHistoryRepository;

    @Autowired
    private CollectionActivityLogsRepository collectionActivityLogsRepository;

    @Autowired
    private CollectionLimitUserWiseRepository collectionLimitUserWiseRepository;

    @Autowired
    private ActivityLogService activityLogService;

    @Transactional
    public BaseDTOResponse<Object> createReceiptTransfer(@RequestBody ReceiptTransferDtoRequest receiptTransferDtoRequest) throws Exception {

        BaseDTOResponse<Object> baseResponse;
        ResponseEntity<Object> response = null;
        try {
            Long collectionActivityId = activityLogService.createActivityLogs(receiptTransferDtoRequest.getActivityData());

            Double transferredAmount = receiptTransferDtoRequest.getAmount();
            Long transferredToID = receiptTransferDtoRequest.getTransferredToUserId();
            String transferMode = receiptTransferDtoRequest.getTransferMode();
            CollectionLimitUserWiseEntity collectionLimitUserWiseEntity = collectionLimitUserWiseRepository.getCollectionLimitUserWiseByUserId(transferredToID, transferMode);
            Double utilizedAmount = collectionLimitUserWiseEntity.getUtilizedLimitValue();
            Double totalLimitValue = collectionLimitUserWiseEntity.getTotalLimitValue();
            if ((utilizedAmount + transferredAmount) < totalLimitValue) {


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
            } else {
                throw new Exception("1016031");
            }
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
            String requestStatus = receiptTransferStatusUpdateDtoRequest.getStatus();
            Long requestActionBy = receiptTransferStatusUpdateDtoRequest.getActionBy();
            Long receiptTransferId = receiptTransferStatusUpdateDtoRequest.getReceiptTransferId();
            receiptTransferEntity = receiptTransferRepository.findById(receiptTransferId).get();
            Long receiptTransferEntityTransferredToUserId = receiptTransferEntity.getTransferredToUserId();
            Long receiptTransferEntityTransferredBy = receiptTransferEntity.getTransferredBy();
            String receiptTransferEntityTransferMode = receiptTransferEntity.getTransferMode();
            Double amount = receiptTransferEntity.getAmount();
            String currentStatus = receiptTransferEntity.getStatus();

            Long collectionActivityLogsId = activityLogService.createActivityLogs(receiptTransferStatusUpdateDtoRequest.getActivityLog());


            if (currentStatus.equals("pending")) {
                switch (requestStatus) {
                    case RECEIPT_TRANSFER_CANCEL:
                        if (receiptTransferEntityTransferredBy.equals(requestActionBy)) {
                            saveReceiptTransferData(receiptTransferStatusUpdateDtoRequest, receiptTransferEntity, collectionActivityLogsId);
                        } else {
                            throw new Exception("1016029");
                        }
                        break;
                    case RECEIPT_TRANSFER_APPROVE:
                        if (receiptTransferEntityTransferredToUserId.equals(requestActionBy)) {
                            List<ReceiptTransferHistoryEntity> receiptTransferHistoryEntityList = receiptTransferHistoryRepository.getReceiptTransferHistoryDataByReceiptTransferId(receiptTransferStatusUpdateDtoRequest.getReceiptTransferId());
                            for (ReceiptTransferHistoryEntity receiptTransferHistoryEntity : receiptTransferHistoryEntityList) {
                                Long collectionReceiptId = receiptTransferHistoryEntity.getCollectionReceiptsId();
                                CollectionReceiptEntity collectionReceiptEntity = collectionReceiptRepository.findByReceiptId(collectionReceiptId);
                                if (collectionReceiptEntity != null) {
                                    collectionReceiptEntity.setLastReceiptTransferId(receiptTransferId);
                                    collectionReceiptEntity.setReceiptHolderUserId(requestActionBy);
                                    collectionReceiptEntity.setCollectionActivityLogsId(collectionActivityLogsId);
                                    collectionReceiptRepository.save(collectionReceiptEntity);
                                }
                            }
                            CollectionLimitUserWiseEntity collectionLimitUserWiseEntity = collectionLimitUserWiseRepository.getCollectionLimitUserWiseByUserId(receiptTransferEntityTransferredBy, receiptTransferEntityTransferMode);
                            if (collectionLimitUserWiseEntity != null) {
                                if (receiptTransferEntityTransferMode.equals("cash")) {
                                    Double utilizedLimitValue = collectionLimitUserWiseEntity.getUtilizedLimitValue();
                                    Double updatedLimit = utilizedLimitValue - amount;
                                    collectionLimitUserWiseEntity.setUtilizedLimitValue(updatedLimit);
                                    collectionLimitUserWiseRepository.save(collectionLimitUserWiseEntity);
                                } else if (receiptTransferEntityTransferMode.equals("cheque")) {
                                    Double utilizedLimitValue = collectionLimitUserWiseEntity.getUtilizedLimitValue();
                                    Double updatedLimit = utilizedLimitValue - amount;
                                    collectionLimitUserWiseEntity.setUtilizedLimitValue(updatedLimit);
                                    collectionLimitUserWiseRepository.save(collectionLimitUserWiseEntity);
                                }
                            }
                            saveReceiptTransferData(receiptTransferStatusUpdateDtoRequest, receiptTransferEntity, collectionActivityLogsId);
                        } else {
                            throw new Exception("1016029");
                        }
                        break;
                    case RECEIPT_TRANSFER_REJECT:
                        if (receiptTransferEntityTransferredToUserId.equals(requestActionBy)) {
                            saveReceiptTransferData(receiptTransferStatusUpdateDtoRequest, receiptTransferEntity, collectionActivityLogsId);
                        } else {
                            throw new Exception("1016029");
                        }
                        break;
                }
            } else {
                throw new Exception("1016030");
            }
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
        return receiptTransferEntity;
    }


    public void saveReceiptTransferData(ReceiptTransferStatusUpdateDtoRequest receiptTransferStatusUpdateDtoRequest, ReceiptTransferEntity receiptTransferEntity, Long collectionActivityLogsId)
            throws Exception {
        receiptTransferEntity.setStatus(receiptTransferStatusUpdateDtoRequest.getStatus());
        receiptTransferEntity.setActionDatetime(new Date());
        receiptTransferEntity.setActionReason(receiptTransferStatusUpdateDtoRequest.getActionReason());
        receiptTransferEntity.setActionRemarks(receiptTransferStatusUpdateDtoRequest.getActionRemarks());
        receiptTransferEntity.setActionBy(receiptTransferStatusUpdateDtoRequest.getActionBy());
        receiptTransferEntity.setCollectionActivityLogsId(collectionActivityLogsId);
        receiptTransferRepository.save(receiptTransferEntity);
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


    public List<Map<String, Object>> getReceiptTransferByUserId(Long transferredBy, Date fromDate, Date toDate, String status, Integer pageNo, Integer pageSize) throws Exception {
        List<Map<String, Object>> receiptTransferEntity;
        try {
            Pageable pageRequest;
            if (pageNo > 0) {
                pageNo = pageNo - 1;
            }
            pageRequest = PageRequest.of(pageNo, pageSize);
            receiptTransferEntity = receiptTransferRepository.getReceiptTransferByUserId(transferredBy, fromDate, toDate, status, pageRequest);
        } catch (Exception e) {
            throw new Exception("1016028");
        }
        return receiptTransferEntity;
    }

    public List<Map<String, Object>> getReceiptTransferByUserIdWithAllStatus(Long transferredBy, Date fromDate, Date toDate, Integer pageNo, Integer pageSize) throws Exception {
        List<Map<String, Object>> receiptTransferEntity2nd;
        try {
            Pageable pageRequest;
            if (pageNo > 0) {
                pageNo = pageNo - 1;
            }
            pageRequest = PageRequest.of(pageNo, pageSize);
            receiptTransferEntity2nd = receiptTransferRepository.getReceiptTransferByUserIdWithAllStatus(transferredBy, fromDate, toDate, pageRequest);
        } catch (Exception e) {
            throw new Exception("1016028");
        }
        return receiptTransferEntity2nd;
    }

}