package com.synoriq.synofin.collection.collectionservice.service.implementation;

import com.synoriq.synofin.collection.collectionservice.common.EnumSQLConstants;
import com.synoriq.synofin.collection.collectionservice.rest.request.emitraRequestDTOs.*;
import com.synoriq.synofin.collection.collectionservice.rest.response.BaseDTOResponse;
import com.synoriq.synofin.collection.collectionservice.service.ConsumedApiLogService;
import com.synoriq.synofin.collection.collectionservice.service.EmitraService;
import com.synoriq.synofin.collection.collectionservice.service.UtilityService;
import com.synoriq.synofin.collection.collectionservice.service.utilityservice.HTTPRequestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import static com.synoriq.synofin.collection.collectionservice.common.GlobalVariables.COLLECTION;

@Service
@Slf4j
public class EmitraServiceImpl implements EmitraService {

    public EmitraServiceImpl(ConsumedApiLogService consumedApiLogService, UtilityService utilityService) {
        this.consumedApiLogService = consumedApiLogService;
        this.utilityService = utilityService;
    }
    @Value("${spring.profiles.active}")
    private String springProfile;

    private final ConsumedApiLogService consumedApiLogService;
    private final UtilityService utilityService;

    @Override
    public BaseDTOResponse<Object> verifySsoToken(String token, Long loanId, VerifySsoTokenDTO requestBody) throws Exception {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", token);
        httpHeaders.add("Content-Type", "application/json");
        Object response = new Object();
        try {
            response = HTTPRequestService.<Object, Object>builder()
                    .httpMethod(HttpMethod.POST)
                    .url("http://localhost:1102/v1/verify-sso-token")
                    .httpHeaders(httpHeaders)
                    .body(getEmitraRequestBody(requestBody))
                    .typeResponseType(Object.class)
                    .build().call();

            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.emitra_verify_sso_token, null, null, response, "success", loanId, HttpMethod.GET.name(), "emitra/verify_sso_token");
        } catch (Exception e) {
            String errorMessage = e.getMessage();
            String modifiedErrorMessage = utilityService.convertToJSON(errorMessage);
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.emitra_verify_sso_token, null, null, modifiedErrorMessage, "failure", loanId, HttpMethod.GET.name(), "emitra/verify_sso_token");
            log.error("{}", e.getMessage());
        }
        return new BaseDTOResponse<>(response);
    }

    @Override
    public Object serviceTransaction(String token, Long loanId, ServiceTransactionDTO requestBody) throws Exception {

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", token);
        httpHeaders.add("Content-Type", "application/json");
        Object response = new Object();
        try {
            response = HTTPRequestService.<Object, Object>builder()
                    .httpMethod(HttpMethod.POST)
                    .url("http://localhost:1102/v1/service-transaction")
                    .httpHeaders(httpHeaders)
                    .body(getEmitraRequestBody(requestBody))
                    .typeResponseType(Object.class)
                    .build().call();

            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.emitra_service_transaction, null, null, response, "success", loanId, HttpMethod.POST.name(), "emitra/service-transaction");
        } catch (Exception e) {
            String errorMessage = e.getMessage();
            String modifiedErrorMessage = utilityService.convertToJSON(errorMessage);
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.emitra_service_transaction, null, null, modifiedErrorMessage, "failure", loanId, HttpMethod.GET.name(), "emitra/service-transaction");
            log.error("{}", e.getMessage());
        }
        return new BaseDTOResponse<>(response);
    }

    @Override
    public Object updateTransactionPosting(String token, Long loanId, UpdateTransactionPostingDTO requestBody) throws Exception {

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", token);
        httpHeaders.add("Content-Type", "application/json");
        Object response = new Object();
        try {
            response = HTTPRequestService.<Object, Object>builder()
                    .httpMethod(HttpMethod.POST)
                    .url("http://localhost:1102/v1/update-transaction-posting")
                    .httpHeaders(httpHeaders)
                    .body(getEmitraRequestBody(requestBody))
                    .typeResponseType(Object.class)
                    .build().call();

            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.emitra_update_transaction_posting, null, null, response, "success", loanId, HttpMethod.GET.name(), "emitra/update_transaction_posting");
        } catch (Exception e) {
            String errorMessage = e.getMessage();
            String modifiedErrorMessage = utilityService.convertToJSON(errorMessage);
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.emitra_update_transaction_posting, null, null, modifiedErrorMessage, "failure", loanId, HttpMethod.GET.name(), "emitra/update_transaction_posting");
            log.error("{}", e.getMessage());
        }
        return new BaseDTOResponse<>(response);
    }

    @Override
    public Object verifyTransaction(String token, Long loanId, VerifyTransactionDTO requestBody) throws Exception {

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", token);
        httpHeaders.add("Content-Type", "application/json");
        Object response = new Object();
        try {
            response = HTTPRequestService.<Object, Object>builder()
                    .httpMethod(HttpMethod.POST)
                    .url("http://localhost:1102/v1/verify-transaction")
                    .httpHeaders(httpHeaders)
                    .body(getEmitraRequestBody(requestBody))
                    .typeResponseType(Object.class)
                    .build().call();

            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.emitra_verify_transaction, null, null, response, "success", loanId, HttpMethod.GET.name(), "emitra/verify_transaction");
        } catch (Exception e) {
            String errorMessage = e.getMessage();
            String modifiedErrorMessage = utilityService.convertToJSON(errorMessage);
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.emitra_verify_transaction, null, null, modifiedErrorMessage, "failure", loanId, HttpMethod.GET.name(), "emitra/verify_transaction");
            log.error("{}", e.getMessage());
        }
        return new BaseDTOResponse<>(response);
    }

    @Override
    public Object getKioskDetails(String token, Long loanId, GetKioskDetailsDTO requestBody) throws Exception {

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", token);
        httpHeaders.add("Content-Type", "application/json");
        Object response = new Object();
        try {
            response = HTTPRequestService.<Object, Object>builder()
                    .httpMethod(HttpMethod.POST)
                    .url("http://localhost:1102/v1/get-kiosk-details")
                    .httpHeaders(httpHeaders)
                    .body(getEmitraRequestBody(requestBody))
                    .typeResponseType(Object.class)
                    .build().call();

            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.emitra_get_kiosk_details, null, null, response, "success", loanId, HttpMethod.GET.name(), "emitra/get_kiosk_details");
        } catch (Exception e) {
            String errorMessage = e.getMessage();
            String modifiedErrorMessage = utilityService.convertToJSON(errorMessage);
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.emitra_get_kiosk_details, null, null, modifiedErrorMessage, "failure", loanId, HttpMethod.GET.name(), "emitra/get_kiosk_details");
            log.error("{}", e.getMessage());
        }
        return new BaseDTOResponse<>(response);
    }

    private CommonIntegrationRequestDTO getEmitraRequestBody(Object data) {
        return new CommonIntegrationRequestDTO(data, "", COLLECTION, "e-mitra");
    }

}
