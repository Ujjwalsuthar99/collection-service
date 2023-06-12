package com.synoriq.synofin.collection.collectionservice.implementation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.synoriq.synofin.collection.collectionservice.entity.*;
import com.synoriq.synofin.collection.collectionservice.repository.*;
import com.synoriq.synofin.collection.collectionservice.rest.request.ReceiptTransferDtoRequest;
import com.synoriq.synofin.collection.collectionservice.rest.request.ReceiptTransferStatusUpdateDtoRequest;
import com.synoriq.synofin.collection.collectionservice.rest.response.BaseDTOResponse;
import com.synoriq.synofin.collection.collectionservice.rest.response.ReceiptTransferDTOs.ReceiptTransferCustomDataResponseDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.ReceiptTransferDTOs.ReceiptTransferDataByReceiptIdResponseDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.ReceiptTransferDTOs.ReceiptTransferReceiptDataResponseDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.ReceiptTransferDTOs.ReceiptsDataResponseDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.ReceiptTransferResponseDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.UserDetailsByUserIdDTOs.UserDataReturnResponseDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.UserDetailsByUserIdDTOs.UserDetailByUserIdDTOResponse;
import com.synoriq.synofin.collection.collectionservice.service.ActivityLogService;
import com.synoriq.synofin.collection.collectionservice.service.UtilityService;
import com.synoriq.synofin.collection.collectionservice.service.ReceiptTransferService;
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
import static com.synoriq.synofin.collection.collectionservice.common.GlobalVariables.*;

@Service
@Slf4j
@Transactional
public class ReceiptTransferServiceImpl implements ReceiptTransferService {
    @Autowired
    private ReceiptRepository receiptRepository;
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

    @Override
    @Transactional
    public BaseDTOResponse<Object> createReceiptTransfer(@RequestBody ReceiptTransferDtoRequest receiptTransferDtoRequest, String token) throws Exception {

        BaseDTOResponse<Object> baseResponse;
        ResponseEntity<Object> response = null;
        try {
            Long receiptTransferTableId = receiptTransferDtoRequest.getReceiptTransferId();
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
            if (!Objects.equals(receiptTransferDtoRequest.getTransferType(), "bank")) {
                if ((utilizedAmount + transferredAmount) < totalLimitValue) {
                    Long collectionActivityId = activityLogService.createActivityLogs(receiptTransferDtoRequest.getActivityData(), token);

                    ReceiptTransferEntity receiptTransferEntity = saveReceiptTransferData(receiptTransferDtoRequest, collectionActivityId);
                    CollectionActivityLogsEntity collectionActivityLogsEntity1 = collectionActivityLogsRepository.findByCollectionActivityLogsId(collectionActivityId);
                    String remarks = receiptTransferDtoRequest.getActivityData().getRemarks();
                    String lastWord = remarks.substring(remarks.lastIndexOf(" ") + 1);
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
                            receiptTransferHistoryEntity.setDeleted(false);
                            receiptTransferHistoryRepository.save(receiptTransferHistoryEntity);
                        }
                    } else {
                        List<ReceiptTransferHistoryEntity> receiptTransferHistoryEntityList;
                        receiptTransferHistoryEntityList = receiptTransferHistoryRepository.getReceiptTransferHistoryDataByReceiptTransferId(receiptTransferTableId);
                        for (ReceiptTransferHistoryEntity receiptTransferHistoryEntity : receiptTransferHistoryEntityList) {

                            ReceiptTransferHistoryEntity receiptTransferHistoryEntityToBeSaved = new ReceiptTransferHistoryEntity();
                            receiptTransferHistoryEntityToBeSaved.setReceiptTransferId(receiptTransferEntity.getReceiptTransferId());
                            receiptTransferHistoryEntityToBeSaved.setDeleted(false);
                            receiptTransferHistoryEntityToBeSaved.setCollectionReceiptsId(receiptTransferHistoryEntity.getCollectionReceiptsId());
                            receiptTransferHistoryRepository.save(receiptTransferHistoryEntityToBeSaved);
                        }
                    }
                    baseResponse = new BaseDTOResponse<Object>(receiptTransferEntity);

                } else {
                    throw new Exception("1016031");
                }
            } else {
                Long collectionActivityId = activityLogService.createActivityLogs(receiptTransferDtoRequest.getActivityData(), token);


                ReceiptTransferEntity receiptTransferEntity = saveReceiptTransferData(receiptTransferDtoRequest, collectionActivityId);

                CollectionActivityLogsEntity collectionActivityLogsEntity1 = collectionActivityLogsRepository.findByCollectionActivityLogsId(collectionActivityId);
                String remarks = receiptTransferDtoRequest.getActivityData().getRemarks();
                String lastWord = remarks.substring(remarks.lastIndexOf(" ") + 1);
                updatedRemarks = CREATE_TRANSFER;
                updatedRemarks = updatedRemarks.replace("{transfer_request}", receiptTransferEntity.getReceiptTransferId().toString());
                updatedRemarks = (updatedRemarks + lastWord);
                collectionActivityLogsEntity1.setRemarks(updatedRemarks);
                collectionActivityLogsRepository.save(collectionActivityLogsEntity1);
                for (Long receiptTransferId : receiptTransferDtoRequest.getReceipts()) {

                    ReceiptTransferHistoryEntity receiptTransferHistoryEntity = new ReceiptTransferHistoryEntity();

                    receiptTransferHistoryEntity.setReceiptTransferId(receiptTransferEntity.getReceiptTransferId());
                    receiptTransferHistoryEntity.setCollectionReceiptsId(receiptTransferId);
                    receiptTransferHistoryEntity.setDeleted(false);
                    receiptTransferHistoryRepository.save(receiptTransferHistoryEntity);
                }
                baseResponse = new BaseDTOResponse<Object>(receiptTransferEntity);
            }
        } catch (Exception ee) {
            log.error("RestControllers error occurred for vanWebHookDetails: {} ->  {}", ee.getMessage());
            throw new Exception(ee.getMessage());
        }
        return baseResponse;
    }

    @Override
    public List<ReceiptTransferDTO> getReceiptTransferSummary(Long transferredByUserId) {
        List<ReceiptTransferEntity> receiptTransferEntityList = receiptTransferRepository.getReceiptTransferSummaryByTransferredBy(transferredByUserId);
        List<ReceiptTransferDTO> receiptTransferDTOList = new ArrayList<>();
        for (ReceiptTransferEntity receiptTransferEntity : receiptTransferEntityList) {
            ReceiptTransferDTO receiptTransferDTO = new ReceiptTransferDTO();
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
    public ReceiptTransferEntity statusUpdate(ReceiptTransferStatusUpdateDtoRequest receiptTransferStatusUpdateDtoRequest, String token) throws Exception {
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

            Long collectionActivityLogsId = activityLogService.createActivityLogs(receiptTransferStatusUpdateDtoRequest.getActivityLog(), token);


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
//                                    collectionReceiptEntity.setCollectionActivityLogsId(collectionActivityLogsId);
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

    @Override
    public void saveReceiptTransferData(ReceiptTransferStatusUpdateDtoRequest receiptTransferStatusUpdateDtoRequest, ReceiptTransferEntity receiptTransferEntity, Long collectionActivityLogsId)
            throws Exception {
        receiptTransferEntity.setStatus(receiptTransferStatusUpdateDtoRequest.getStatus());
        receiptTransferEntity.setActionDatetime(new Date());
        receiptTransferEntity.setActionReason(receiptTransferStatusUpdateDtoRequest.getActionReason());
        receiptTransferEntity.setActionRemarks(receiptTransferStatusUpdateDtoRequest.getActionRemarks());
        receiptTransferEntity.setActionBy(receiptTransferStatusUpdateDtoRequest.getActionBy());
        receiptTransferEntity.setCollectionActivityLogsId(collectionActivityLogsId);
        receiptTransferEntity.setReceiptImage(receiptTransferStatusUpdateDtoRequest.getImages());
        receiptTransferRepository.save(receiptTransferEntity);
    }
    @Override
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
    @Override
    public ReceiptTransferResponseDTO getReceiptTransferById(String token ,Long receiptTransferId, Long userId) throws Exception {
        log.info("receipt tranfer idddd {}", receiptTransferId);
        ReceiptTransferEntity receiptTransferEntity;
        boolean buttonRestriction = false;
        Map<String, Object> bankData = null;
        ReceiptTransferResponseDTO receiptTransferResponseDTO = new ReceiptTransferResponseDTO();
        try {
            receiptTransferEntity = receiptTransferRepository.findById(receiptTransferId).get();
            Long receiptTransferToUserId = receiptTransferEntity.getTransferredToUserId();
            Long receiptTransferByUserId = receiptTransferEntity.getTransferredBy();
            if (receiptTransferEntity.getTransferBankCode() != null && !Objects.equals(receiptTransferEntity.getTransferBankCode(), "")) {
                bankData = receiptTransferRepository.getBankData(Long.parseLong(receiptTransferEntity.getTransferBankCode()));
            }
            List<Map<String, Object>> receiptsData = receiptTransferRepository.getDataByReceiptTransferId(receiptTransferId);
            CollectionLimitUserWiseEntity collectionLimitUserWiseEntity = collectionLimitUserWiseRepository.getCollectionLimitUserWiseByUserId(userId, receiptTransferEntity.getTransferMode());

            List<ReceiptTransferHistoryEntity> receiptTransferHistoryEntityList = receiptTransferHistoryRepository.getReceiptTransferHistoryDataByReceiptTransferId(receiptTransferId);
            Long receiptId =  receiptTransferHistoryEntityList.get(0).getCollectionReceiptsId();
            List<ReceiptTransferHistoryEntity> receiptTransferHistoryEntity = receiptTransferHistoryRepository.buttonRestriction(receiptTransferId, receiptId);

            if (!receiptTransferHistoryEntity.isEmpty()) {
                buttonRestriction = true;
            }
            UserDetailByUserIdDTOResponse transferToUserData;
            UserDetailByUserIdDTOResponse transferByUserData;
            UserDataReturnResponseDTO returnTransferToUserData = new UserDataReturnResponseDTO();
            UserDataReturnResponseDTO returnTransferByUserData = new UserDataReturnResponseDTO();
            if (receiptTransferToUserId != null) {
                //  transfer to
                transferToUserData = utilityService.getUserDetailsByUserId(token, receiptTransferToUserId);
                returnTransferToUserData.setUserId(transferToUserData.getData().getId());
                returnTransferToUserData.setUserName(transferToUserData.getData().getEmployeeUserName());
                returnTransferToUserData.setDepartment(transferToUserData.getData().getDepartment());
                returnTransferToUserData.setName(transferToUserData.getData().getEmployeeName());

                //  transfer by
                transferByUserData = utilityService.getUserDetailsByUserId(token, receiptTransferByUserId);
                returnTransferByUserData.setUserId(transferByUserData.getData().getId());
                returnTransferByUserData.setUserName(transferByUserData.getData().getEmployeeUserName());
                returnTransferByUserData.setDepartment(transferByUserData.getData().getDepartment());
                returnTransferByUserData.setName(transferByUserData.getData().getEmployeeName());

            }
            receiptTransferResponseDTO.setTransferToUserData(returnTransferToUserData);
            receiptTransferResponseDTO.setTransferByUserData(returnTransferByUserData);
            receiptTransferResponseDTO.setButtonRestriction(buttonRestriction);
            receiptTransferResponseDTO.setReceiptTransferData(receiptTransferEntity);
            receiptTransferResponseDTO.setBankData(bankData);
            receiptTransferResponseDTO.setReceiptData(receiptsData);
            if (collectionLimitUserWiseEntity != null) {
                receiptTransferResponseDTO.setAmountInHand(collectionLimitUserWiseEntity.getUtilizedLimitValue());
            } else {
                receiptTransferResponseDTO.setAmountInHand(0.0);
            }

        } catch (Exception e) {
            log.info("error {}", e);
            e.printStackTrace();
            throw new Exception("1016028");
        }
        return receiptTransferResponseDTO;
    }
    @Override
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
    @Override
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

    @Override
    public ReceiptTransferDataByReceiptIdResponseDTO getReceiptTransferByReceiptId(String token, Long receiptId) throws Exception {
        List<Map<String, Object>> receiptTransferDataList;
        Map<String, Object> receiptData;
        List<ReceiptTransferCustomDataResponseDTO> userTransferArr = new ArrayList<>();
        List<ReceiptTransferCustomDataResponseDTO> bankTransferArr = new ArrayList<>();
        ReceiptTransferDataByReceiptIdResponseDTO receiptTransferDataByReceiptIdResponseDTO = new ReceiptTransferDataByReceiptIdResponseDTO();
        ReceiptTransferReceiptDataResponseDTO receiptTransferReceiptDataResponseDTO = new ReceiptTransferReceiptDataResponseDTO();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String receiptTransferReadOnlyMode = collectionConfigurationsRepository.findConfigurationValueByConfigurationName(RECEIPT_TRANSFER_MODE_READ_ONLY);
            if (receiptTransferReadOnlyMode.equals("true")) {
                receiptTransferDataByReceiptIdResponseDTO.setTransferHistoryButton(false);
            } else {
                receiptTransferDataByReceiptIdResponseDTO.setTransferHistoryButton(true);
            }
            receiptData = receiptRepository.getReceiptDataByReceiptId(receiptId);
            JsonNode geoLocationDataNode1 = objectMapper.readTree(String.valueOf(receiptData.get("geo_location_data")));
            JsonNode imagesNode1 = objectMapper.readTree(String.valueOf(receiptData.get("images")));
            Map<String, String> obj = new HashMap<>();
            for (int i = 1; i <= imagesNode1.size(); i++) {
                String sr = String.valueOf(imagesNode1.get("url" + i));
                String newStr = "bankDepositSlip/" + receiptData.get("created_by") + "/" + new Gson().fromJson(String.valueOf(sr), String.class);
                obj.put("url" + i, newStr);
            }
            receiptTransferReceiptDataResponseDTO.setImages(obj);
            receiptTransferReceiptDataResponseDTO.setLocation(new Gson().fromJson(String.valueOf(geoLocationDataNode1), Object.class));

            receiptTransferDataList = receiptTransferHistoryRepository.getReceiptTransferByReceiptId(receiptId);
            for (Map<String, Object> receiptTransferData : receiptTransferDataList) {
                JsonNode geoLocationDataNode = objectMapper.readTree(String.valueOf(receiptTransferData.get("geo_location_data")));
                JsonNode imagesNode = objectMapper.readTree(String.valueOf(receiptTransferData.get("receipt_image")));
                if (String.valueOf(receiptTransferData.get("transfer_type")).equals("bank")) {
                    ReceiptTransferCustomDataResponseDTO bankTransferDTO = new ReceiptTransferCustomDataResponseDTO();
                    bankTransferDTO.setReceiptTransferId(Long.parseLong(String.valueOf(receiptTransferData.get("receipt_transfer_id"))));
                    bankTransferDTO.setCreatedDate(String.valueOf(receiptTransferData.get("created_date")));
                    bankTransferDTO.setTransferByName(String.valueOf(receiptTransferData.get("transfer_by_name")));
                    bankTransferDTO.setTransferToName(String.valueOf(receiptTransferData.get("transfer_to_name")));
                    bankTransferDTO.setTransferType(String.valueOf(receiptTransferData.get("transfer_type")));
                    bankTransferDTO.setDepositAmount(Double.parseDouble(String.valueOf(receiptTransferData.get("deposit_amount"))));
                    bankTransferDTO.setBankName(String.valueOf(receiptTransferData.get("bank_name")));
                    bankTransferDTO.setAccountNumber(String.valueOf(receiptTransferData.get("account_number")));
                    bankTransferDTO.setGeolocation(new Gson().fromJson(String.valueOf(geoLocationDataNode), Object.class));
                    bankTransferDTO.setReceiptTransferProofs(new Gson().fromJson(String.valueOf(imagesNode), Object.class));
                    bankTransferArr.add(bankTransferDTO);
                } else {
                    ReceiptTransferCustomDataResponseDTO userTransferDTO = new ReceiptTransferCustomDataResponseDTO();
                    userTransferDTO.setReceiptTransferId(Long.parseLong(String.valueOf(receiptTransferData.get("receipt_transfer_id"))));
                    userTransferDTO.setCreatedDate(String.valueOf(receiptTransferData.get("created_date")));
                    userTransferDTO.setTransferByName(String.valueOf(receiptTransferData.get("transfer_by_name")));
                    userTransferDTO.setTransferToName(String.valueOf(receiptTransferData.get("transfer_to_name")));
                    userTransferDTO.setTransferType(String.valueOf(receiptTransferData.get("transfer_type")));
                    userTransferDTO.setDepositAmount(Double.parseDouble(String.valueOf(receiptTransferData.get("deposit_amount"))));
                    userTransferDTO.setBankName(null);
                    userTransferDTO.setStatus(utilityService.capitalizeName(String.valueOf(receiptTransferData.get("status"))));
                    userTransferDTO.setAccountNumber(null);
                    userTransferDTO.setGeolocation(new Gson().fromJson(String.valueOf(geoLocationDataNode), Object.class));
                    userTransferDTO.setReceiptTransferProofs(new Gson().fromJson(String.valueOf(imagesNode), Object.class));
                    userTransferArr.add(userTransferDTO);
                }
            }
            receiptTransferDataByReceiptIdResponseDTO.setUserTransfer(userTransferArr);
            receiptTransferDataByReceiptIdResponseDTO.setBankTransfer(bankTransferArr);
            receiptTransferDataByReceiptIdResponseDTO.setReceiptData(receiptTransferReceiptDataResponseDTO);
        } catch (Exception e) {
            throw new Exception("1016028");
        }
        return receiptTransferDataByReceiptIdResponseDTO;
    }

    @Override
    public List<ReceiptsDataResponseDTO> getReceiptsDataByReceiptTransferId(String Token, Long receiptTransferId) throws Exception {
        List<Map<String, Object>> receiptsData;
        List<ReceiptsDataResponseDTO> receiptsDataList = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            receiptsData = receiptTransferRepository.getReceiptsDataByReceiptTransferId(receiptTransferId);
            for (Map<String, Object> receipt : receiptsData) {
                ReceiptsDataResponseDTO receiptsDataResponseDTO = new ReceiptsDataResponseDTO();
                receiptsDataResponseDTO.setReceiptId(Long.parseLong(String.valueOf(receipt.get("receipt_id"))));
                receiptsDataResponseDTO.setCreatedDate(String.valueOf(receipt.get("created_date")));
                receiptsDataResponseDTO.setReceiptAmount(Double.parseDouble(String.valueOf(receipt.get("receipt_amount"))));
                receiptsDataResponseDTO.setLoanApplicationNumber(String.valueOf(receipt.get("loan_application_number")));
                receiptsDataResponseDTO.setCreatedBy(String.valueOf(receipt.get("created_by")));
                receiptsDataResponseDTO.setStatus(utilityService.capitalizeName(String.valueOf(receipt.get("status"))));

                JsonNode geoLocationDataNode = objectMapper.readTree(String.valueOf(receipt.get("geo_location_data")));
                JsonNode imagesNode = objectMapper.readTree(String.valueOf(receipt.get("receipt_images")));
                Map<String, String> imagesObj = new HashMap<>();
                for (int i = 1; i <= imagesNode.size(); i++) {
                    String imageVal = String.valueOf(imagesNode.get("url" + i));
                    String updatedImageVal = "bankDepositSlip/" + receiptsDataResponseDTO.getCreatedBy() + "/" + new Gson().fromJson(String.valueOf(imageVal), String.class);
                    imagesObj.put("url" + i, updatedImageVal);
                }

                receiptsDataResponseDTO.setReceiptImages(imagesObj);
                receiptsDataResponseDTO.setGeoLocationData(new Gson().fromJson(String.valueOf(geoLocationDataNode), Object.class));
                receiptsDataList.add(receiptsDataResponseDTO);
            }

        } catch (Exception e) {
            throw new Exception("1016028");
        }
        return receiptsDataList;
    }

    private ReceiptTransferEntity saveReceiptTransferData(ReceiptTransferDtoRequest receiptTransferDtoRequest, Long collectionActivityId) {
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
        receiptTransferEntity.setRemarks(receiptTransferDtoRequest.getRemarks());
        receiptTransferEntity.setTransferBankCode(receiptTransferDtoRequest.getTransferBankCode());
        receiptTransferEntity.setActionDatetime(receiptTransferDtoRequest.getActionDatetime());
        receiptTransferEntity.setActionReason(receiptTransferDtoRequest.getActionReason());
        receiptTransferEntity.setActionRemarks(receiptTransferDtoRequest.getActionRemarks());
        receiptTransferEntity.setActionBy(receiptTransferDtoRequest.getActionBy());
        receiptTransferEntity.setCollectionActivityLogsId(collectionActivityId);

        receiptTransferRepository.save(receiptTransferEntity);
        return receiptTransferEntity;
    }
}