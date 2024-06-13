package com.synoriq.synofin.collection.collectionservice.implementation;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.synoriq.synofin.collection.collectionservice.common.EnumSQLConstants;
import com.synoriq.synofin.collection.collectionservice.common.errorcode.ErrorCode;
import com.synoriq.synofin.collection.collectionservice.common.exception.CustomException;
import com.synoriq.synofin.collection.collectionservice.config.oauth.CurrentUserInfo;
import com.synoriq.synofin.collection.collectionservice.entity.*;
import com.synoriq.synofin.collection.collectionservice.repository.*;
import com.synoriq.synofin.collection.collectionservice.rest.commondto.GeoLocationDTO;
import com.synoriq.synofin.collection.collectionservice.rest.request.createReceiptDTOs.ReceiptServiceDtoRequest;
import com.synoriq.synofin.collection.collectionservice.rest.request.receiptTransferDTOs.ReceiptTransferAirtelDepositStatusRequestDTO;
import com.synoriq.synofin.collection.collectionservice.rest.request.receiptTransferDTOs.ReceiptTransferDtoRequest;
import com.synoriq.synofin.collection.collectionservice.rest.request.receiptTransferDTOs.ReceiptTransferForAirtelRequestDTO;
import com.synoriq.synofin.collection.collectionservice.rest.request.receiptTransferDTOs.ReceiptTransferStatusUpdateDtoRequest;
import com.synoriq.synofin.collection.collectionservice.rest.request.depositInvoiceDTOs.DepositInvoiceRequestDTO;
import com.synoriq.synofin.collection.collectionservice.rest.request.depositInvoiceDTOs.DepositInvoiceWrapperRequestDTO;
import com.synoriq.synofin.collection.collectionservice.rest.request.depositInvoiceDTOs.DepositInvoiceWrapperRequestDataDTO;
import com.synoriq.synofin.collection.collectionservice.rest.request.depositInvoiceDTOs.DepositInvoiceWrapperRequestListDTO;
import com.synoriq.synofin.collection.collectionservice.rest.request.receiptTransferDTOs.ReceiptTransferLmsFilterDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.BaseDTOResponse;
import com.synoriq.synofin.collection.collectionservice.rest.response.CreateReceiptLmsDTOs.ServiceRequestSaveResponse;
import com.synoriq.synofin.collection.collectionservice.rest.response.DepositInvoiceResponseDTOs.DepositInvoiceResponseDataDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.DepositInvoiceResponseDTOs.DepositInvoiceWrapperResponseDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.ReceiptTransferDTOs.*;
import com.synoriq.synofin.collection.collectionservice.rest.response.ReceiptTransferLmsFilterResponseDTOs.ReceiptTransferLmsFilterResponseDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.ReceiptTransferResponseDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.UserDetailByTokenDTOs.UserDetailByTokenDTOResponse;
import com.synoriq.synofin.collection.collectionservice.rest.response.UserDetailsByUserIdDTOs.UserDataReturnResponseDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.UserDetailsByUserIdDTOs.UserDetailByUserIdDTOResponse;
import com.synoriq.synofin.collection.collectionservice.rest.response.s3ImageDTOs.UploadImageResponseDTO.UploadImageOnS3ResponseDTO;
import com.synoriq.synofin.collection.collectionservice.service.*;
import com.synoriq.synofin.collection.collectionservice.service.utilityservice.HTTPRequestService;
import com.synoriq.synofin.dataencryptionservice.service.RSAUtils;
import com.synoriq.synofin.lms.commondto.dto.collection.ReceiptTransferDTO;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.concurrent.DelegatingSecurityContextExecutorService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.Tuple;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.synoriq.synofin.collection.collectionservice.common.ActivityEvent.*;
import static com.synoriq.synofin.collection.collectionservice.common.ActivityRemarks.*;
import static com.synoriq.synofin.collection.collectionservice.common.ActivityRemarks.CREATE_RECEIPT;
import static com.synoriq.synofin.collection.collectionservice.common.GlobalVariables.*;

@Service
@Slf4j
@Transactional
public class ReceiptTransferServiceImpl implements ReceiptTransferService {
    @Autowired
    private ReceiptRepository receiptRepository;
    @Autowired
    private EntityManager entityManager;

    @Value("${spring.profiles.active}")
    private String springProfile;

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

    @Autowired
    private IntegrationConnectorService integrationConnectorService;

    @Override
    @Transactional
    public BaseDTOResponse<Object> createReceiptTransfer(@RequestBody ReceiptTransferDtoRequest receiptTransferDtoRequest, String token) throws Exception {

        BaseDTOResponse<Object> baseResponse;
        try {
            Long receiptTransferTableId = receiptTransferDtoRequest.getReceiptTransferId();
            String limitConf;
            String updatedRemarks;

            for (Long receiptId : receiptTransferDtoRequest.getReceipts()) {
                ReceiptTransferHistoryEntity receiptTransferIdCheck = receiptTransferHistoryRepository.findByCollectionReceiptsIdAndDeleted(receiptId, false);
                if (receiptTransferIdCheck != null) {
                    ErrorCode errorCode = ErrorCode.getErrorCode(1016050, "Receipt " + receiptId + " already has been transferred");
                    throw new CustomException(errorCode);
                }
            }

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

                    ReceiptTransferEntity receiptTransferEntity = saveReceiptTransferData(receiptTransferDtoRequest, collectionActivityId, token);
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


                ReceiptTransferEntity receiptTransferEntity = saveReceiptTransferData(receiptTransferDtoRequest, collectionActivityId, token);

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
        } catch (CustomException e) {
            throw new CustomException(e.getMessage(), e.getCode());
        } catch (Exception ee) {
            log.error("RestControllers error occurred for vanWebHookDetails {}", ee.getMessage());
            throw new Exception(ee.getMessage());
        }
        return baseResponse;
    }

    @Override
    @Transactional
    public BaseDTOResponse<Object> createReceiptTransferNew(Object object, MultipartFile transferProof, String token) throws Exception {

        BaseDTOResponse<Object> baseResponse;
        try {
            log.info("Begin createReceiptNew");
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(String.valueOf(object));
            ReceiptTransferDtoRequest receiptTransferDtoRequest = objectMapper.convertValue(jsonNode, ReceiptTransferDtoRequest.class);
            Long receiptTransferTableId = receiptTransferDtoRequest.getReceiptTransferId();

            GeoLocationDTO geoLocationDTO = objectMapper.convertValue(receiptTransferDtoRequest.getActivityData().getGeolocationData(), GeoLocationDTO.class);
            UploadImageOnS3ResponseDTO transferProofUploaded = integrationConnectorService.uploadImageOnS3(token, transferProof, "receipt_transfer", geoLocationDTO, "");
            String filePath = "";
            if (transferProofUploaded.getData() != null) {
                filePath = transferProofUploaded.getData().getUserRefNo() + "/" + transferProofUploaded.getData().getFileName();
            }
            // creating images Object
            Map<String, Object> imageMap = new HashMap<>();
            imageMap.put("transfer1", filePath);
            receiptTransferDtoRequest.setReceiptImage(imageMap);

            String limitConf;
            String updatedRemarks;

            // this check need an update because in transfer type it will not work, right now I am commenting it
//            for (Long receiptId : receiptTransferDtoRequest.getReceipts()) {
//                ReceiptTransferHistoryEntity receiptTransferIdCheck = receiptTransferHistoryRepository.findByCollectionReceiptsId(receiptId);
//                if (receiptTransferIdCheck != null) {
//                    ErrorCode errorCode = ErrorCode.getErrorCode(1016050, "Receipt " + receiptId + " already has been transferred");
//                    throw new CustomException(errorCode);
//                }
//            }
            String airtelDepositTransferMode = String.valueOf(collectionConfigurationsRepository.findConfigurationValueByConfigurationName("airtel_deposit_transfer_mode"));
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

            Long collectionActivityId = activityLogService.createActivityLogs(receiptTransferDtoRequest.getActivityData(), token);

            ReceiptTransferEntity receiptTransferEntity = saveReceiptTransferData(receiptTransferDtoRequest, collectionActivityId, token);
            CollectionActivityLogsEntity collectionActivityLogsEntity1 = collectionActivityLogsRepository.findByCollectionActivityLogsId(collectionActivityId);
            String remarks = receiptTransferDtoRequest.getActivityData().getRemarks();
            String lastWord = remarks.substring(remarks.lastIndexOf(" ") + 1);
            updatedRemarks = CREATE_TRANSFER;
            updatedRemarks = updatedRemarks.replace("{transfer_request}", receiptTransferEntity.getReceiptTransferId().toString());
            updatedRemarks = (updatedRemarks + lastWord);
            collectionActivityLogsEntity1.setRemarks(updatedRemarks);
            collectionActivityLogsRepository.save(collectionActivityLogsEntity1);
            if (Objects.equals(receiptTransferDtoRequest.getTransferType(), "transfer")) {
                if ((utilizedAmount + transferredAmount) < totalLimitValue) {
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

                for (Long receiptTransferId : receiptTransferDtoRequest.getReceipts()) {

                    ReceiptTransferHistoryEntity receiptTransferHistoryEntity = new ReceiptTransferHistoryEntity();

                    receiptTransferHistoryEntity.setReceiptTransferId(receiptTransferEntity.getReceiptTransferId());
                    receiptTransferHistoryEntity.setCollectionReceiptsId(receiptTransferId);
                    receiptTransferHistoryEntity.setDeleted(false);
                    receiptTransferHistoryRepository.save(receiptTransferHistoryEntity);
                }
                baseResponse = new BaseDTOResponse<>(receiptTransferEntity);

                if(airtelDepositTransferMode.equals("true")) {
                    receiptTransferDtoRequest.setReceiptTransferId(receiptTransferEntity.getReceiptTransferId());
                    DigitalPaymentTransactionsEntity digitalPaymentTransactionsEntity = getDigitalPaymentTransactionsEntity(receiptTransferDtoRequest);
                    digitalPaymentTransactionsRepository.save(digitalPaymentTransactionsEntity);
                }
            }

        } catch (CustomException e) {
            throw new CustomException(e.getMessage(), e.getCode());
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

    private void saveReceiptTransferData(ReceiptTransferStatusUpdateDtoRequest receiptTransferStatusUpdateDtoRequest, ReceiptTransferEntity receiptTransferEntity, Long collectionActivityLogsId)
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

    private void setRemarks(ReceiptTransferStatusUpdateDtoRequest receiptTransferStatusUpdateDtoRequest, Long collectionActivityId) {
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

    @NotNull
    private static DigitalPaymentTransactionsEntity getDigitalPaymentTransactionsEntity(ReceiptTransferDtoRequest receiptTransferDtoRequest) {
        DigitalPaymentTransactionsEntity digitalPaymentTransactionsEntity = new DigitalPaymentTransactionsEntity();
        digitalPaymentTransactionsEntity.setCreatedDate(new Date());
        digitalPaymentTransactionsEntity.setCreatedBy(receiptTransferDtoRequest.getActivityData().getUserId());
        digitalPaymentTransactionsEntity.setModifiedDate(null);
        digitalPaymentTransactionsEntity.setModifiedBy(null);
        digitalPaymentTransactionsEntity.setLoanId(5044565L);
        digitalPaymentTransactionsEntity.setPaymentServiceName("airtel_deposition");
        digitalPaymentTransactionsEntity.setStatus(receiptTransferDtoRequest.getStatus());
        digitalPaymentTransactionsEntity.setMerchantTranId(String.valueOf(receiptTransferDtoRequest.getReceiptTransferId()));
        digitalPaymentTransactionsEntity.setAmount(Float.parseFloat(String.valueOf(receiptTransferDtoRequest.getAmount())));
        digitalPaymentTransactionsEntity.setUtrNumber(null);
        digitalPaymentTransactionsEntity.setReceiptRequestBody(receiptTransferDtoRequest);
        digitalPaymentTransactionsEntity.setPaymentLink(null);
        digitalPaymentTransactionsEntity.setMobileNo(null);
        digitalPaymentTransactionsEntity.setVendor("airtel");
        digitalPaymentTransactionsEntity.setReceiptGenerated(false);
        digitalPaymentTransactionsEntity.setCollectionActivityLogsId(null);
        digitalPaymentTransactionsEntity.setActionActivityLogsId(null);
        digitalPaymentTransactionsEntity.setOtherResponseData(receiptTransferDtoRequest);
        return digitalPaymentTransactionsEntity;
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
                if (receiptTransferEntity1 != null) {
                    return new BaseDTOResponse<>(receiptTransferEntity1);
                } else {
                    return new BaseDTOResponse<>(com.synoriq.synofin.collection.collectionservice.common.errorcode.ErrorCode.getErrorCode(1016025));
                }
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
        List<ReceiptTransferCustomDataResponseDTO> bankTransferArr = new ArrayList<>(1);
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
                    boolean exists = bankTransferArr.stream()
                            .anyMatch(dto -> dto.getReceiptTransferId() > Long.parseLong(String.valueOf(receiptTransferData.get("receipt_transfer_id"))));
                    if (!exists) {
                        bankTransferArr.clear();
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
                    }
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
            if (!receiptTransferDataList.isEmpty()) {
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
                    if(res.getResponse().equals(true) && res.getData().getFailedRequestCount() == 0) {
                        receiptTransferEntity.setStatus(depositInvoiceRequestDTO.getAction());
                        receiptTransferEntity.setActionDatetime(new Date());
                        receiptTransferEntity.setActionReason("");
                        receiptTransferEntity.setActionRemarks(depositInvoiceRequestDTO.getRemarks());
                        receiptTransferEntity.setActionBy(resp.getData().getUserData().getUserId());
                        receiptTransferEntity.setActionActivityLogsId(collectionActivityLogsEntity.getCollectionActivityLogsId());
                        receiptTransferRepository.save(receiptTransferEntity);
                    }
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
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.deposit_challan, null, depositInvoiceWrapperBody, res, "success", null, HttpMethod.POST.name(), "depositChallanBulkAction");
        } catch (Exception e) {
            String errorMessage = e.getMessage();
            String modifiedErrorMessage = utilityService.convertToJSON(errorMessage);
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.deposit_challan, null, null, modifiedErrorMessage, "failure", null, HttpMethod.POST.name(), "depositChallanBulkAction");
            throw new Exception("1016042");
        }
        return depositInvoiceResponseDataDTO;
    }

    private ReceiptTransferEntity saveReceiptTransferData(ReceiptTransferDtoRequest receiptTransferDtoRequest, Long collectionActivityId, String token) {
        ReceiptTransferEntity receiptTransferEntity = new ReceiptTransferEntity();
        UserDetailByTokenDTOResponse res = utilityService.getUserDetailsByToken(token);
        log.info("token res {} -> userId {}", res, res.getData().getUserData().getUserId());
        receiptTransferEntity.setCreatedDate(new Date());
        // for lifpl to lifc secret key auth token userId data not present
        receiptTransferEntity.setTransferredBy(res.getData().getUserData().getUserId() == null ? 1L : res.getData().getUserData().getUserId());
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
                if (paymentMode.equals("cash") || paymentMode.equals("cheque")) {
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
            DigitalPaymentTransactionsEntity utrNumberData = digitalPaymentTransactionsRepository.checkUtrNumberValidation(requestBody.getUtrNumber());
            if(!requestBody.getUtrNumber().isEmpty()) {
                if(utrNumberData != null) {
                    throw new Exception("1016044");
                }
            }

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


            Optional<DigitalPaymentTransactionsEntity> digitalPaymentTransactions = Optional.ofNullable(digitalPaymentTransactionsRepository.findByMerchantTranId(receiptTransferId.toString()));
            if(digitalPaymentTransactions.isPresent()) {
                digitalPaymentTransactions.get().setUtrNumber(requestBody.getUtrNumber());
                digitalPaymentTransactions.get().setCallBackRequestBody(requestBody);
                digitalPaymentTransactionsRepository.save(digitalPaymentTransactions.get());
            }
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.deposit_challan, null, requestBody, "Data saved successfully", "success", null, HttpMethod.POST.name(), "receipt-transfer/airtel-deposition/status-update");
            return new BaseDTOResponse<>("data saved successfully");

        } catch (Exception ee) {
//            return new BaseDTOResponse<>("failure");
            throw new Exception(ee.getMessage());
        }

    }


    @Override
    public BaseDTOResponse<Object> getReceiptTransferForAirtel(String token, ReceiptTransferForAirtelRequestDTO receiptTransferForAirtelRequestDTO) throws Exception {
        Map<String, Object> receiptTransferEntity = receiptTransferRepository.getReceiptTransferById(receiptTransferForAirtelRequestDTO.getReceiptTransferId());
        try {
            if (receiptTransferEntity != null) {
                return new BaseDTOResponse<>(receiptTransferEntity);
            } else {
                return new BaseDTOResponse<>(com.synoriq.synofin.collection.collectionservice.common.errorcode.ErrorCode.getErrorCode(1016025));
            }

        } catch (Exception e) {
            log.info("error", e);
            e.printStackTrace();
            throw new Exception("1016028");
        }
    }
    @Override
    public BaseDTOResponse<Object> getReceiptTransferByFilter(ReceiptTransferLmsFilterDTO filterDTO) throws Exception {
        ReceiptTransferLmsFilterResponseDTO receiptTransferLmsFilterResponseDTO = new ReceiptTransferLmsFilterResponseDTO();
        try {
            String encryptionKey = rsaUtils.getEncryptionKey(currentUserInfo.getClientId());
            String password = rsaUtils.getPassword(currentUserInfo.getClientId());
            if(encryptionKey == null || encryptionKey.equals("")) {
                encryptionKey = null;
            }
            if(password == null || password.equals("")) {
                password = null;
            }
//            Boolean piiPermission = rsaUtils.getPiiPermission();
            boolean piiPermission = true;
            log.info("encryption key {}", encryptionKey);
            log.info("password {}", password);
            String whereCondition = "";

            if (filterDTO.getCriteria() != null && filterDTO.getCriteria().size() > 0) {
                String receiptCondition = "sr.request_source IN (";
                for (String receipt : filterDTO.getCriteria()) {
                    receiptCondition = receiptCondition + "'" + receipt + "'" + ",";
                }
                receiptCondition = receiptCondition.substring(0, receiptCondition.length() - 1);
                receiptCondition = receiptCondition + ")    ";
                whereCondition = whereCondition + receiptCondition;
//                whereCondition = whereCondition + " and sr.request_source =  '" + filterDTO.getCriteria() + "'     ";
            }
            if (filterDTO.getFromDate() != null && filterDTO.getToDate() != null) {
                whereCondition = whereCondition + " and date(sr.form->>'date_of_receipt') between to_date('" + filterDTO.getFromDate() + "', 'DD-MM-YYYY') and to_date('" + filterDTO.getToDate() + "', 'DD-MM-YYYY') and ";
            }
            // remove last 4 characters of string
            whereCondition = Optional.ofNullable(whereCondition)
                    .filter(str -> str.length() != 0)
                    .map(str -> str.substring(0, str.length() - 4))
                    .orElse(whereCondition);

            String queryString = "select \n" +
                    "    sr.service_request_id,\n" +
                    "    concat(lms.decrypt_data(c.first_name, " + encryptionKey + "," + password + "," + piiPermission + "), ' ', lms.decrypt_data(c.last_name, " + encryptionKey + "," + password + "," + piiPermission + ")) as customer_name,\n" +
                    "    (case when cast(sr.form->>'receipt_amount' as decimal) is null then 0 else cast(sr.form->>'receipt_amount' as decimal) end) as receipt_amount,\n" +
                    "    sr.form->>'payment_mode' as payment_mode,\n" +
                    "    case when sr.request_source = 'm_collect' then 'Syno Collect'\n" +
                    "    when sr.request_source = 'manual_entry' then 'Manual Entry'\n" +
                    "    when sr.request_source = 'bulk_upload' then 'Bulk Upload'\n" +
                    "    when sr.request_source = 'bank_recon' then 'Bank Recon'\n" +
                    "    else sr.request_source end as receipt_source,\n" +
                    "    (select concat(u.\"name\", ' - (', u.username, ')') from master.users u where u.user_id = sr.created_by) as created_by,\n" +
                    "    COUNT(sr.service_request_id) OVER () AS total_rows\n" +
                    "from lms.service_request sr \n" +
                    "join (select loan_application_number, loan_application_id from lms.loan_application) as la on la.loan_application_id = sr.loan_id\n" +
                    "join (select loan_id, customer_id, customer_type from lms.customer_loan_mapping) as clm on clm.loan_id = sr.loan_id and clm.customer_type = 'applicant' \n" +
                    "join (select customer_id, first_name, last_name  from lms.customer) as c on clm.customer_id = c.customer_id\n" +
                    "where sr.status = 'initiated' and sr.form->>'payment_mode' ="+ "'" + filterDTO.getPaymentMode() + "'\n" +
                    " and sr.service_request_id not in (select rth.collection_receipts_id from collection.receipt_transfer_history rth join collection.receipt_transfer rt on rth.receipt_transfer_id = rt.receipt_transfer_id where rt.status = 'pending') and " + whereCondition;

            int pageNumber = filterDTO.getPage();
            int pageSize = filterDTO.getSize();
            queryString += " LIMIT " + pageSize + " OFFSET " + (pageNumber * pageSize);
            log.info("here is the query for you {}", queryString);

            Query queryData = null;
            queryData = this.entityManager.createNativeQuery(queryString, Tuple.class);
            List<Tuple> queryRows = queryData.getResultList();
            List<Map<String, Object>> data = utilityService.formatDigitalSiteVisitData(queryRows);

            if (data.size() > 0) {
                int totalCount = Integer.parseInt(String.valueOf(data.get(0).get("total_rows")));
                receiptTransferLmsFilterResponseDTO.setData(data);
                receiptTransferLmsFilterResponseDTO.setTotalCount(totalCount);
                return new BaseDTOResponse<>(receiptTransferLmsFilterResponseDTO);
            } else {
                int totalCount = 0;
                receiptTransferLmsFilterResponseDTO.setData(new ArrayList<>());
                receiptTransferLmsFilterResponseDTO.setTotalCount(totalCount);
                return new BaseDTOResponse<>(receiptTransferLmsFilterResponseDTO);
            }
        } catch (Exception e) {
            throw new Exception("1017000");
        }
    }

}