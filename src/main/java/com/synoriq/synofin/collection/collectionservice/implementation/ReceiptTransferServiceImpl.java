package com.synoriq.synofin.collection.collectionservice.implementation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.synoriq.synofin.collection.collectionservice.common.EnumSQLConstants;
import com.synoriq.synofin.collection.collectionservice.config.oauth.CurrentUserInfo;
import com.synoriq.synofin.collection.collectionservice.entity.*;
import com.synoriq.synofin.collection.collectionservice.repository.*;
import com.synoriq.synofin.collection.collectionservice.rest.request.ReceiptTransferAirtelDepositStatusRequestDTO;
import com.synoriq.synofin.collection.collectionservice.rest.request.ReceiptTransferDtoRequest;
import com.synoriq.synofin.collection.collectionservice.rest.request.ReceiptTransferStatusUpdateDtoRequest;
import com.synoriq.synofin.collection.collectionservice.rest.request.depositInvoiceDTOs.DepositInvoiceRequestDTO;
import com.synoriq.synofin.collection.collectionservice.rest.request.depositInvoiceDTOs.DepositInvoiceWrapperRequestDTO;
import com.synoriq.synofin.collection.collectionservice.rest.request.depositInvoiceDTOs.DepositInvoiceWrapperRequestDataDTO;
import com.synoriq.synofin.collection.collectionservice.rest.request.depositInvoiceDTOs.DepositInvoiceWrapperRequestListDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.BaseDTOResponse;
import com.synoriq.synofin.collection.collectionservice.rest.response.DepositInvoiceResponseDTOs.DepositInvoiceResponseDataDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.DepositInvoiceResponseDTOs.DepositInvoiceWrapperResponseDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.ReceiptTransferDTOs.*;
import com.synoriq.synofin.collection.collectionservice.rest.response.ReceiptTransferResponseDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.UserDetailByTokenDTOs.UserDetailByTokenDTOResponse;
import com.synoriq.synofin.collection.collectionservice.rest.response.UserDetailsByUserIdDTOs.UserDataReturnResponseDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.UserDetailsByUserIdDTOs.UserDetailByUserIdDTOResponse;
import com.synoriq.synofin.collection.collectionservice.service.ActivityLogService;
import com.synoriq.synofin.collection.collectionservice.service.ConsumedApiLogService;
import com.synoriq.synofin.collection.collectionservice.service.UtilityService;
import com.synoriq.synofin.collection.collectionservice.service.ReceiptTransferService;
import com.synoriq.synofin.collection.collectionservice.service.utilityservice.HTTPRequestService;
import com.synoriq.synofin.dataencryptionservice.service.RSAUtils;
import com.synoriq.synofin.lms.commondto.dto.collection.ReceiptTransferDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
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
    private CurrentUserInfo currentUserInfo;
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
    DigitalPaymentTransactionsRepository digitalPaymentTransactionsRepository;
    @Autowired
    private RSAUtils rsaUtils;
    @Autowired
    ConsumedApiLogService consumedApiLogService;

    @Autowired
    private ActivityLogService activityLogService;

    @Override
    @Transactional
    public BaseDTOResponse<Object> createReceiptTransfer(@RequestBody ReceiptTransferDtoRequest receiptTransferDtoRequest, String token) throws Exception {

        BaseDTOResponse<Object> baseResponse;
        try {
            Long receiptTransferTableId = receiptTransferDtoRequest.getReceiptTransferId();
            String limitConf;
            String updatedRemarks;

            Double utilizedAmount;
            Double totalLimitValue;
            Double transferredAmount = receiptTransferDtoRequest.getAmount();
            Long transferredToID = receiptTransferDtoRequest.getTransferredToUserId();
            String transferMode = receiptTransferDtoRequest.getTransferMode();
            if (transferMode.equals("cash")) {
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
                    baseResponse = new BaseDTOResponse<>(receiptTransferEntity);

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
                baseResponse = new BaseDTOResponse<>(receiptTransferEntity);
            }
        } catch (Exception ee) {
            log.error("RestControllers error occurred for vanWebHookDetails {}", ee.getMessage());
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
                                Map<String, Object> receiptDataByServiceId = receiptRepository.getLoanIdByServiceId(collectionReceiptId);
                                if (Objects.equals(receiptDataByServiceId.get("status").toString(), "approved")) {
                                    amount = amount - Double.parseDouble(receiptDataByServiceId.get("receiptAmount").toString());
                                }
                                if (collectionReceiptEntity != null) {
                                    collectionReceiptEntity.setLastReceiptTransferId(receiptTransferId);
                                    collectionReceiptEntity.setReceiptHolderUserId(requestActionBy);
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
                                String limitConf;
                                if (receiptTransferEntityTransferMode.equals("cash")) {
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
        receiptTransferEntity.setActionActivityLogsId(collectionActivityLogsId);
        receiptTransferEntity.setReceiptImage(receiptTransferStatusUpdateDtoRequest.getImages());
        receiptTransferRepository.save(receiptTransferEntity);
    }

    @Override
    public void setRemarks(ReceiptTransferStatusUpdateDtoRequest receiptTransferStatusUpdateDtoRequest, Long collectionActivityId) {
        CollectionActivityLogsEntity collectionActivityLogsEntity1 = collectionActivityLogsRepository.findByCollectionActivityLogsId(collectionActivityId);
        String remarks = receiptTransferStatusUpdateDtoRequest.getActivityLog().getRemarks();
        String lastWord = remarks.substring(remarks.lastIndexOf(" ") + 1);
        String updatedRemarks = TRANSFER_STATUS;
        updatedRemarks = updatedRemarks.replace("{transfer_request}", receiptTransferStatusUpdateDtoRequest.getReceiptTransferId().toString());
        updatedRemarks = updatedRemarks.replace("{transfer_action}", receiptTransferStatusUpdateDtoRequest.getStatus());
        updatedRemarks = (updatedRemarks + lastWord);
        collectionActivityLogsEntity1.setRemarks(updatedRemarks);
        collectionActivityLogsRepository.save(collectionActivityLogsEntity1);
    }

    @Override
    public BaseDTOResponse<Object> getReceiptTransferById(String token, Long receiptTransferId, Long userId) throws Exception {
        log.info("receipt tranfer idddd {}", receiptTransferId);
        ReceiptTransferEntity receiptTransferEntity;
        boolean buttonRestriction = false;
        Map<String, Object> bankData = null;
        ReceiptTransferResponseDTO receiptTransferResponseDTO = new ReceiptTransferResponseDTO();
        try {
            if(userId == 00000L) {
                ReceiptTransferEntity receiptTransferEntity1 = receiptTransferRepository.findByReceiptTransferId(receiptTransferId);
                return new BaseDTOResponse<>(receiptTransferEntity1);
            }
            String encryptionKey = rsaUtils.getEncryptionKey(currentUserInfo.getClientId());
            String password = rsaUtils.getPassword(currentUserInfo.getClientId());
//            Boolean piiPermission = rsaUtils.getPiiPermission();
            Boolean piiPermission = true;
            receiptTransferEntity = receiptTransferRepository.findById(receiptTransferId).get();
            Long receiptTransferToUserId = receiptTransferEntity.getTransferredToUserId();
            Long receiptTransferByUserId = receiptTransferEntity.getTransferredBy();
            if (receiptTransferEntity.getTransferBankCode() != null && !Objects.equals(receiptTransferEntity.getTransferBankCode(), "")) {
                bankData = receiptTransferRepository.getBankData(Long.parseLong(receiptTransferEntity.getTransferBankCode()));
            }
            List<Map<String, Object>> receiptsData = receiptTransferRepository.getDataByReceiptTransferId(receiptTransferId, encryptionKey, password, piiPermission);
            CollectionLimitUserWiseEntity collectionLimitUserWiseEntity = collectionLimitUserWiseRepository.getCollectionLimitUserWiseByUserId(userId, receiptTransferEntity.getTransferMode());

            List<ReceiptTransferHistoryEntity> receiptTransferHistoryEntityList = receiptTransferHistoryRepository.getReceiptTransferHistoryDataByReceiptTransferId(receiptTransferId);
            Long receiptId = receiptTransferHistoryEntityList.get(0).getCollectionReceiptsId();
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
            log.info("error", e);
            e.printStackTrace();
            throw new Exception("1016028");
        }
        return new BaseDTOResponse<>(receiptTransferResponseDTO);
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
            receiptTransferDataByReceiptIdResponseDTO.setTransferHistoryButton(!receiptTransferReadOnlyMode.equals("true"));
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
            receiptTransferReceiptDataResponseDTO.setName(String.valueOf(receiptData.get("full_name")));
            receiptTransferReceiptDataResponseDTO.setUserName(String.valueOf(receiptData.get("created_by")));

            receiptTransferDataList = receiptTransferHistoryRepository.getReceiptTransferByReceiptId(receiptId);
            for (Map<String, Object> receiptTransferData : receiptTransferDataList) {
                JsonNode geoLocationDataNode = objectMapper.readTree(String.valueOf(receiptTransferData.get("transfer_location_data")));
                JsonNode approvalLocationDataNode = objectMapper.readTree(String.valueOf(receiptTransferData.get("approval_location_data")));
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
                    bankTransferDTO.setTransferLocationData(new Gson().fromJson(String.valueOf(geoLocationDataNode), Object.class));
                    bankTransferDTO.setApprovalLocationData(new Gson().fromJson("{}", Object.class));
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
                    userTransferDTO.setTransferLocationData(new Gson().fromJson(String.valueOf(geoLocationDataNode), Object.class));
                    userTransferDTO.setApprovalLocationData(new Gson().fromJson(String.valueOf(approvalLocationDataNode), Object.class));
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
    public AllBankTransferResponseDTO getAllBankTransfers(String token, String searchKey, String status, Integer pageNo, Integer pageSize) throws Exception {
        List<Map<String, Object>> receiptTransferDataList;
        List<ReceiptTransferCustomDataResponseDTO> bankTransferArr = new ArrayList<>();
        AllBankTransferResponseDTO allBankTransferResponseDTO = new AllBankTransferResponseDTO();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Pageable pageable = PageRequest.of(pageNo, pageSize);

            List<String> statusList;
            if (Objects.equals(status, "pending")) {
                statusList = Collections.singletonList("pending");
            } else {
                statusList = Arrays.asList("approved", "cancelled", "rejected");
            }
            receiptTransferDataList = receiptTransferHistoryRepository.getAllBankTransfers(statusList, searchKey, pageable);
            if (receiptTransferDataList.size() > 0) {
                for (Map<String, Object> receiptTransferData : receiptTransferDataList) {
                    JsonNode geoLocationDataNode = objectMapper.readTree(String.valueOf(receiptTransferData.get("transfer_location_data")));
                    JsonNode imagesNode = objectMapper.readTree(String.valueOf(receiptTransferData.get("receipt_image")));
                    ReceiptTransferCustomDataResponseDTO bankTransferDTO = new ReceiptTransferCustomDataResponseDTO();
                    bankTransferDTO.setReceiptTransferId(Long.parseLong(String.valueOf(receiptTransferData.get("receipt_transfer_id"))));
                    bankTransferDTO.setCreatedDate(String.valueOf(receiptTransferData.get("created_date")));
                    bankTransferDTO.setTransferByName(String.valueOf(receiptTransferData.get("transfer_by_name")));
                    bankTransferDTO.setTransferToName(String.valueOf(receiptTransferData.get("transfer_to_name")));
                    bankTransferDTO.setTransferType(String.valueOf(receiptTransferData.get("transfer_type")));
                    bankTransferDTO.setApprovedBy(String.valueOf(receiptTransferData.get("approved_by")));
                    bankTransferDTO.setActionDateTime(String.valueOf(receiptTransferData.get("action_datetime")));
                    bankTransferDTO.setStatus(String.valueOf(receiptTransferData.get("status")));
                    bankTransferDTO.setDepositAmount(Double.parseDouble(String.valueOf(receiptTransferData.get("deposit_amount"))));
                    bankTransferDTO.setBankName(String.valueOf(receiptTransferData.get("bank_name")));
                    bankTransferDTO.setAccountNumber(String.valueOf(receiptTransferData.get("account_number")));
                    bankTransferDTO.setTransferLocationData(new Gson().fromJson(String.valueOf(geoLocationDataNode), Object.class));
                    bankTransferDTO.setApprovalLocationData(new Gson().fromJson("{}", Object.class));
                    bankTransferDTO.setReceiptTransferProofs(new Gson().fromJson(String.valueOf(imagesNode), Object.class));
                    bankTransferArr.add(bankTransferDTO);
                }

                allBankTransferResponseDTO.setData(bankTransferArr);
                allBankTransferResponseDTO.setTotalCount(Long.parseLong(String.valueOf(receiptTransferDataList.get(0).get("total_rows"))));
            } else {
                allBankTransferResponseDTO.setTotalCount(0L);
                allBankTransferResponseDTO.setData(new ArrayList<>());
                return allBankTransferResponseDTO;
            }
        } catch (Exception e) {
            throw new Exception("1016028");
        }
        return allBankTransferResponseDTO;
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
                receiptsDataResponseDTO.setPaymentMode(String.valueOf(receipt.get("payment_mode")));
                receiptsDataResponseDTO.setLoanId(Long.parseLong(String.valueOf(receipt.get("loan_id"))));
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

    @Override
    public DepositInvoiceResponseDataDTO depositInvoice(String bearerToken, DepositInvoiceRequestDTO depositInvoiceRequestDTO) throws Exception {
        List<Map<String, Object>> receiptsData;
        List<DepositInvoiceWrapperRequestDTO> depositInvoiceWrapperArr = new ArrayList<>();
        DepositInvoiceWrapperResponseDTO res = new DepositInvoiceWrapperResponseDTO();
        DepositInvoiceResponseDataDTO depositInvoiceResponseDataDTO = new DepositInvoiceResponseDataDTO();
        try {
            receiptsData = receiptTransferRepository.getReceiptsDataByReceiptTransferId(depositInvoiceRequestDTO.getReceiptTransferId());
            DepositInvoiceWrapperRequestDataDTO depositInvoiceWrapperRequestDataDTO = new DepositInvoiceWrapperRequestDataDTO();
            depositInvoiceWrapperRequestDataDTO.setFundTransferNumber(depositInvoiceRequestDTO.getUtrNumber());
            if (Objects.equals(depositInvoiceRequestDTO.getAction(), "approved")) {
                for (Map<String, Object> receiptData : receiptsData) {
                    DepositInvoiceWrapperRequestDTO depositInvoiceWrapperRequestDTO = new DepositInvoiceWrapperRequestDTO();
                    depositInvoiceWrapperRequestDTO.setAction(depositInvoiceRequestDTO.getAction());
                    depositInvoiceWrapperRequestDTO.setActionBy("checker");
                    depositInvoiceWrapperRequestDTO.setServiceRequestId(Long.parseLong(String.valueOf(receiptData.get("receipt_id"))));
                    depositInvoiceWrapperRequestDTO.setLoanId(Long.parseLong(String.valueOf(receiptData.get("loan_id"))));
                    depositInvoiceWrapperRequestDTO.setComment(depositInvoiceRequestDTO.getRemarks());
                    depositInvoiceWrapperRequestDTO.setReqSource("m_collect");
                    depositInvoiceWrapperRequestDTO.setReqData(depositInvoiceWrapperRequestDataDTO);
                    depositInvoiceWrapperArr.add(depositInvoiceWrapperRequestDTO);
                    log.info("depositInvoiceDTOdepositInvoiceDTO {}", depositInvoiceWrapperRequestDTO);
                }
            }
            DepositInvoiceWrapperRequestListDTO depositInvoiceWrapperRequestListDTO = new DepositInvoiceWrapperRequestListDTO();
            depositInvoiceWrapperRequestListDTO.setReceiptObjectData(depositInvoiceWrapperArr);
            System.out.println("depositInvoiceWrapperArr" + depositInvoiceWrapperArr);

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("Authorization", bearerToken);
            httpHeaders.add("Content-Type", "application/json");

            DepositInvoiceWrapperRequestListDTO depositInvoiceWrapperBody = new ObjectMapper().convertValue(depositInvoiceWrapperRequestListDTO, DepositInvoiceWrapperRequestListDTO.class);
            if (Objects.equals(depositInvoiceRequestDTO.getAction(), "approved")) {
                res = HTTPRequestService.<Object, DepositInvoiceWrapperResponseDTO>builder()
                        .httpMethod(HttpMethod.POST)
                        .url("http://localhost:1102/v1/depositChallanBulkAction")
                        .httpHeaders(httpHeaders)
                        .body(depositInvoiceWrapperBody)
                        .typeResponseType(DepositInvoiceWrapperResponseDTO.class)
                        .build().call();
            }
            log.info("res {}", res);
            UserDetailByTokenDTOResponse resp = utilityService.getUserDetailsByToken(bearerToken);
            CollectionActivityLogsEntity collectionActivityLogsEntity = new CollectionActivityLogsEntity();
            ReceiptTransferEntity receiptTransferEntity = receiptTransferRepository.findById(depositInvoiceRequestDTO.getReceiptTransferId()).get();
            String updatedRemarks = TRANSFER_STATUS;
            updatedRemarks = updatedRemarks.replace("{transfer_request}", depositInvoiceRequestDTO.getReceiptTransferId().toString());
            updatedRemarks = updatedRemarks.replace("{transfer_action}", depositInvoiceRequestDTO.getAction());
            updatedRemarks = (updatedRemarks + resp.getData().getUserName());

            collectionActivityLogsEntity.setActivityName("receipt_transfer_" + depositInvoiceRequestDTO.getAction());
            collectionActivityLogsEntity.setActivityDate(new Date());
            collectionActivityLogsEntity.setDeleted(false);
            collectionActivityLogsEntity.setActivityBy(resp.getData().getUserData().getUserId());
            collectionActivityLogsEntity.setDistanceFromUserBranch(0D);
            collectionActivityLogsEntity.setAddress("{}");
            collectionActivityLogsEntity.setRemarks(updatedRemarks);
            collectionActivityLogsEntity.setImages("{}");
            collectionActivityLogsEntity.setGeolocation("{}");
            collectionActivityLogsRepository.save(collectionActivityLogsEntity);
            List<ReceiptTransferHistoryEntity> receiptTransferHistoryEntityList;
            switch (depositInvoiceRequestDTO.getAction()) {
                case RECEIPT_TRANSFER_APPROVE:
                    receiptTransferHistoryEntityList = receiptTransferHistoryRepository.getReceiptTransferHistoryDataByReceiptTransferId(depositInvoiceRequestDTO.getReceiptTransferId());
                    for (ReceiptTransferHistoryEntity receiptTransferHistoryEntity : receiptTransferHistoryEntityList) {
                        Long collectionReceiptId = receiptTransferHistoryEntity.getCollectionReceiptsId();
                        CollectionReceiptEntity collectionReceiptEntity = collectionReceiptRepository.findByReceiptId(collectionReceiptId);
                        if (collectionReceiptEntity != null) {
                            collectionReceiptEntity.setLastReceiptTransferId(depositInvoiceRequestDTO.getReceiptTransferId());
                            collectionReceiptRepository.save(collectionReceiptEntity);
                        }
                    }
                    receiptTransferEntity.setStatus(depositInvoiceRequestDTO.getAction());
                    receiptTransferEntity.setActionDatetime(new Date());
                    receiptTransferEntity.setActionReason("");
                    receiptTransferEntity.setActionRemarks(depositInvoiceRequestDTO.getRemarks());
                    receiptTransferEntity.setActionBy(resp.getData().getUserData().getUserId());
                    receiptTransferEntity.setActionActivityLogsId(collectionActivityLogsEntity.getCollectionActivityLogsId());
                    receiptTransferRepository.save(receiptTransferEntity);
                    break;
                case RECEIPT_TRANSFER_REJECT:
                    receiptTransferHistoryEntityList = receiptTransferHistoryRepository.getReceiptTransferHistoryDataByReceiptTransferId(depositInvoiceRequestDTO.getReceiptTransferId());
                    for (ReceiptTransferHistoryEntity receiptTransferHistoryEntity : receiptTransferHistoryEntityList) {
                        receiptTransferHistoryEntity.setDeleted(true);
                    }
                    receiptTransferEntity.setStatus(depositInvoiceRequestDTO.getAction());
                    receiptTransferEntity.setActionDatetime(new Date());
                    receiptTransferEntity.setActionReason("");
                    receiptTransferEntity.setActionRemarks(depositInvoiceRequestDTO.getRemarks());
                    receiptTransferEntity.setActionBy(resp.getData().getUserData().getUserId());
                    receiptTransferEntity.setActionActivityLogsId(collectionActivityLogsEntity.getCollectionActivityLogsId());
                    receiptTransferRepository.save(receiptTransferEntity);
                    break;
                default:
                    throw new Exception("1016032");
            }
            Long successCount, failedCount;

            if (res.getData() == null) {
                successCount = 0L;
                failedCount = 0L;
            } else {
                successCount = res.getData().getSuccessfulRequestCount();
                failedCount = res.getData().getFailedRequestCount();
            }

            depositInvoiceResponseDataDTO.setSuccessfulRequestCount(successCount);
            depositInvoiceResponseDataDTO.setFailedRequestCount(failedCount);
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.deposit_challan, null, depositInvoiceWrapperBody, res, "success", null);
        } catch (Exception e) {
            String errorMessage = e.getMessage();
            String modifiedErrorMessage = utilityService.convertToJSON(errorMessage);
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.deposit_challan, null, null, modifiedErrorMessage, "failure", null);
            throw new Exception("1016042");
        }
        return depositInvoiceResponseDataDTO;
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


    @Override
    public BaseDTOResponse<Object> disableApproveButtonInLms(String Token, Long receiptId) throws Exception {

        try {
            String receiptTransferId = receiptTransferRepository.getDepositionOfReceipt(receiptId);
            String paymentMode = receiptRepository.getPaymentModeByReceiptId(receiptId);

            DisableApproveButtonResponseDTO disableApproveButtonResponseDTO = new DisableApproveButtonResponseDTO();

            String disableApproveButtonConf = collectionConfigurationsRepository.findConfigurationValueByConfigurationName(DISABLE_APPROVE_BUTTON_IN_LMS);
            if (disableApproveButtonConf.equals("true")) {
                if (!paymentMode.equals("upi")) {
                    disableApproveButtonResponseDTO.setDisableApproveButton(receiptTransferId == null);
                } else {
                    disableApproveButtonResponseDTO.setDisableApproveButton(false);
                }
            } else {
                disableApproveButtonResponseDTO.setDisableApproveButton(false);
            }
            return new BaseDTOResponse<>(disableApproveButtonResponseDTO);
        } catch (Exception e) {
            throw new Exception("1016028");
        }
    }

    public BaseDTOResponse<Object> airtelDepositStatusUpdate(String bearerToken, ReceiptTransferAirtelDepositStatusRequestDTO requestBody) throws Exception {
        Long receiptTransferId = requestBody.getReceiptTransferId();
        try {
            ReceiptTransferEntity receiptTransferEntity = receiptTransferRepository.findByReceiptTransferId(receiptTransferId);
            if(receiptTransferEntity == null) {
                throw new Exception("1017002");
            }

// we are updating the receipt transfer id in merchant tran id column in digital payment transaction table
            if (requestBody.getStatus().equals("success")) {
                receiptTransferEntity.setStatus("payment_received");
                receiptTransferEntity.setActionBy(receiptTransferEntity.getTransferredBy());
                receiptTransferEntity.setActionDatetime(new Date());
                receiptTransferEntity.setActionReason("Airtel Deposition");
            } else {
                receiptTransferEntity.setStatus("pending");
            }
// here we have to update the activity log id for approval action that we will think accordingly
            receiptTransferRepository.save(receiptTransferEntity);


            // Here we are storing the logs of digital payment transaction table
            // adding vendor static as csl
            DigitalPaymentTransactionsEntity digitalPaymentTransactionsEntity = new DigitalPaymentTransactionsEntity();
            digitalPaymentTransactionsEntity.setCreatedDate(new Date());
            digitalPaymentTransactionsEntity.setCreatedBy(receiptTransferEntity.getTransferredBy());
            digitalPaymentTransactionsEntity.setModifiedDate(null);
            digitalPaymentTransactionsEntity.setModifiedBy(null);
            digitalPaymentTransactionsEntity.setLoanId(5044565L);
            digitalPaymentTransactionsEntity.setPaymentServiceName("airtel_deposition");
            digitalPaymentTransactionsEntity.setStatus(requestBody.getStatus());
            digitalPaymentTransactionsEntity.setMerchantTranId(String.valueOf(receiptTransferId));
            digitalPaymentTransactionsEntity.setAmount(Float.parseFloat(String.valueOf(receiptTransferEntity.getAmount())));
            digitalPaymentTransactionsEntity.setUtrNumber(requestBody.getUtrNumber());
            digitalPaymentTransactionsEntity.setReceiptRequestBody(requestBody);
            digitalPaymentTransactionsEntity.setPaymentLink(null);
            digitalPaymentTransactionsEntity.setMobileNo(null);
            digitalPaymentTransactionsEntity.setVendor("csl");
            digitalPaymentTransactionsEntity.setReceiptGenerated(false);
            digitalPaymentTransactionsEntity.setCollectionActivityLogsId(null);
            digitalPaymentTransactionsEntity.setActionActivityLogsId(null);
            digitalPaymentTransactionsEntity.setOtherResponseData(null);
            digitalPaymentTransactionsRepository.save(digitalPaymentTransactionsEntity);

            return new BaseDTOResponse<>("data saved successfully");

        } catch (Exception ee) {
            return new BaseDTOResponse<>("failure");
        }

    }
}