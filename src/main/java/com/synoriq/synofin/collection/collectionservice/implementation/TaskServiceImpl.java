package com.synoriq.synofin.collection.collectionservice.implementation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.synoriq.synofin.collection.collectionservice.common.EnumSQLConstants;
import com.synoriq.synofin.collection.collectionservice.config.oauth.CurrentUserInfo;
import com.synoriq.synofin.collection.collectionservice.entity.AdditionalContactDetailsEntity;
import com.synoriq.synofin.collection.collectionservice.repository.AdditionalContactDetailsRepository;
import com.synoriq.synofin.collection.collectionservice.repository.TaskRepository;
import com.synoriq.synofin.collection.collectionservice.rest.request.taskDetailsDTO.TaskDetailRequestDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.BaseDTOResponse;
import com.synoriq.synofin.collection.collectionservice.rest.response.TaskDetailResponseDTOs.*;
import com.synoriq.synofin.collection.collectionservice.rest.response.TaskDetailResponseDTOs.CollateralDetailsResponseDTO.CollateralDetailsResponseDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.TaskDetailResponseDTOs.CollateralDetailsResponseDTO.CollateralDetailsReturnResponseDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.TaskDetailResponseDTOs.LoanSummaryForLoanDTOs.LoanSummaryResponseDTO;
import com.synoriq.synofin.collection.collectionservice.service.ConsumedApiLogService;
import com.synoriq.synofin.collection.collectionservice.service.UtilityService;
import com.synoriq.synofin.collection.collectionservice.service.utilityservice.HTTPRequestService;
import com.synoriq.synofin.collection.collectionservice.service.TaskService;
import com.synoriq.synofin.dataencryptionservice.service.RSAUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class TaskServiceImpl implements TaskService {

    @Autowired
    private RSAUtils rsaUtils;
    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UtilityService utilityService;

    @Autowired
    private ConsumedApiLogService consumedApiLogService;

    @Autowired
    private AdditionalContactDetailsRepository additionalContactDetailsRepository;
    @Autowired
    private CurrentUserInfo currentUserInfo;

    @Override
    public BaseDTOResponse<Object> getTaskDetails(Long userId, Integer pageNo, Integer pageSize) throws Exception {


        BaseDTOResponse<Object> baseDTOResponse;
        try {
            Pageable pageRequest;
            if (pageNo > 0) {
                pageNo = pageNo - 1;
            }
            pageRequest = PageRequest.of(pageNo, pageSize);
            String encryptionKey = rsaUtils.getEncryptionKey(currentUserInfo.getClientId());
            String password = rsaUtils.getPassword(currentUserInfo.getClientId());
            Boolean piiPermission = rsaUtils.getPiiPermission();
            List<Map<String, Object>> taskDetailPages = taskRepository.getTaskDetailsByPages(userId, encryptionKey, password, piiPermission, pageRequest);

            if (pageNo > 0) {
                if (taskDetailPages.size() == 0) {
                    return new BaseDTOResponse<>(taskDetailPages);
                }
            }

            baseDTOResponse = new BaseDTOResponse<>(taskDetailPages);
        } catch (Exception e) {
            throw new Exception("1017002");
        }

        return baseDTOResponse;

    }

    @Override
    public Object getTaskDetailByLoanId(String token, TaskDetailRequestDTO taskDetailRequestDTO) throws Exception {

        TaskDetailDTOResponse loanRes;
        CustomerDetailDTOResponse customerRes;
        LoanBasicDetailsDTOResponse loanDetailRes;

        TaskDetailRequestDTO loanDataBody = new ObjectMapper().convertValue(taskDetailRequestDTO, TaskDetailRequestDTO.class);
        TaskDetailDTOResponse resp = new TaskDetailDTOResponse();
        TaskDetailReturnResponseDTO response = new TaskDetailReturnResponseDTO();
        BaseDTOResponse<Object> baseDTOResponse = null;
        String loanId = taskDetailRequestDTO.getRequestData().getLoanId();
        Long loanIdNumber = Long.parseLong(loanId);
        try {


//            log.info("request dto details {}", taskDetailRequestDTO);
            HttpHeaders httpHeaders = new HttpHeaders();
            log.info("token {}", token);
            httpHeaders.add("Authorization", token);
            httpHeaders.add("Content-Type", "application/json");

            loanRes = HTTPRequestService.<Object, TaskDetailDTOResponse>builder()
                    .httpMethod(HttpMethod.POST)
                    .url("http://localhost:1102/v1/getDataForLoanActions")
                    .httpHeaders(httpHeaders)
                    .body(loanDataBody)
                    .typeResponseType(TaskDetailDTOResponse.class)
                    .build().call();

//            log.info("loan details {}", loanRes);
            // creating api logs
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.get_data_for_loan_action, null, loanDataBody, loanRes, "success", Long.parseLong(loanId));

            loanDetailRes = HTTPRequestService.<Object, LoanBasicDetailsDTOResponse>builder()
                    .httpMethod(HttpMethod.GET)
                    .url("http://localhost:1102/v1/getBasicLoanDetails?loanId=" + loanIdNumber)
                    .httpHeaders(httpHeaders)
                    .typeResponseType(LoanBasicDetailsDTOResponse.class)
                    .build().call();

//            log.info("getBasicLoanDetails {}", loanDetailRes);
            // creating api logs
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.get_basic_loan_detail, null, null, loanDetailRes, "success", Long.parseLong(loanId));

            customerRes = HTTPRequestService.<Object, CustomerDetailDTOResponse>builder()
                    .httpMethod(HttpMethod.GET)
                    .url("http://localhost:1102/v1/getCustomerDetails?loanId=" + loanIdNumber)
                    .httpHeaders(httpHeaders)
                    .typeResponseType(CustomerDetailDTOResponse.class)
                    .build().call();

//            log.info("customer details {}", customerRes.getData());
            // creating api logs
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.get_customer_details, null, null, customerRes, "success", Long.parseLong(loanId));

            LoanSummaryResponseDTO loanSummaryResponse = HTTPRequestService.<Object, LoanSummaryResponseDTO>builder()
                    .httpMethod(HttpMethod.GET)
                    .url("http://localhost:1102/v1/getLoanSummaryForLoan/" + loanIdNumber)
                    .httpHeaders(httpHeaders)
                    .typeResponseType(LoanSummaryResponseDTO.class)
                    .build().call();

            // creating api logs
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.get_loan_summary, null, null, customerRes, "success", Long.parseLong(loanId));

            if (Objects.equals(loanDetailRes.getData() != null ? loanDetailRes.getData().getProductType() : "", "vehicle")) {
                CollateralDetailsResponseDTO collateralResponse = HTTPRequestService.<Object, CollateralDetailsResponseDTO>builder()
                        .httpMethod(HttpMethod.GET)
                        .url("http://localhost:1102/v1/getCollaterals?loanId=" + loanIdNumber)
                        .httpHeaders(httpHeaders)
                        .typeResponseType(CollateralDetailsResponseDTO.class)
                        .build().call();

                // creating api logs
                consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.get_collaterals, null, null, collateralResponse, "success", Long.parseLong(loanId));

                if (collateralResponse.getData() != null) {
                    CollateralDetailsReturnResponseDTO collateralDetailsReturnResponseDTO = new CollateralDetailsReturnResponseDTO();
                    collateralResponse.getData().forEach((key, value) -> {
                        collateralDetailsReturnResponseDTO.setChasisNumber(String.valueOf(value.get("chasis_no")));
                        collateralDetailsReturnResponseDTO.setVehicleNumber(String.valueOf(value.get("vehicle_registration_no")));
                        collateralDetailsReturnResponseDTO.setVehicleType(String.valueOf(value.get("vehicle_type")));
                        collateralDetailsReturnResponseDTO.setModel(String.valueOf(value.get("model")));
                        collateralDetailsReturnResponseDTO.setManufacturer(String.valueOf(value.get("manufacturer")));
                        collateralDetailsReturnResponseDTO.setEngineNumber(String.valueOf(value.get("engine_no")));
                        collateralDetailsReturnResponseDTO.setCostOfAsset(Double.parseDouble(String.valueOf(value.get("cost_of_asset"))));

                    });
                    response.setCollateralDetails(collateralDetailsReturnResponseDTO);
                } else {
                    response.setCollateralDetails(null);
                }

            }
            int dpd = loanDetailRes.getData() != null ? loanDetailRes.getData().getDpd() : 0;
            String dpdTextColor;
            String dpdBgColor;
            String dpdBucket;

            if (dpd >= 0 && dpd <= 30) {
                dpdTextColor = "#323232";
                dpdBgColor = "#61B2FF";
                dpdBucket = "0-30 DPD";
            } else if (dpd >= 31 && dpd <= 60) {
                dpdTextColor = "#ffffff";
                dpdBgColor = "#2F80ED";
                dpdBucket = "31-60 DPD";
            } else if (dpd >= 61 && dpd <= 90) {
                dpdTextColor = "#323232";
                dpdBgColor = "#FDAAAA";
                dpdBucket = "61-90 DPD";
            } else if (dpd >= 91 && dpd <= 120) {
                dpdTextColor = "#323232";
                dpdBgColor = "#F2994A";
                dpdBucket = "91-120 DPD";
            } else if (dpd >= 121 && dpd <= 150) {
                dpdTextColor = "#ffffff";
                dpdBgColor = "#FF5359";
                dpdBucket = "121-150 DPD";
            } else if (dpd >= 151 && dpd <= 180) {
                dpdTextColor = "#ffffff";
                dpdBgColor = "#C83939";
                dpdBucket = "151-180 DPD";
            } else {
                dpdTextColor = "#ffffff";
                dpdBgColor = "#722F37";
                dpdBucket = "180+ DPD";
            }

            String loanApplicationNumber = taskRepository.getLoanApplicationNumber(loanIdNumber);

            List<CustomerDetailsReturnResponseDTO> customerList = new ArrayList<>();


            if (!(customerRes.getData() == null)) {
                for (CustomerDataResponseDTO customerData : customerRes.getData()) {
                    if (customerData.getBasicInfo() != null) {
                        CustomerDetailsReturnResponseDTO customerDetails = new CustomerDetailsReturnResponseDTO();
                        BasicInfoReturnResponseDTO basicInfoApplicant = new BasicInfoReturnResponseDTO();
                        Map<String, String> address = new HashMap<>();
                        NumbersReturnResponseDTO numbersReturnResponseDTO = new NumbersReturnResponseDTO();
                        customerDetails.setId(customerData.getId());
                        customerDetails.setCustomerType(customerData.getCustomerType());
                        basicInfoApplicant.setId(customerData.getBasicInfo().getId());
                        basicInfoApplicant.setFirstName(customerData.getBasicInfo().getFirstName());
                        basicInfoApplicant.setMiddleName(customerData.getBasicInfo().getMiddleName());
                        basicInfoApplicant.setLastName(customerData.getBasicInfo().getLastName());
                        basicInfoApplicant.setDob(customerData.getBasicInfo().getDob());
                        basicInfoApplicant.setDpd(dpd);
                        basicInfoApplicant.setDpdBucket(dpdBucket);
                        basicInfoApplicant.setDpdBgColor(dpdBgColor);
                        basicInfoApplicant.setDpdTextColor(dpdTextColor);
                        basicInfoApplicant.setPos(loanDetailRes.getData().getPrincipalOutstanding());
                        basicInfoApplicant.setLoanAmount(loanDetailRes.getData().getLoanAmount());
                        basicInfoApplicant.setEmiAmount(loanDetailRes.getData().getEmiAmount());
                        basicInfoApplicant.setLoanTenure(loanDetailRes.getData().getLoanTenure());
                        basicInfoApplicant.setAssetClassification(loanDetailRes.getData().getAssetClassification());
                        basicInfoApplicant.setEmiDate("Pending LMS");
                        if (customerData.getCommunication() != null) {
                            for (CommunicationResponseDTO communicationData : customerData.getCommunication()) {
                                if (!(communicationData.getAddressType() == null)) {
                                    address.put(communicationData.getAddressType(), communicationData.getFullAddress());
                                    if (!Objects.equals(communicationData.getNumbers(), "") && communicationData.getNumbers() != null) {
                                        numbersReturnResponseDTO.setMobNo(utilityService.mobileNumberMasking(communicationData.getNumbers()));
                                    }
                                    if ((!Objects.equals(numbersReturnResponseDTO.getMobNo(), "")) && !(Objects.equals(numbersReturnResponseDTO.getMobNo(), communicationData.getNumbers()))) {
                                        numbersReturnResponseDTO.setAlternativeMobile(utilityService.mobileNumberMasking(communicationData.getNumbers()));
                                    }
                                } else {
                                    if (!Objects.equals(communicationData.getNumbers(), "")) {
                                        numbersReturnResponseDTO.setAlternativeMobile(utilityService.mobileNumberMasking(communicationData.getNumbers()));
                                    }
                                }
                            }
                        }
                        customerDetails.setBasicInfo(basicInfoApplicant);
                        customerDetails.setAddress(address);
                        customerDetails.setNumbers(numbersReturnResponseDTO);
                        customerList.add(customerDetails);

                    }
                }
            }
            List<AdditionalContactDetailsEntity> additionalContactDetailsEntity = additionalContactDetailsRepository.findAllByLoanId(loanIdNumber);

            if (!additionalContactDetailsEntity.isEmpty()) {
                for (AdditionalContactDetailsEntity additionalContactDetailsEntity1 : additionalContactDetailsEntity) {
                    NumbersReturnResponseDTO numbersReturnResponseDTO1 = new NumbersReturnResponseDTO();
                    BasicInfoReturnResponseDTO basicInfoOther = new BasicInfoReturnResponseDTO();
                    numbersReturnResponseDTO1.setMobNo(utilityService.mobileNumberMasking(additionalContactDetailsEntity1.getMobileNumber().toString()));
                    if (additionalContactDetailsEntity1.getAltMobileNumber() != null) {
                        numbersReturnResponseDTO1.setAlternativeMobile(utilityService.mobileNumberMasking(additionalContactDetailsEntity1.getAltMobileNumber().toString()));
                    }
                    basicInfoOther.setRelation(additionalContactDetailsEntity1.getRelationWithApplicant());
                    basicInfoOther.setFirstName(additionalContactDetailsEntity1.getContactName());
                    CustomerDetailsReturnResponseDTO customerDetailsOther = new CustomerDetailsReturnResponseDTO();
                    customerDetailsOther.setBasicInfo(basicInfoOther);
                    customerDetailsOther.setNumbers(numbersReturnResponseDTO1);
                    customerDetailsOther.setCustomerType("other");
                    customerList.add(customerDetailsOther);
                }
            }
//            log.info("customerList {}", customerList);
            loanRes.getData().setLoanBranch(loanDetailRes.getData().getSourcingBranch());
            loanRes.getData().setEmiCycle(utilityService.addSuffix(loanDetailRes.getData().getEmiCycle()));
            loanRes.getData().setBalancePrincipal(loanDetailRes.getData().getBalancePrincipal());
            loanRes.getData().setEmiPaid(loanSummaryResponse.getData().getInstallmentAmount().getPaid());
            loanRes.getData().setEmiPaidCount(loanSummaryResponse.getData().getNumberOfInstallments().getPaid());
            loanRes.getData().setBalanceEmi(loanSummaryResponse.getData().getInstallmentAmount().getDuesAsOnDate());
            loanRes.getData().setBalanceEmiCount(loanSummaryResponse.getData().getNumberOfInstallments().getDuesAsOnDate());
            loanRes.getData().setLoanApplicationNumber(loanApplicationNumber);
            response.setCustomerDetails(customerList);
            response.setLoanDetails(loanRes.getData());
            baseDTOResponse = new BaseDTOResponse<>(response);
        } catch (Exception e) {
            String errorMessage = e.getMessage();
            String modifiedErrorMessage = utilityService.convertToJSON(errorMessage);
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.get_basic_loan_detail, null, null, modifiedErrorMessage, "failure", Long.parseLong(loanId));
            throw new Exception("1016040");
        }
        return baseDTOResponse;

    }

    @Override
    public BaseDTOResponse<Object> getTaskDetailsBySearchKey(Long userId, String searchKey, Integer pageNo, Integer pageSize) throws Exception {


        BaseDTOResponse<Object> baseDTOResponse;
        try {
            Pageable pageRequest;
            if (pageNo > 0) {
                pageNo = pageNo - 1;
            }
            pageRequest = PageRequest.of(pageNo, pageSize);
            String encryptionKey = rsaUtils.getEncryptionKey(currentUserInfo.getClientId());
            String password = rsaUtils.getPassword(currentUserInfo.getClientId());
            Boolean piiPermission = rsaUtils.getPiiPermission();
            List<Map<String, Object>> taskDetailPages = taskRepository.getTaskDetailsBySearchKey(userId, searchKey, encryptionKey, password, piiPermission, pageRequest);
            if (pageNo > 0) {
                if (taskDetailPages.size() == 0) {
                    return new BaseDTOResponse<>(taskDetailPages);
                }
            }
            baseDTOResponse = new BaseDTOResponse<>(taskDetailPages);
        } catch (Exception e) {
            throw new Exception("1017002");
        }

        return baseDTOResponse;

    }

    @Override
    public BaseDTOResponse<Object> getLoanIdsByLoanId(Long loanId) throws Exception {


        BaseDTOResponse<Object> baseDTOResponse;
        try {
            List<Object> loanIds = taskRepository.getLoanIdsByLoanId(loanId);
            baseDTOResponse = new BaseDTOResponse<>(loanIds);
        } catch (Exception e) {
            throw new Exception("1017002");
        }

        return baseDTOResponse;

    }

}
