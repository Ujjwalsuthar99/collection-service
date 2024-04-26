package com.synoriq.synofin.collection.collectionservice.implementation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.synoriq.synofin.collection.collectionservice.common.EnumSQLConstants;
import com.synoriq.synofin.collection.collectionservice.config.oauth.CurrentUserInfo;
import com.synoriq.synofin.collection.collectionservice.entity.AdditionalContactDetailsEntity;
import com.synoriq.synofin.collection.collectionservice.entity.RepossessionEntity;
import com.synoriq.synofin.collection.collectionservice.repository.AdditionalContactDetailsRepository;
import com.synoriq.synofin.collection.collectionservice.repository.CollectionConfigurationsRepository;
import com.synoriq.synofin.collection.collectionservice.repository.RepossessionRepository;
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
import static com.synoriq.synofin.collection.collectionservice.common.GlobalVariables.*;

@Service
@Slf4j
public class TaskServiceImpl implements TaskService {

    @Autowired
    private RSAUtils rsaUtils;
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private RepossessionRepository repossessionRepository;
    @Autowired
    private CollectionConfigurationsRepository collectionConfigurationsRepository;
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
//            Boolean piiPermission = rsaUtils.getPiiPermission();
            Boolean piiPermission = true;
            List<Map<String, Object>> taskDetailPages = taskRepository.getTaskDetailsByPages(userId, encryptionKey, password, piiPermission, pageRequest);
            if (pageNo > 0) {
                if (taskDetailPages.isEmpty()) {
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
        BaseDTOResponse<Object> collateralRes;
        TaskDetailDTOResponse loanRes;
        LoanDetailsResponseDTO loanDetailsResponseDTO = new LoanDetailsResponseDTO();
        CustomerDetailDTOResponse customerRes;
        LoanBasicDetailsDTOResponse loanDetailRes;

        TaskDetailRequestDTO loanDataBody = new ObjectMapper().convertValue(taskDetailRequestDTO, TaskDetailRequestDTO.class);
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

            // lms/loan-modification/v1/service-request/getDataForLoanActions
            loanRes = HTTPRequestService.<Object, TaskDetailDTOResponse>builder()
                    .httpMethod(HttpMethod.POST)
                    .url("http://localhost:1102/v1/getDataForLoanActions")
                    .httpHeaders(httpHeaders)
                    .body(loanDataBody)
                    .typeResponseType(TaskDetailDTOResponse.class)
                    .build().call();

            log.info("loan details jhadsuhbsduh {}", loanRes);
            // creating api logs
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.get_data_for_loan_action, null, loanDataBody, loanRes, "success", Long.parseLong(loanId));

            loanDetailRes = HTTPRequestService.<Object, LoanBasicDetailsDTOResponse>builder()
                    .httpMethod(HttpMethod.GET)
                    .url("http://localhost:1102/v1/getBasicLoanDetails?loanId=" + loanIdNumber)
                    .httpHeaders(httpHeaders)
                    .typeResponseType(LoanBasicDetailsDTOResponse.class)
                    .build().call();

            log.info("getBasicLoanDetails {}", loanDetailRes);
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
                collateralRes = utilityService.getCollaterals(loanIdNumber, token);
                CollateralDetailsResponseDTO collateralResponse = new ObjectMapper().convertValue(collateralRes.getData(), CollateralDetailsResponseDTO.class);

                if (collateralResponse.getData() != null) {
                    CollateralDetailsReturnResponseDTO collateralDetailsReturnResponseDTO = new CollateralDetailsReturnResponseDTO();
                    collateralResponse.getData().forEach((key, value) -> {
                        if (Objects.equals(value.get("collateral_product").toString(), "vehicle")) {
                            collateralDetailsReturnResponseDTO.setChasisNumber(String.valueOf(value.get("chasis_no")));
                            collateralDetailsReturnResponseDTO.setVehicleNumber(String.valueOf(value.get("vehicle_registration_no")));
                            collateralDetailsReturnResponseDTO.setVehicleType(String.valueOf(value.get("vehicle_type")));
                            collateralDetailsReturnResponseDTO.setModel(String.valueOf(value.get("model")));
                            collateralDetailsReturnResponseDTO.setManufacturer(String.valueOf(value.get("manufacturer")));
                            collateralDetailsReturnResponseDTO.setEngineNumber(String.valueOf(value.get("engine_no")));
                            collateralDetailsReturnResponseDTO.setCostOfAsset(value.get("cost_of_asset") != null ? Double.parseDouble(String.valueOf(value.get("cost_of_asset"))) : 0.0);
                        }

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

            String repoStatus = "new";
            boolean repoCardShow = false;
            Long repoId = null;
            RepossessionEntity repossessionEntity;
            String isRepossessionEnabled = collectionConfigurationsRepository.findConfigurationValueByConfigurationName(IS_REPOSSESSION_ENABLED);
            String showRepossessionAfterXDpd = collectionConfigurationsRepository.findConfigurationValueByConfigurationName(SHOW_REPOSSESSION_AFTER_X_DPD);
            int repoDpd = Integer.parseInt(showRepossessionAfterXDpd);
            if (Objects.equals(isRepossessionEnabled, "true") && repoDpd < dpd && Objects.equals(loanDetailRes.getData() != null ? loanDetailRes.getData().getProductType() : "", "vehicle")) {
                repossessionEntity = repossessionRepository.findTop1ByLoanIdOrderByCreatedDateDesc(loanIdNumber);
                if (repossessionEntity != null) {
                    repoId = repossessionEntity.getRepossessionId();
                    repoStatus = repossessionEntity.getStatus();
                }
                repoCardShow = true;
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
                        if (loanDetailRes.getData() != null) {
                            basicInfoApplicant.setPos(loanDetailRes.getData().getPrincipalOutstanding());
                            basicInfoApplicant.setLoanAmount(loanDetailRes.getData().getLoanAmount());
                            basicInfoApplicant.setEmiAmount(loanDetailRes.getData().getEmiAmount());
                            basicInfoApplicant.setLoanTenure(loanDetailRes.getData().getLoanTenure());
                            basicInfoApplicant.setAssetClassification(loanDetailRes.getData().getAssetClassification());
                        }
                        basicInfoApplicant.setEmiDate("Pending LMS");
                        if (customerData.getCommunication() != null) {
                            for (CommunicationResponseDTO communicationData : customerData.getCommunication()) {
                                if (!(communicationData.getAddressType() == null)) {
                                    address.put(communicationData.getAddressType(), communicationData.getFullAddress());
                                    if (!Objects.equals(communicationData.getPrimaryNumber(), "") && communicationData.getPrimaryNumber() != null) {
                                        numbersReturnResponseDTO.setMobNo(utilityService.mobileNumberMasking(communicationData.getPrimaryNumber()));
                                    }
                                    if ((!Objects.equals(numbersReturnResponseDTO.getMobNo(), "")) && !(Objects.equals(numbersReturnResponseDTO.getMobNo(), communicationData.getPrimaryNumber()))) {
                                        numbersReturnResponseDTO.setAlternativeMobile(utilityService.mobileNumberMasking(communicationData.getPrimaryNumber()));
                                    }
                                } else {
                                    if (!Objects.equals(communicationData.getPrimaryNumber(), "")) {
                                        numbersReturnResponseDTO.setAlternativeMobile(utilityService.mobileNumberMasking(communicationData.getPrimaryNumber()));
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
                    basicInfoOther.setAddress(additionalContactDetailsEntity1.getAddress());
                    CustomerDetailsReturnResponseDTO customerDetailsOther = new CustomerDetailsReturnResponseDTO();
                    customerDetailsOther.setBasicInfo(basicInfoOther);
                    customerDetailsOther.setNumbers(numbersReturnResponseDTO1);
                    customerDetailsOther.setCustomerType("other");
                    customerList.add(customerDetailsOther);
                }
            }

            if (loanDetailRes.getData() != null) {
                loanDetailsResponseDTO.setLoanBranch(loanDetailRes.getData().getSourcingBranch());
                loanDetailsResponseDTO.setEmiCycle(utilityService.addSuffix(loanDetailRes.getData().getEmiCycle()));
                loanDetailsResponseDTO.setBalancePrincipal(loanDetailRes.getData().getBalancePrincipal());
            }
            if (loanRes.getData() != null) {
                loanDetailsResponseDTO.setOutStandingCharges(loanRes.getData().getOutStandingCharges() == null ? new ArrayList<>() : loanRes.getData().getOutStandingCharges());
                loanDetailsResponseDTO.setDateOfReceipt(loanRes.getData().getDateOfReceipt());
                loanDetailsResponseDTO.setAdditionInExcessMoney(loanRes.getData().getAdditionInExcessMoney());
            }
            loanDetailsResponseDTO.setEmiPaid(loanSummaryResponse.getData().getInstallmentAmount().getPaid());
            loanDetailsResponseDTO.setEmiPaidCount(loanSummaryResponse.getData().getNumberOfInstallments().getPaid());
            loanDetailsResponseDTO.setBalanceEmi(loanSummaryResponse.getData().getInstallmentAmount().getDuesAsOnDate());
            loanDetailsResponseDTO.setBalanceEmiCount(loanSummaryResponse.getData().getNumberOfInstallments().getDuesAsOnDate());
            loanDetailsResponseDTO.setLoanApplicationNumber(loanApplicationNumber);
            loanDetailsResponseDTO.setRepoStatus(repoStatus);
            loanDetailsResponseDTO.setRepoId(repoId);
            loanDetailsResponseDTO.setRepoCardShow(repoCardShow);
            response.setCustomerDetails(customerList);
            response.setLoanDetails(loanDetailsResponseDTO);
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
//            Boolean piiPermission = rsaUtils.getPiiPermission();
            Boolean piiPermission = true;
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
