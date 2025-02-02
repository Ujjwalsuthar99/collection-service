package com.synoriq.synofin.collection.collectionservice.service.implementation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.synoriq.synofin.collection.collectionservice.common.EnumSQLConstants;
import com.synoriq.synofin.collection.collectionservice.common.errorcode.ErrorCode;
import com.synoriq.synofin.collection.collectionservice.common.exception.CollectionException;
import com.synoriq.synofin.collection.collectionservice.common.exception.CustomException;
import com.synoriq.synofin.collection.collectionservice.config.oauth.CurrentUserInfo;
import com.synoriq.synofin.collection.collectionservice.entity.AdditionalContactDetailsEntity;
import com.synoriq.synofin.collection.collectionservice.entity.RepossessionEntity;
import com.synoriq.synofin.collection.collectionservice.repository.AdditionalContactDetailsRepository;
import com.synoriq.synofin.collection.collectionservice.repository.CollectionConfigurationsRepository;
import com.synoriq.synofin.collection.collectionservice.repository.RepossessionRepository;
import com.synoriq.synofin.collection.collectionservice.repository.TaskRepository;
import com.synoriq.synofin.collection.collectionservice.rest.request.taskdetailsdto.TaskDetailRequestDTO;
import com.synoriq.synofin.collection.collectionservice.rest.request.taskdetailsdto.TaskFilterRequestDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.BaseDTOResponse;
import com.synoriq.synofin.collection.collectionservice.rest.response.taskdetailresponsedtos.*;
import com.synoriq.synofin.collection.collectionservice.rest.response.taskdetailresponsedtos.collateraldetailsresponsedto.CollateralDetailsResponseDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.taskdetailresponsedtos.collateraldetailsresponsedto.CollateralDetailsReturnResponseDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.taskdetailresponsedtos.loansummaryforloandtos.LoanSummaryResponseDTO;
import com.synoriq.synofin.collection.collectionservice.service.ConsumedApiLogService;
import com.synoriq.synofin.collection.collectionservice.service.UtilityService;
import com.synoriq.synofin.collection.collectionservice.service.TaskService;
import com.synoriq.synofin.dataencryptionservice.service.RSAUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpMethod;
import org.springframework.security.concurrent.DelegatingSecurityContextExecutorService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static com.synoriq.synofin.collection.collectionservice.common.GlobalVariables.*;

@Service
@Slf4j
public class TaskServiceImpl implements TaskService {

    private static final String EXCEP_CODE = "1017002";
    private static final String RED_COLOR = "#323232";
    private static final String CASE_STR = "    (case\n";

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
    private EntityManager entityManager;

    @Autowired
    private AdditionalContactDetailsRepository additionalContactDetailsRepository;
    @Autowired
    private CurrentUserInfo currentUserInfo;
    @Autowired
    private RestTemplate restTemplate;

    @Override
    public BaseDTOResponse<Object> getTaskDetails(Long userId, Integer pageNo, Integer pageSize, TaskFilterRequestDTO taskFilterRequestDTO) throws CollectionException {


        if (!taskFilterRequestDTO.getSearchKey().isEmpty() && !taskFilterRequestDTO.getSearchKey().matches("^[a-zA-Z0-9]+$")) {
            throw new IllegalArgumentException("Invalid search key");
        }

        BaseDTOResponse<Object> baseDTOResponse;
        try {
            String encryptionKey = rsaUtils.getEncryptionKey(currentUserInfo.getClientId());
            String password = rsaUtils.getPassword(currentUserInfo.getClientId());
            StringBuilder whereCondition = new StringBuilder();
            whereCondition.append(" and la2.allocated_to_user_id = ").append(userId).append(" ");
            StringBuilder dpdWhereCondition = new StringBuilder();
            boolean breakOccurred = false;
            if (taskFilterRequestDTO.getDpd() != null && !taskFilterRequestDTO.getDpd().isEmpty()) {

                Iterator<String> iterator = taskFilterRequestDTO.getDpd().iterator();
                while (iterator.hasNext()) {
                    String s = iterator.next();
                    boolean conditionMatched = false;
                    if (s.equals("Current")) {
                        dpdWhereCondition.append(breakOccurred ? " or" : " and").append(" la.days_past_due = 0");
                        conditionMatched = true;
                    } else if (s.equals("180+")) {
                        dpdWhereCondition.append(breakOccurred ? " or" : " and").append(" la.days_past_due > 180");
                        conditionMatched = true;
                    }
                    if (conditionMatched) {
                        breakOccurred = true;
                        iterator.remove();
                        break;
                    }
                }
                Optional<Integer> maxValue = taskFilterRequestDTO.getDpd().stream()
                        .map(s -> {
                            String[] parts = s.split("-");
                            return Integer.parseInt(parts[1]);
                        })
                        .max(Integer::compareTo);

                Optional<Integer> minValue = taskFilterRequestDTO.getDpd().stream()
                        .map(s -> {
                            String[] parts = s.split("-");
                            return Integer.parseInt(parts[0]);
                        })
                        .min(Integer::compareTo);

                Integer max = maxValue.orElse(null);
                Integer min = minValue.orElse(null);


                dpdWhereCondition.append(breakOccurred ? " or" : " and").append(" la.days_past_due between ").append(min).append(" and ").append(max);
                String str = dpdWhereCondition.toString().split(" ")[1];
                dpdWhereCondition.insert(str.length() + 1, "(");
                dpdWhereCondition.insert(dpdWhereCondition.length(), ")");


            }
            whereCondition.append(dpdWhereCondition);
            if (!taskFilterRequestDTO.getSearchKey().isEmpty()) {
                whereCondition.append(" and (LOWER(concat_ws(' ', c.first_name, c.last_name)) like LOWER(concat('%', '")
                        .append(taskFilterRequestDTO.getSearchKey())
                        .append("','%')) or LOWER(la.product) like LOWER(concat('%','")
                        .append(taskFilterRequestDTO.getSearchKey()).append("', '%')) or \n")
                        .append("      LOWER(la.loan_application_number) like LOWER(concat('%', '")
                        .append(taskFilterRequestDTO.getSearchKey())
                        .append("', '%')) or LOWER(branch.branch_name) like LOWER(concat('%','")
                        .append(taskFilterRequestDTO.getSearchKey())
                        .append("', '%')) or \n")
                        .append("      LOWER(vehicle.vehicle_registration_no) like LOWER(concat('%','")
                        .append(taskFilterRequestDTO.getSearchKey())
                        .append("', '%'))) ");
            }

            if (taskFilterRequestDTO.getOrder() != null) {
                if (taskFilterRequestDTO.getOrder().equals("ASC")) {
                    whereCondition.append(" order by la.days_past_due asc, la.loan_application_id ASC");
                } else {
                    whereCondition.append(" order by la.days_past_due desc, la.loan_application_id ASC");
                }
            } else {
                whereCondition.append(" order by la.loan_application_id asc");
            }

            String queryString = taskListViewQuery(encryptionKey, password) + whereCondition;




            int offset = pageNo * pageSize;
            Query queryData = this.entityManager.createNativeQuery(queryString);
            queryData.setFirstResult(offset);
            queryData.setMaxResults(pageSize);
            List<Object[]> queryRows = queryData.getResultList();

            List<FilterTaskResponseDTO> data = queryRows.stream()
                    .map(FilterTaskResponseDTO::new).collect(Collectors.toList());

            baseDTOResponse = new BaseDTOResponse<>(data);
        } catch (Exception e) {
            ErrorCode errCode = ErrorCode.getErrorCode(Integer.valueOf(EXCEP_CODE));
            throw new CollectionException(errCode, Integer.valueOf(EXCEP_CODE));
        }

        return baseDTOResponse;

    }

    @Override
    public Object getTaskDetailByLoanId(String token, TaskDetailRequestDTO taskDetailRequestDTO) throws CollectionException {
        final String vehicle = "vehicle";
        final String ffffff = "#ffffff";
        BaseDTOResponse<Object> collateralRes;
        log.info("before token {}", token);
        LoanDetailsResponseDTO loanDetailsResponseDTO = new LoanDetailsResponseDTO();
        TaskDetailReturnResponseDTO response = new TaskDetailReturnResponseDTO();
        BaseDTOResponse<Object> baseDTOResponse = null;
        String loanId = taskDetailRequestDTO.getRequestData().getLoanId();
        Long loanIdNumber = Long.parseLong(loanId);
        String multiReceiptClientCredentials = collectionConfigurationsRepository.findConfigurationValueByConfigurationName(MULTI_RECEIPT_CLIENT_CREDENTIALS);
        if (!multiReceiptClientCredentials.equals("false")) {
            ArrayList<Map<String, Object>> list = null;
            try {
                list = new ObjectMapper().readValue(multiReceiptClientCredentials, new TypeReference<ArrayList<Map<String, Object>>>() {
                });
            } catch (JsonProcessingException e) {
                throw new CustomException(e.getMessage());
            }
            String generatedToken = null;
            try {
                generatedToken = utilityService.getTokenByApiKeySecret(list.get(0));
            } catch (Exception e) {
                throw new CustomException(e.getMessage());
            }
            token = "Bearer " + generatedToken;
        }
        log.info("after token {}", token);
        String finalToken = token;
        try {

            log.info("final token {}", finalToken);

            ExecutorService executorService = Executors.newFixedThreadPool(4);
            executorService = new DelegatingSecurityContextExecutorService(executorService, SecurityContextHolder.getContext());
            Callable<TaskDetailDTOResponse> taskDetailCallable = () -> utilityService.getChargesForLoan(finalToken, taskDetailRequestDTO);
            Callable<LoanBasicDetailsDTOResponse> loanDetailCallable = () -> utilityService.getBasicLoanDetails(finalToken, loanIdNumber);
            Callable<CustomerDetailDTOResponse> customerDetailCallable = () -> utilityService.getCustomerDetails(finalToken, loanIdNumber);
            Callable<LoanSummaryResponseDTO> loanSummaryCallable = () -> utilityService.getLoanSummary(finalToken, loanIdNumber);

            Future<TaskDetailDTOResponse> taskDetailFuture = executorService.submit(taskDetailCallable);
            Future<LoanBasicDetailsDTOResponse> loanDetailFuture = executorService.submit(loanDetailCallable);
            Future<CustomerDetailDTOResponse> customerDetailFuture = executorService.submit(customerDetailCallable);
            Future<LoanSummaryResponseDTO> loanSummaryFuture = executorService.submit(loanSummaryCallable);

            TaskDetailDTOResponse loanRes = taskDetailFuture.get(30, TimeUnit.SECONDS);
            LoanBasicDetailsDTOResponse loanDetailRes = loanDetailFuture.get(30, TimeUnit.SECONDS);
            CustomerDetailDTOResponse customerRes = customerDetailFuture.get(30, TimeUnit.SECONDS);
            LoanSummaryResponseDTO loanSummaryResponse = loanSummaryFuture.get(30, TimeUnit.SECONDS);

            executorService.shutdown();

            log.info("loan charges {}", loanRes);

            if (Objects.equals(loanDetailRes.getData() != null ? loanDetailRes.getData().getProductType() : "", vehicle)) {
                collateralRes = utilityService.getCollaterals(loanIdNumber, token);
                CollateralDetailsResponseDTO collateralResponse = new ObjectMapper().convertValue(collateralRes.getData(), CollateralDetailsResponseDTO.class);

                if (collateralResponse.getData() != null && !collateralResponse.getData().isEmpty()) {
                    CollateralDetailsReturnResponseDTO collateralDetailsReturnResponseDTO = new CollateralDetailsReturnResponseDTO();
                    collateralResponse.getData().forEach((key, value) -> {
                        if (Objects.equals(value.get("collateral_product").toString(), vehicle)) {
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

            if (dpd == 0) {
                dpdTextColor = "#000000";
                dpdBgColor = "#a2e890";
                dpdBucket = "Current";
            } else if (dpd > 0 && dpd <= 30) {
                dpdTextColor = RED_COLOR;
                dpdBgColor = "#61B2FF";
                dpdBucket = "1-30 DPD";
            } else if (dpd >= 31 && dpd <= 60) {
                dpdTextColor = ffffff;
                dpdBgColor = "#2F80ED";
                dpdBucket = "31-60 DPD";
            } else if (dpd >= 61 && dpd <= 90) {
                dpdTextColor = RED_COLOR;
                dpdBgColor = "#FDAAAA";
                dpdBucket = "61-90 DPD";
            } else if (dpd >= 91 && dpd <= 120) {
                dpdTextColor = RED_COLOR;
                dpdBgColor = "#F2994A";
                dpdBucket = "91-120 DPD";
            } else if (dpd >= 121 && dpd <= 150) {
                dpdTextColor = ffffff;
                dpdBgColor = "#FF5359";
                dpdBucket = "121-150 DPD";
            } else if (dpd >= 151 && dpd <= 180) {
                dpdTextColor = ffffff;
                dpdBgColor = "#C83939";
                dpdBucket = "151-180 DPD";
            } else {
                dpdTextColor = ffffff;
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
            if (Objects.equals(isRepossessionEnabled, "true") && repoDpd < dpd && Objects.equals(loanDetailRes.getData() != null ? loanDetailRes.getData().getProductType() : "", vehicle)) {
                repossessionEntity = repossessionRepository.findTop1ByLoanIdOrderByCreatedDateDesc(loanIdNumber);
                if (repossessionEntity != null) {
                    repoId = repossessionEntity.getRepossessionId();
                    repoStatus = repossessionEntity.getStatus();
                }
                repoCardShow = true;
            }

            String loanApplicationNumber = taskRepository.getLoanApplicationNumber(loanIdNumber);

            List<CustomerDetailsReturnResponseDTO> customerList = new ArrayList<>();


            if (!customerRes.getData().isEmpty()) {
                for (CustomerDataResponseDTO customerData : customerRes.getData()) {
                    if (customerData.getBasicInfo() != null) {
                        CustomerDetailsReturnResponseDTO customerDetails = new CustomerDetailsReturnResponseDTO();
                        BasicInfoReturnResponseDTO basicInfoApplicant = new BasicInfoReturnResponseDTO();
                        Map<String, String> address = new HashMap<>();
                        NumbersReturnResponseDTO numbersReturnResponseDTO = new NumbersReturnResponseDTO();
                        customerDetails.setId(customerData.getId());
                        customerDetails.setCustomerType(customerData.getCustomerType());
                        basicInfoApplicant.setId(customerData.getId());
                        basicInfoApplicant.setFirstName(customerData.getBasicInfo().getFirstName());
                        basicInfoApplicant.setMiddleName(customerData.getBasicInfo().getMiddleName());
                        basicInfoApplicant.setLastName(customerData.getBasicInfo().getLastName());
                        basicInfoApplicant.setDob(customerData.getBasicInfo().getDob());

                        if (customerData.getCommunication() != null) {
                            for (CommunicationResponseDTO communicationData : customerData.getCommunication()) {
                                if (!communicationData.getAddressType().isEmpty()) {
                                    address.put(communicationData.getAddressType(), communicationData.getFullAddress());

                                    if (communicationData.getPrimaryNumber() != null && (numbersReturnResponseDTO.getMobNo() == null || numbersReturnResponseDTO.getMobNo().isEmpty())) {
                                        numbersReturnResponseDTO.setMobNo(utilityService.mobileNumberMasking(communicationData.getPrimaryNumber()));
                                    } else {
                                        numbersReturnResponseDTO.setAlternativeMobile(utilityService.mobileNumberMasking(communicationData.getPrimaryNumber() != null ? communicationData.getPrimaryNumber() : ""));
                                    }
                                } else {
                                    if (!Objects.equals(communicationData.getPrimaryNumber(), "")) {
                                        numbersReturnResponseDTO.setAlternativeMobile(utilityService.mobileNumberMasking(communicationData.getPrimaryNumber()));
                                    }
                                }
                            }
                        }
                        customerDetails.setBasicInfo(basicInfoApplicant);
                        customerDetails.setAddress(address.isEmpty() ? null : address);
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
            loanDetailsResponseDTO.setDpd(dpd);
            loanDetailsResponseDTO.setDpdBucket(dpdBucket);
            loanDetailsResponseDTO.setDpdBgColor(dpdBgColor);
            loanDetailsResponseDTO.setDpdTextColor(dpdTextColor);
            if (loanDetailRes.getData() != null) {
                loanDetailsResponseDTO.setPos(loanDetailRes.getData().getPrincipalOutstanding());
                loanDetailsResponseDTO.setLoanAmount(loanDetailRes.getData().getLoanAmount());
                loanDetailsResponseDTO.setLoanStatus(loanDetailRes.getData().getLoanStatus());
                loanDetailsResponseDTO.setEmiAmount(loanDetailRes.getData().getEmiAmount());
                loanDetailsResponseDTO.setLoanTenure(loanDetailRes.getData().getLoanTenure());
                loanDetailsResponseDTO.setAssetClassification(loanDetailRes.getData().getAssetClassification());
            }
            loanDetailsResponseDTO.setEmiDate("Pending LMS");
            response.setCustomerDetails(customerList);
            response.setLoanDetails(loanDetailsResponseDTO);
            baseDTOResponse = new BaseDTOResponse<>(response);
        } catch (InterruptedException ee) {
            log.error("Interrupted Exception Error {}", ee.getMessage());
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            String errorMessage = e.getMessage();
            String modifiedErrorMessage = utilityService.convertToJSON(errorMessage);
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.get_basic_loan_detail, null, null, modifiedErrorMessage, "failure", Long.parseLong(loanId), HttpMethod.POST.name(), "taskSummary" + loanIdNumber);
            ErrorCode errCode = ErrorCode.getErrorCode(1016040);
            throw new CollectionException(errCode, 1016040);
        }
        return baseDTOResponse;

    }

    @Override
    public BaseDTOResponse<Object> getTaskDetailsBySearchKey(Long userId, String searchKey, Integer pageNo, Integer pageSize) throws CollectionException {


        BaseDTOResponse<Object> baseDTOResponse;
        try {
            Pageable pageRequest;
            if (pageNo > 0) {
                pageNo = pageNo - 1;
            }
            pageRequest = PageRequest.of(pageNo, pageSize);
            String encryptionKey = rsaUtils.getEncryptionKey(currentUserInfo.getClientId());
            String password = rsaUtils.getPassword(currentUserInfo.getClientId());
            Boolean piiPermission = true;
            List<Map<String, Object>> taskDetailPages = taskRepository.getTaskDetailsBySearchKey(userId, searchKey, encryptionKey, password, piiPermission, pageRequest);
            baseDTOResponse = new BaseDTOResponse<>(taskDetailPages);
        } catch (Exception e) {
            ErrorCode errCode = ErrorCode.getErrorCode(Integer.valueOf(EXCEP_CODE));
            throw new CollectionException(errCode, Integer.valueOf(EXCEP_CODE));
        }

        return baseDTOResponse;

    }

    @Override
    public BaseDTOResponse<Object> getLoanIdsByLoanId(Long loanId) throws CollectionException {


        BaseDTOResponse<Object> baseDTOResponse;
        try {
            List<Object> loanIds = taskRepository.getLoanIdsByLoanId(loanId);
            baseDTOResponse = new BaseDTOResponse<>(loanIds);
        } catch (Exception e) {
            ErrorCode errCode = ErrorCode.getErrorCode(Integer.valueOf(EXCEP_CODE));
            throw new CollectionException(errCode, Integer.valueOf(EXCEP_CODE));
        }

        return baseDTOResponse;

    }

    private static String taskListViewQuery(String encryptionKey, String password) {

        return "select la.loan_application_id,\n" +
                "    branch.branch_name as branch,\n" +
                "    concat(lms.decrypt_data(c.first_name, '" + encryptionKey + "', '" + password + "', true), ' ', lms.decrypt_data(c.middle_name, '" + encryptionKey + "', '" + password + "', true), ' ', lms.decrypt_data(c.last_name, '" + encryptionKey + "', '" + password + "', true)) as customer_name,\n" +
                "    c.phone1_json->>'mobile' as mobile,\n" +
                "    c.address1_json->>'address' as address,\n" +
                "    la.product as product,\n" +
                "    la.loan_application_number,\n" +
                "    la2.task_purpose,\n" +
                "    count(la.loan_application_id) over () as total_count,\n" +
                CASE_STR +
                "\t   when la.days_past_due = 0 then 'Current'\n" +
                "       when la.days_past_due between 1 and 30 then '1-30 DPD'\n" +
                "       when la.days_past_due between 31 and 60 then '31-60 DPD'\n" +
                "       when la.days_past_due between 61 and 90 then '61-90 DPD'\n" +
                "       when la.days_past_due between 91 and 120 then '91-120 DPD'\n" +
                "       when la.days_past_due between 121 and 150 then '121-150 DPD'\n" +
                "       when la.days_past_due between 151 and 180 then '151-180 DPD'\n" +
                "       else '180+ DPD' end) as days_past_due_bucket,\n" +
                "   la.days_past_due,\n" +
                CASE_STR +
                "\t    when la.days_past_due = 0 then '#a2e890'\n" +
                "        when la.days_past_due between 1 and 30 then '#61B2FF'\n" +
                "        when la.days_past_due between 31 and 60 then '#2F80ED'\n" +
                "        when la.days_past_due between 61 and 90 then '#FDAAAA'\n" +
                "        when la.days_past_due between 91 and 120 then '#F2994A'\n" +
                "        when la.days_past_due between 121 and 150 then '#FF5359'\n" +
                "        when la.days_past_due between 151 and 180 then '#C83939'\n" +
                "        else '#722F37'\n" +
                "    end) as dpd_bg_color_key,\n" +
                CASE_STR +
                "\t    when la.days_past_due = 0 then '#000000'\n" +
                "        when la.days_past_due between 1 and 30 then '#323232'\n" +
                "        when la.days_past_due between 31 and 60 then '#ffffff'\n" +
                "        when la.days_past_due between 61 and 90 then '#323232'\n" +
                "        when la.days_past_due between 91 and 120 then '#323232'\n" +
                "        when la.days_past_due between 121 and 150 then '#ffffff'\n" +
                "        when la.days_past_due between 151 and 180 then '#ffffff'\n" +
                "        else '#ffffff'\n" +
                "    end) as dpd_text_color_key\n" +
                "from\n" +
                "    lms.loan_application la\n" +
                "    join lms.customer_loan_mapping clm on la.loan_application_id = clm.loan_id\n" +
                "    join lms.customer c on clm.customer_id = c.customer_id\n" +
                "    join collection.loan_allocation la2 on la2.loan_id = la.loan_application_id \n" +
                "    left join (select branch_name, branch_id from master.branch) as branch on branch.branch_id = la.branch_id \n" +
                "    left join (select loan_id, vehicle_registration_no from lms.collateral_vehicle) as vehicle on vehicle.loan_id = la.loan_application_id \n" +
                "where\n" +
                "    clm.\"customer_type\" = 'applicant'\n" +
                "    and la.deleted = false\n and la2.deleted = false\n" +
                "    and la.loan_status in ('active')\n";
    }

}
