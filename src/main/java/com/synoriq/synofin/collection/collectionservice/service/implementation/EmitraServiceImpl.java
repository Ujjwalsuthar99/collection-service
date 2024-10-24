package com.synoriq.synofin.collection.collectionservice.service.implementation;

import com.synoriq.synofin.collection.collectionservice.common.EnumSQLConstants;
import com.synoriq.synofin.collection.collectionservice.common.errorcode.ErrorCode;
import com.synoriq.synofin.collection.collectionservice.common.exception.ConnectorException;
import com.synoriq.synofin.collection.collectionservice.common.exception.CustomException;
import com.synoriq.synofin.collection.collectionservice.entity.DigitalPaymentTransactionsEntity;
import com.synoriq.synofin.collection.collectionservice.rest.commondto.CommonIntegrationResponseDTO;
import com.synoriq.synofin.collection.collectionservice.rest.request.emitrarequestdtos.*;
import com.synoriq.synofin.collection.collectionservice.rest.commondto.IntegrationServiceErrorResponseDTO;
import com.synoriq.synofin.collection.collectionservice.rest.request.CollectionActivityLogDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.BaseDTOResponse;
import com.synoriq.synofin.collection.collectionservice.service.*;
import com.synoriq.synofin.collection.collectionservice.service.utilityservice.HTTPRequestService;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.Map;

import static com.synoriq.synofin.collection.collectionservice.common.ActivityRemarks.EMITRA_RECEIPT_STATUS;
import static com.synoriq.synofin.collection.collectionservice.common.GlobalVariables.COLLECTION;
import static com.synoriq.synofin.collection.collectionservice.common.PaymentRelatedVariables.*;

@Service
@Slf4j
public class EmitraServiceImpl implements EmitraService {

    public EmitraServiceImpl(ConsumedApiLogService consumedApiLogService, UtilityService utilityService, RestTemplate restTemplate, DigitalPaymentTransactionsService digitalPaymentTransactionsService, ActivityLogService activityLogService) {
        this.consumedApiLogService = consumedApiLogService;
        this.utilityService = utilityService;
        this.restTemplate = restTemplate;
        this.digitalPaymentTransactionsService = digitalPaymentTransactionsService;
        this.activityLogService = activityLogService;
    }
    @Value("${spring.profiles.active}")
    private String springProfile;


    public static final String SUCCESS = "success";
    public static final String FAILURE = "failure";

    private final ConsumedApiLogService consumedApiLogService;
    private final UtilityService utilityService;
    private final RestTemplate restTemplate;
    private final DigitalPaymentTransactionsService digitalPaymentTransactionsService;
    private final ActivityLogService activityLogService;


    @Override
    public BaseDTOResponse<Object> verifySsoToken(String token, VerifySsoTokenDTO requestBody) throws CustomException {
        CommonIntegrationResponseDTO response = new CommonIntegrationResponseDTO();
        try {
            response = HTTPRequestService.<Object, CommonIntegrationResponseDTO>builder()
                    .httpMethod(HttpMethod.POST)
                    .url("http://localhost:1102/v1/verify-sso-token")
                    .httpHeaders(UtilityService.createHeaders(token))
                    .body(getEmitraRequestBody(requestBody))
                    .typeResponseType(CommonIntegrationResponseDTO.class)
                    .build().call(restTemplate);

            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.emitra_verify_sso_token, null, requestBody, response, SUCCESS, null, HttpMethod.GET.name(), "emitra/verify_sso_token");
        } catch (Exception e) {
            String errorMessage = e.getMessage();
            String modifiedErrorMessage = utilityService.convertToJSON(errorMessage);
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.emitra_verify_sso_token, null, requestBody, modifiedErrorMessage, FAILURE, null, HttpMethod.GET.name(), "emitra/verify_sso_token");
            log.error("{}", e.getMessage());
        }
        return new BaseDTOResponse<>(response.getData());
    }

    @Override
    public Object serviceTransaction(String token, Long loanId, Long userId, ServiceTransactionDTO requestBody) throws CustomException {

   
        CommonIntegrationResponseDTO response = new CommonIntegrationResponseDTO();
        String apiEndpoint = "emitra/service-transaction";
        try {

            VerifySsoTokenDTO verifySsoTokenDTO = new VerifySsoTokenDTO();
            verifySsoTokenDTO.setToken(requestBody.getSsoToken());
            Object verifySsoTokenData = verifySsoToken(token, verifySsoTokenDTO);
            Map<String, Object> verifySsoTokenObjMap = new ObjectMapper().convertValue(verifySsoTokenData, Map.class);

            if(verifySsoTokenObjMap.get("response").equals(false) || verifySsoTokenObjMap.get("data") == null) {
                throw new ConnectorException((IntegrationServiceErrorResponseDTO) verifySsoTokenObjMap.get("error"), HttpStatus.FAILED_DEPENDENCY, response.getRequestId());
            }


            response = HTTPRequestService.<Object, CommonIntegrationResponseDTO>builder()
                    .httpMethod(HttpMethod.POST)
                    .url("http://localhost:1102/v1/service-transaction")
                    .httpHeaders(UtilityService.createHeaders(token))
                    .body(getEmitraRequestBody(requestBody))
                    .typeResponseType(CommonIntegrationResponseDTO.class)
                    .build().call(restTemplate);
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.emitra_service_transaction, userId, requestBody, response, SUCCESS, loanId, HttpMethod.POST.name(), apiEndpoint);

            if(response.getResponse().equals(false) || response.getData() == null) {
                throw new ConnectorException(response.getError(), HttpStatus.FAILED_DEPENDENCY, response.getRequestId());
            }

            Map<String, Object> serviceTransactionResponseData = new ObjectMapper().convertValue(response.getData(), Map.class);

            CollectionActivityLogDTO collectionActivityLogDTO = new CollectionActivityLogDTO();
            String updatedRemarks = EMITRA_RECEIPT_STATUS;
            String transactionId = String.valueOf(serviceTransactionResponseData.get("transaction_id"));
            String transactionStatus = String.valueOf(serviceTransactionResponseData.get("transaction_status"));
            updatedRemarks = updatedRemarks.replace("{status}", transactionStatus);
            updatedRemarks = updatedRemarks.replace("{transaction_id}", transactionId);
            collectionActivityLogDTO.setRemarks(updatedRemarks);
            collectionActivityLogDTO.setUserId(userId);
            collectionActivityLogDTO.setDeleted(false);
            collectionActivityLogDTO.setActivityName("transaction_" + transactionStatus);
            collectionActivityLogDTO.setDistanceFromUserBranch(0.0);
            collectionActivityLogDTO.setAddress("{}");
            collectionActivityLogDTO.setImages(null);
            collectionActivityLogDTO.setLoanId(loanId);
            collectionActivityLogDTO.setGeolocationData("{}");
            collectionActivityLogDTO.setBatteryPercentage(0L);
            Long activityLogId = activityLogService.createActivityLogs(collectionActivityLogDTO, token);


            DigitalPaymentTransactionsEntity digitalPaymentTransactionsEntity = new DigitalPaymentTransactionsEntity(
                    new Date(), userId,
                    null, null, loanId,
                    EMITRA, PENDING, String.valueOf(serviceTransactionResponseData.get("request_id")),
                    Float.parseFloat(String.valueOf(serviceTransactionResponseData.get("trans_amt"))),
                    transactionId, requestBody, null,
                    null, EMITRA, false, response, activityLogId, null, null, null);
            digitalPaymentTransactionsService.createDigitalPaymentTransaction(digitalPaymentTransactionsEntity);


            UpdateTransactionPostingDTO updateTransactionRequestBody = new UpdateTransactionPostingDTO();
            updateTransactionRequestBody.setApplicationId(String.valueOf(digitalPaymentTransactionsEntity.getDigitalPaymentTransactionsId()));
            updateTransactionRequestBody.setTransactionId(String.valueOf(serviceTransactionResponseData.get("transaction_id")));
            updateTransactionRequestBody.setSsoToken(requestBody.getSsoToken());

            // we are not throwing exception for emitra update transaction posting API because user is not having any screens to get this notified.
            updateTransactionPosting(token, loanId, updateTransactionRequestBody);

        } catch (ConnectorException ee) {
            String errorMessage = ee.getMessage();
            String modifiedErrorMessage = utilityService.convertToJSON(errorMessage);
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.emitra_service_transaction, userId, requestBody, modifiedErrorMessage, FAILURE, loanId, HttpMethod.POST.name(), apiEndpoint);
            throw new ConnectorException(ErrorCode.EMITRA_CONNECTOR_EXCEPTION, ee.getText(), HttpStatus.FAILED_DEPENDENCY, ee.getRequestId());
        } catch (Exception e) {
            String errorMessage = e.getMessage();
            String modifiedErrorMessage = utilityService.convertToJSON(errorMessage);
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.emitra_service_transaction, userId, requestBody, modifiedErrorMessage, FAILURE, loanId, HttpMethod.GET.name(), apiEndpoint);
            log.error("{}", e.getMessage());
            throw new CustomException(e.getMessage());
        }
        return new BaseDTOResponse<>(response.getData());
    }

    @Override
    public Object updateTransactionPosting(String token, Long loanId, UpdateTransactionPostingDTO requestBody) throws CustomException {
        CommonIntegrationResponseDTO response = new CommonIntegrationResponseDTO();
        try {
            response = HTTPRequestService.<Object, CommonIntegrationResponseDTO>builder()
                    .httpMethod(HttpMethod.POST)
                    .url("http://localhost:1102/v1/update-transaction-posting")
                    .httpHeaders(UtilityService.createHeaders(token))
                    .body(getEmitraRequestBody(requestBody))
                    .typeResponseType(CommonIntegrationResponseDTO.class)
                    .build().call(restTemplate);

            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.emitra_update_transaction_posting, null, requestBody, response, SUCCESS, loanId, HttpMethod.GET.name(), "emitra/update_transaction_posting");
        } catch (Exception e) {
            String errorMessage = e.getMessage();
            String modifiedErrorMessage = utilityService.convertToJSON(errorMessage);
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.emitra_update_transaction_posting, null, requestBody, modifiedErrorMessage, FAILURE, loanId, HttpMethod.GET.name(), "emitra/update_transaction_posting");
            log.error("{}", e.getMessage());
            throw new CustomException(e.getMessage());
        }
        return new BaseDTOResponse<>(response.getData());
    }

    @Override
    public Object verifyTransaction(String token, Long loanId, VerifyTransactionDTO requestBody) throws CustomException {
        
        CommonIntegrationResponseDTO response = new CommonIntegrationResponseDTO();
        try {
            response = HTTPRequestService.<Object, CommonIntegrationResponseDTO>builder()
                    .httpMethod(HttpMethod.POST)
                    .url("http://localhost:1102/v1/verify-transaction")
                    .httpHeaders(UtilityService.createHeaders(token))
                    .body(getEmitraRequestBody(requestBody))
                    .typeResponseType(CommonIntegrationResponseDTO.class)
                    .build().call(restTemplate);

            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.emitra_verify_transaction, null, requestBody, response, SUCCESS, loanId, HttpMethod.GET.name(), "emitra/verify_transaction");
        } catch (Exception e) {
            String errorMessage = e.getMessage();
            String modifiedErrorMessage = utilityService.convertToJSON(errorMessage);
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.emitra_verify_transaction, null, requestBody, modifiedErrorMessage, FAILURE, loanId, HttpMethod.GET.name(), "emitra/verify_transaction");
            log.error("{}", e.getMessage());
            throw new CustomException(e.getMessage());
        }
        return new BaseDTOResponse<>(response.getData());
    }

    @Override
    public Object getKioskDetails(String token, Long loanId, GetKioskDetailsDTO requestBody) throws CustomException {
        CommonIntegrationResponseDTO response = new CommonIntegrationResponseDTO();
        try {
            response = HTTPRequestService.<Object, CommonIntegrationResponseDTO>builder()
                    .httpMethod(HttpMethod.POST)
                    .url("http://localhost:1102/v1/get-kiosk-details")
                    .httpHeaders(UtilityService.createHeaders(token))
                    .body(getEmitraRequestBody(requestBody))
                    .typeResponseType(CommonIntegrationResponseDTO.class)
                    .build().call(restTemplate);

            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.emitra_get_kiosk_details, null, requestBody, response, SUCCESS, loanId, HttpMethod.GET.name(), "emitra/get_kiosk_details");
        } catch (Exception e) {
            String errorMessage = e.getMessage();
            String modifiedErrorMessage = utilityService.convertToJSON(errorMessage);
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.emitra_get_kiosk_details, null, requestBody, modifiedErrorMessage, FAILURE, loanId, HttpMethod.GET.name(), "emitra/get_kiosk_details");
            log.error("{}", e.getMessage());
            throw new CustomException(e.getMessage());
        }
        return new BaseDTOResponse<>(response.getData());
    }


    @Override
    public Object decryption(String token, DecryptionDTO requestBody) throws CustomException {
        
        CommonIntegrationResponseDTO response = new CommonIntegrationResponseDTO();
        try {
            response = HTTPRequestService.<Object, CommonIntegrationResponseDTO>builder()
                    .httpMethod(HttpMethod.POST)
                    .url("http://localhost:1102/v1/decryption")
                    .httpHeaders(UtilityService.createHeaders(token))
                    .body(getEmitraRequestBody(requestBody))
                    .typeResponseType(CommonIntegrationResponseDTO.class)
                    .build().call(restTemplate);

            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.emitra_decryption, null, requestBody, response, SUCCESS, null, HttpMethod.GET.name(), "emitra/decryption");
        } catch (Exception e) {
            String errorMessage = e.getMessage();
            String modifiedErrorMessage = utilityService.convertToJSON(errorMessage);
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.emitra_decryption, null, requestBody, modifiedErrorMessage, FAILURE, null, HttpMethod.GET.name(), "emitra/decryption");
            log.error("{}", e.getMessage());
            throw new CustomException(e.getMessage());
        }
        return new BaseDTOResponse<>(response.getData());
    }

    private CommonIntegrationRequestDTO getEmitraRequestBody(Object data) {
        return new CommonIntegrationRequestDTO(data, "", COLLECTION, "e-mitra");
    }


}
