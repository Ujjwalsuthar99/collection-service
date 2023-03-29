package com.synoriq.synofin.collection.collectionservice.service;

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

import java.util.*;

import static com.synoriq.synofin.collection.collectionservice.common.ActivityEvent.*;
import static com.synoriq.synofin.collection.collectionservice.common.ActivityRemarks.*;
import static com.synoriq.synofin.collection.collectionservice.common.GlobalVariables.CASH_COLLECTION_DEFAULT_LIMIT;
import static com.synoriq.synofin.collection.collectionservice.common.GlobalVariables.CHEQUE_COLLECTION_DEFAULT_LIMIT;

@Service
@Slf4j
@Transactional
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
    private CollectionConfigurationsRepository collectionConfigurationsRepository;
    @Autowired
    private UtilityService utilityService;

    @Autowired
    private ActivityLogService activityLogService;

    @Transactional
    public BaseDTOResponse<Object> createReceiptTransfer(@RequestBody ReceiptTransferDtoRequest receiptTransferDtoRequest) throws Exception {

        BaseDTOResponse<Object> baseResponse;
        ResponseEntity<Object> response = null;
        try {
            Long receiptTransferTableId = receiptTransferDtoRequest.getReceiptTransferId();
            Long collectionActivityId = activityLogService.createActivityLogs(receiptTransferDtoRequest.getActivityData());
            String limitConf;
            String updatedRemarks;

            Double utilizedAmount;
            Double totalLimitValue;
            Double transferredAmount = receiptTransferDtoRequest.getAmount();
            Long transferredToID = receiptTransferDtoRequest.getTransferredToUserId();
            String transferMode = receiptTransferDtoRequest.getTransferMode();
            if(transferMode.equals("cash")) {
                limitConf = CASH_COLLECTION_DEFAULT_LIMIT;
            } else {
                limitConf = CHEQUE_COLLECTION_DEFAULT_LIMIT;
            }
            if (transferredToID != null) {
                CollectionLimitUserWiseEntity collectionLimitUserWiseEntity = collectionLimitUserWiseRepository.getCollectionLimitUserWiseByUserId(transferredToID, transferMode);
                if (collectionLimitUserWiseEntity == null) {
                    utilizedAmount = 0.00;
                    totalLimitValue = Double.valueOf(collectionConfigurationsRepository.findConfigurationValueByConfigurationName(limitConf));
                } else {
                    utilizedAmount = collectionLimitUserWiseEntity.getUtilizedLimitValue();
                    totalLimitValue = collectionLimitUserWiseEntity.getTotalLimitValue();
                }
            } else {
                utilizedAmount = 0.00;
                totalLimitValue = Double.valueOf(collectionConfigurationsRepository.findConfigurationValueByConfigurationName(limitConf));
            }
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
                CollectionActivityLogsEntity collectionActivityLogsEntity1 = collectionActivityLogsRepository.findByCollectionActivityLogsId(collectionActivityId);
                String remarks = receiptTransferDtoRequest.getActivityData().getRemarks();
                String lastWord = remarks.substring(remarks.lastIndexOf(" ")+1);
                updatedRemarks = CREATE_TRANSFER;
                updatedRemarks = updatedRemarks.replace("{transfer_request}", receiptTransferEntity.getReceiptTransferId().toString());
                updatedRemarks = (updatedRemarks + lastWord);
                collectionActivityLogsEntity1.setRemarks(updatedRemarks);
                collectionActivityLogsRepository.save(collectionActivityLogsEntity1);
                if (receiptTransferTableId == null) {
                    for (Long receiptTransferId : receiptTransferDtoRequest.getReceipts()) {

                        ReceiptTransferHistoryEntity receiptTransferHistoryEntity = new ReceiptTransferHistoryEntity();

                        receiptTransferHistoryEntity.setReceiptTransferId(receiptTransferEntity.getReceiptTransferId());
                        receiptTransferHistoryEntity.setCollectionReceiptsId(receiptTransferId);
                        receiptTransferHistoryRepository.save(receiptTransferHistoryEntity);
                    }
                } else {
                    List<ReceiptTransferHistoryEntity> receiptTransferHistoryEntityList;
                    receiptTransferHistoryEntityList = receiptTransferHistoryRepository.getReceiptTransferHistoryDataByReceiptTransferId(receiptTransferTableId);
                    for (ReceiptTransferHistoryEntity receiptTransferHistoryEntity : receiptTransferHistoryEntityList) {

                        ReceiptTransferHistoryEntity receiptTransferHistoryEntityToBeSaved = new ReceiptTransferHistoryEntity();
                        receiptTransferHistoryEntityToBeSaved.setReceiptTransferId(receiptTransferEntity.getReceiptTransferId());
                        receiptTransferHistoryEntityToBeSaved.setCollectionReceiptsId(receiptTransferHistoryEntity.getCollectionReceiptsId());
                        receiptTransferHistoryRepository.save(receiptTransferHistoryEntityToBeSaved);
                    }
                }
                baseResponse = new BaseDTOResponse<Object>(receiptTransferEntity);

            } else {
                throw new Exception("1016031");
            }
        } catch (Exception ee) {
            log.error("RestControllers error occurred for vanWebHookDetails: {} ->  {}", ee.getMessage());
            throw new Exception(ee.getMessage());
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
    @Transactional
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
                            // history entity rows delete on cancelling the transfer
                            List<ReceiptTransferHistoryEntity> receiptTransferHistoryEntityList = receiptTransferHistoryRepository.getReceiptTransferHistoryDataByReceiptTransferId(receiptTransferId);
                            for (ReceiptTransferHistoryEntity receiptTransferHistoryEntity : receiptTransferHistoryEntityList) {
                                receiptTransferHistoryEntity.setDeleted(true);
                            }
                            // set Activity remarks
                            setRemarks(receiptTransferStatusUpdateDtoRequest, collectionActivityLogsId);
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
                            // checking limit value from limit_wise table for transfer by user id
                            CollectionLimitUserWiseEntity collectionLimitUserWiseEntityOfTransferById = collectionLimitUserWiseRepository.getCollectionLimitUserWiseByUserId(receiptTransferEntityTransferredBy, receiptTransferEntityTransferMode);
                            if (collectionLimitUserWiseEntityOfTransferById != null) {
                                if (receiptTransferEntityTransferMode.equals("cash")) {
                                    Double utilizedLimitValue = collectionLimitUserWiseEntityOfTransferById.getUtilizedLimitValue();
                                    Double updatedLimit = utilizedLimitValue - amount;
                                    collectionLimitUserWiseEntityOfTransferById.setUtilizedLimitValue(updatedLimit);
                                    collectionLimitUserWiseRepository.save(collectionLimitUserWiseEntityOfTransferById);
                                } else if (receiptTransferEntityTransferMode.equals("cheque")) {
                                    Double utilizedLimitValue = collectionLimitUserWiseEntityOfTransferById.getUtilizedLimitValue();
                                    Double updatedLimit = utilizedLimitValue - amount;
                                    collectionLimitUserWiseEntityOfTransferById.setUtilizedLimitValue(updatedLimit);
                                    collectionLimitUserWiseRepository.save(collectionLimitUserWiseEntityOfTransferById);
                                }
                            }
                            // checking limit value from limit_wise table for transfer to user id
                            CollectionLimitUserWiseEntity collectionLimitUserWiseEntityOfTransferToUserId = collectionLimitUserWiseRepository.getCollectionLimitUserWiseByUserId(requestActionBy, receiptTransferEntityTransferMode);
                            if (collectionLimitUserWiseEntityOfTransferToUserId != null) {
                                if (receiptTransferEntityTransferMode.equals("cash")) {
                                    Double utilizedLimitValue = collectionLimitUserWiseEntityOfTransferToUserId.getUtilizedLimitValue();
                                    if ((utilizedLimitValue + amount) > collectionLimitUserWiseEntityOfTransferToUserId.getTotalLimitValue()) {
                                        throw new Exception("1016037");
                                    }
                                    Double updatedLimit = utilizedLimitValue + amount;
                                    collectionLimitUserWiseEntityOfTransferToUserId.setUtilizedLimitValue(updatedLimit);
                                    collectionLimitUserWiseRepository.save(collectionLimitUserWiseEntityOfTransferToUserId);
                                } else if (receiptTransferEntityTransferMode.equals("cheque")) {
                                    Double utilizedLimitValue = collectionLimitUserWiseEntityOfTransferToUserId.getUtilizedLimitValue();
                                    Double updatedLimit = utilizedLimitValue + amount;
                                    collectionLimitUserWiseEntityOfTransferToUserId.setUtilizedLimitValue(updatedLimit);
                                    collectionLimitUserWiseRepository.save(collectionLimitUserWiseEntityOfTransferToUserId);
                                }
                            } else {
                                CollectionLimitUserWiseEntity collectionLimitUserWiseEntity = new CollectionLimitUserWiseEntity();
                                String limitConf = "";
                                if(receiptTransferEntityTransferMode.equals("cash")) {
                                    limitConf = CASH_COLLECTION_DEFAULT_LIMIT;
                                } else {
                                    limitConf = CHEQUE_COLLECTION_DEFAULT_LIMIT;
                                }
                                Double totalLimitValue = Double.valueOf(collectionConfigurationsRepository.findConfigurationValueByConfigurationName(limitConf));
                                collectionLimitUserWiseEntity.setCreatedDate(new Date());
                                collectionLimitUserWiseEntity.setDeleted(false);
                                collectionLimitUserWiseEntity.setCollectionLimitStrategiesKey(receiptTransferEntityTransferMode);
                                collectionLimitUserWiseEntity.setUserId(requestActionBy);
                                collectionLimitUserWiseEntity.setTotalLimitValue(totalLimitValue);
                                collectionLimitUserWiseEntity.setUtilizedLimitValue(amount);
                                collectionLimitUserWiseRepository.save(collectionLimitUserWiseEntity);
                            }
                            saveReceiptTransferData(receiptTransferStatusUpdateDtoRequest, receiptTransferEntity, collectionActivityLogsId);
                            // set Activity remarks
                            setRemarks(receiptTransferStatusUpdateDtoRequest, collectionActivityLogsId);
                        } else {
                            throw new Exception("1016029");
                        }
                        break;
                    case RECEIPT_TRANSFER_REJECT:
                        if (receiptTransferEntityTransferredToUserId.equals(requestActionBy)) {
                            saveReceiptTransferData(receiptTransferStatusUpdateDtoRequest, receiptTransferEntity, collectionActivityLogsId);
                            // history entity rows delete on rejecting the transfer
                            List<ReceiptTransferHistoryEntity> receiptTransferHistoryEntityList = receiptTransferHistoryRepository.getReceiptTransferHistoryDataByReceiptTransferId(receiptTransferId);
                            for (ReceiptTransferHistoryEntity receiptTransferHistoryEntity : receiptTransferHistoryEntityList) {
                                receiptTransferHistoryEntity.setDeleted(true);
                            }
                            // set Activity remarks
                            setRemarks(receiptTransferStatusUpdateDtoRequest, collectionActivityLogsId);
                        } else {
                            throw new Exception("1016029");
                        }
                        break;
                    default:
                        throw new Exception("1016032");
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

    public void setRemarks(ReceiptTransferStatusUpdateDtoRequest receiptTransferStatusUpdateDtoRequest, Long collectionActivityId) {
        CollectionActivityLogsEntity collectionActivityLogsEntity1 = collectionActivityLogsRepository.findByCollectionActivityLogsId(collectionActivityId);
        String remarks = receiptTransferStatusUpdateDtoRequest.getActivityLog().getRemarks();
        String lastWord = remarks.substring(remarks.lastIndexOf(" ")+1);
        String updatedRemarks = TRANSFER_STATUS;
        updatedRemarks = updatedRemarks.replace("{transfer_request}", receiptTransferStatusUpdateDtoRequest.getReceiptTransferId().toString());
        updatedRemarks = updatedRemarks.replace("{transfer_action}", receiptTransferStatusUpdateDtoRequest.getStatus());
        updatedRemarks = (updatedRemarks + lastWord);
        collectionActivityLogsEntity1.setRemarks(updatedRemarks);
        collectionActivityLogsRepository.save(collectionActivityLogsEntity1);
    }

    public ReceiptTransferResponseDTO getReceiptTransferById(Long receiptTransferId, Long userId) throws Exception {
        log.info("receipt tranfer idddd {}", receiptTransferId);
        ReceiptTransferEntity receiptTransferEntity;
        ReceiptTransferResponseDTO receiptTransferResponseDTO = new ReceiptTransferResponseDTO();
        try {
            receiptTransferEntity = receiptTransferRepository.findById(receiptTransferId).get();
            Long receiptTransferToUserId = receiptTransferEntity.getTransferredToUserId();
            Long receiptTrasnferByUserId = receiptTransferEntity.getTransferredBy();

            List<Map<String, Object>> receiptsData = receiptTransferRepository.getDataByReceiptTransferId(receiptTransferId);
            CollectionLimitUserWiseEntity collectionLimitUserWiseEntity = collectionLimitUserWiseRepository.getCollectionLimitUserWiseByUserId(userId, receiptTransferEntity.getTransferMode());
            //  flagg //
            // temporary work for user data //
            Map<String, Object> transferToUserData = null;
            Map<String, Object> transferByUserData = null;
            if (receiptTransferToUserId != null) {
                transferToUserData = receiptTransferRepository.getUserDataByUserId(receiptTransferToUserId);
                transferByUserData = receiptTransferRepository.getUserDataByUserId(receiptTrasnferByUserId);
            }
            receiptTransferResponseDTO.setTransferToUserData(transferToUserData);
            receiptTransferResponseDTO.setTransferByUserData(transferByUserData);
            // temporary work for user data //
            receiptTransferResponseDTO.setReceiptTransferData(receiptTransferEntity);
            receiptTransferResponseDTO.setReceiptData(receiptsData);
            if (collectionLimitUserWiseEntity != null) {
                receiptTransferResponseDTO.setAmountInHand(collectionLimitUserWiseEntity.getUtilizedLimitValue());
            } else {
                receiptTransferResponseDTO.setAmountInHand(0.0);
            }

        } catch (Exception e) {
            throw new Exception("1016028");
        }
        return receiptTransferResponseDTO;
    }


    public List<Map<String, Object>> getReceiptTransferByUserId(Long transferredBy, Date fromDate, Date endDate, String status, Integer pageNo, Integer pageSize) throws Exception {
        List<Map<String, Object>> receiptTransferEntity;
        try {
            Date toDate = utilityService.addOneDay(endDate);
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

    public Map<String, List<Map<String, Object>>> getReceiptTransferByUserIdWithAllStatus(Long transferredBy, Date fromDate, Date endDate, Integer pageNo, Integer pageSize) throws Exception {
        List<Map<String, Object>> transfer;
        List<Map<String, Object>> receiver;
        Map<String, List<Map<String, Object>>> newObjResponse = new HashMap<>();
        try {
            Date toDate = utilityService.addOneDay(endDate);
            Pageable pageRequest;
            if (pageNo > 0) {
                pageNo = pageNo - 1;
            }
            pageRequest = PageRequest.of(pageNo, pageSize);
            receiver = receiptTransferRepository.getReceiptTransferByReceiverUserIdWithAllStatus(transferredBy, fromDate, toDate, pageRequest);
            transfer = receiptTransferRepository.getReceiptTransferByTransferUserIdWithAllStatus(transferredBy, fromDate, toDate, pageRequest);
            newObjResponse.put("receiver", receiver);
            newObjResponse.put("transfer", transfer);
        } catch (Exception e) {
            throw new Exception("1016028");
        }
        return newObjResponse;
    }

}