package com.synoriq.synofin.collection.collectionservice.service.implementation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.synoriq.synofin.collection.collectionservice.common.EnumSQLConstants;
import com.synoriq.synofin.collection.collectionservice.common.errorcode.ErrorCode;
import com.synoriq.synofin.collection.collectionservice.common.exception.CollectionException;
import com.synoriq.synofin.collection.collectionservice.common.exception.CustomException;
import com.synoriq.synofin.collection.collectionservice.entity.CollectionActivityLogsEntity;
import com.synoriq.synofin.collection.collectionservice.entity.RepossessionEntity;
import com.synoriq.synofin.collection.collectionservice.repository.CollectionActivityLogsRepository;
import com.synoriq.synofin.collection.collectionservice.repository.RepossessionRepository;
import com.synoriq.synofin.collection.collectionservice.rest.request.CollectionActivityLogDTO;
import com.synoriq.synofin.collection.collectionservice.rest.request.repossessiondtos.RepossessionRequestDTO;
import com.synoriq.synofin.collection.collectionservice.rest.request.repossessiondtos.lmsrepossession.LmsRepossessionDTO;
import com.synoriq.synofin.collection.collectionservice.rest.request.repossessiondtos.lmsrepossession.LmsRepossessionDataDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.BaseDTOResponse;
import com.synoriq.synofin.collection.collectionservice.rest.response.createreceiptlmsdtos.ServiceRequestSaveResponse;
import com.synoriq.synofin.collection.collectionservice.rest.response.repossessiondtos.RepossessionCommonDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.repossessiondtos.RepossessionRepoIdResponseDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.repossessiondtos.RepossessionResponseDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.taskdetailresponsedtos.collateraldetailsresponsedto.CollateralDetailsResponseDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.taskdetailresponsedtos.LoanBasicDetailsDTOResponse;
import com.synoriq.synofin.collection.collectionservice.service.ActivityLogService;
import com.synoriq.synofin.collection.collectionservice.service.ConsumedApiLogService;
import com.synoriq.synofin.collection.collectionservice.service.RepossessionService;
import com.synoriq.synofin.collection.collectionservice.service.UtilityService;
import com.synoriq.synofin.collection.collectionservice.service.utilityservice.HTTPRequestService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.WordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.*;
@Service
@Slf4j
public class RepossessionServiceImpl implements RepossessionService {

    private static final String SUCCESS_STATUS = "success";
    private static final String ACTIVITY_NAME_STR = "activity_name";
    private static final String ACTIVITY_DATE_STR = "activity_date";
    private static final String ACTIVITY_BY_STR = "activity_by";

    @Autowired
    private RepossessionRepository repossessionRepository;

    @Autowired
    private ActivityLogService activityLogService;

    @Autowired
    private UtilityService utilityService;

    @Autowired
    private ConsumedApiLogService consumedApiLogService;
    @Autowired
    private CollectionActivityLogsRepository collectionActivityLogsRepository;
    @Autowired
    private RestTemplate restTemplate;

    @Override
    public BaseDTOResponse<Object> getRepossessionData(Long loanId) throws CustomException {
        Optional<RepossessionEntity> repossessionEntity = Optional.of(new RepossessionEntity());
        List<Long> referenceIds = new ArrayList<>();
        RepossessionResponseDTO repossessionResponseDTO = new RepossessionResponseDTO();
        boolean isHistory = false;
        List<RepossessionCommonDTO> current = new ArrayList<>();
        List<RepossessionCommonDTO> history = new ArrayList<>();
        try {

            List<CollectionActivityLogsEntity> collectionActivityLogsEntityList = collectionActivityLogsRepository.getActivityLogsDataByLoanIdWithRepossession(loanId);
            if (collectionActivityLogsEntityList.isEmpty()) {
                for (CollectionActivityLogsEntity collectionActivityLogsEntity : collectionActivityLogsEntityList) {
                    RepossessionCommonDTO repossessionCommonDTO = new RepossessionCommonDTO();
                    if (!referenceIds.contains(collectionActivityLogsEntity.getReferenceId())) {
                        repossessionEntity = repossessionRepository.findById(collectionActivityLogsEntity.getReferenceId());
                        isHistory = referenceIds.isEmpty();
                    }
                    if (repossessionEntity.isPresent()) {
                        Map<String, Object> yardJson = new ObjectMapper().convertValue(repossessionEntity.get().getYardDetailsJson(), Map.class);
                        Map<String, Object> remarksJson = new ObjectMapper().convertValue(repossessionEntity.get().getRemarks(), Map.class);

                        repossessionCommonDTO.setStatus(collectionActivityLogsEntity.getActivityName().substring(13));
                        repossessionCommonDTO.setAgency(repossessionEntity.get().getRecoveryAgency());
                        repossessionCommonDTO.setAttachments(collectionActivityLogsEntity.getImages());
                        repossessionCommonDTO.setRemark(String.valueOf(remarksJson.get(collectionActivityLogsEntity.getActivityName().substring(13)+"_remarks")));
                        repossessionCommonDTO.setActionBy(repossessionRepository.getNameFromUsers(collectionActivityLogsEntity.getActivityBy()));
                        repossessionCommonDTO.setApprovedBy(repossessionRepository.getNameFromUsers(collectionActivityLogsEntity.getActivityBy()));
                        repossessionCommonDTO.setAssignTo(repossessionEntity.get().getAssignedTo() != null ? repossessionRepository.getNameFromUsers(repossessionEntity.get().getAssignedTo()) : null);
                        repossessionCommonDTO.setCreateDate(collectionActivityLogsEntity.getActivityDate());
                        repossessionCommonDTO.setInitiatedBy(repossessionRepository.getNameFromUsers(repossessionEntity.get().getCreatedBy()));
                        repossessionCommonDTO.setYardContactNumber(repossessionEntity.get().getYardDetailsJson() != null ? String.valueOf(yardJson.get("yard_contact_number")) : null);
                        repossessionCommonDTO.setYardName(repossessionEntity.get().getYardDetailsJson() != null ? String.valueOf(yardJson.get("yard_name")) : null);
                        repossessionCommonDTO.setVehicleHandoverTo(repossessionEntity.get().getYardDetailsJson() != null ? String.valueOf(yardJson.get("vehicle_handover_to")) : null);

                        if (isHistory) {
                            history.add(repossessionCommonDTO);
                        } else {
                            current.add(repossessionCommonDTO);
                        }
                    }
                    referenceIds.add(collectionActivityLogsEntity.getReferenceId());
                }
                repossessionResponseDTO.setCurrent(current);
                repossessionResponseDTO.setHistory(history);
            } else {
                repossessionResponseDTO.setCurrent(new ArrayList<>());
                repossessionResponseDTO.setHistory(new ArrayList<>());
            }

            return new BaseDTOResponse<>(repossessionResponseDTO);
        } catch (Exception e) {
            throw new CustomException(e.getMessage());
        }
    }

    @Override
    public BaseDTOResponse<Object> getAllRepossession() throws CustomException {
        List<Map<String, Object>>  repossessionData;
        try {
            repossessionData = repossessionRepository.getAllRepossession();
            return new BaseDTOResponse<>(repossessionData);
        } catch (Exception e) {
            throw new CustomException(e.getMessage());
        }
    }

    @Override
    public BaseDTOResponse<Object> initiateRepossession(String token, RepossessionRequestDTO requestDto) throws CustomException {
        CollectionActivityLogDTO collectionActivityLogDTO = new CollectionActivityLogDTO();
        RepossessionEntity repossessionEntity = new RepossessionEntity();
        try {
            Map<String, Object> remarksJson = new HashMap<>();
            remarksJson.put("initiated_remarks", requestDto.getRemarks());

            RepossessionEntity repossessionEntity1 = repossessionRepository.findByLoadIdAndInitiatedStatus(requestDto.getLoanId());
            if (repossessionEntity1 == null) {
                repossessionEntity.setRemarks(remarksJson);
                repossessionEntity.setLoanId(requestDto.getLoanId());
                repossessionEntity.setDeleted(false);
                repossessionEntity.setCreatedDate(new Date());
                repossessionEntity.setCreatedBy(requestDto.getInitiatedBy());
                repossessionEntity.setStatus("initiated");
                repossessionRepository.save(repossessionEntity);

                // created activity for repossession initiate
                collectionActivityLogDTO.setActivityName("repossession_initiated");
                collectionActivityLogDTO.setAddress("{}");
                collectionActivityLogDTO.setBatteryPercentage(requestDto.getBatteryPercentage());
                collectionActivityLogDTO.setImages(requestDto.getAttachments());
                collectionActivityLogDTO.setDeleted(false);
                collectionActivityLogDTO.setRemarks("Repossession initiated against loan id" + requestDto.getLoanId());
                collectionActivityLogDTO.setGeolocationData(requestDto.getGeoLocationData());
                collectionActivityLogDTO.setLoanId(requestDto.getLoanId());
                collectionActivityLogDTO.setUserId(requestDto.getInitiatedBy());
                collectionActivityLogDTO.setDistanceFromUserBranch(0D);
                Long activityId = activityLogService.createActivityLogs(collectionActivityLogDTO, token);
                CollectionActivityLogsEntity collectionActivityLogsEntity = collectionActivityLogsRepository.findByCollectionActivityLogsId(activityId);


                collectionActivityLogsEntity.setReferenceId(repossessionEntity.getRepossessionId());
                collectionActivityLogsRepository.save(collectionActivityLogsEntity);
            } else {
                ErrorCode errCode = ErrorCode.getErrorCode(1016046);
                throw new CollectionException(errCode, 1016046);
            }

        } catch (Exception e) {
            throw new CustomException(e.getMessage());
        }
        return new BaseDTOResponse<>(repossessionEntity);
    }

    @Override
    public BaseDTOResponse<Object> yardRepossession(String token, RepossessionRequestDTO requestDto) throws CustomException {
        CollectionActivityLogDTO collectionActivityLogDTO = new CollectionActivityLogDTO();
        RepossessionEntity repossessionEntity;
        Long lmsRepoId = null;
        BaseDTOResponse<Object> resp;
        try {
            repossessionEntity = repossessionRepository.findByRepossessionId(requestDto.getRepoId());
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String repoDate = dateFormat.format(repossessionEntity.getCreatedDate());
            String tranDate = dateFormat.format(new Date());
            if (Objects.equals(requestDto.getStatus(), "yard")) {
                LmsRepossessionDTO lmsRepossessionDTO = new LmsRepossessionDTO();
                LmsRepossessionDataDTO lmsRepossessionDataDTO = new LmsRepossessionDataDTO();
                Map<String, String> yardJson = new ObjectMapper().convertValue(repossessionEntity.getYardDetailsJson(), Map.class);
                lmsRepossessionDataDTO.setRepoDate(repoDate);
                lmsRepossessionDataDTO.setRemarks(requestDto.getStatus());
                lmsRepossessionDataDTO.setRepossessionAgency(requestDto.getRecoveryAgency());
                lmsRepossessionDataDTO.setCharges(new ArrayList<>());
                lmsRepossessionDataDTO.setNumberOfInstruments(1);
                lmsRepossessionDataDTO.setSourcingRm(repossessionRepository.getNameFromUsers(repossessionEntity.getAssignedTo()));
                lmsRepossessionDataDTO.setBranch("");
                lmsRepossessionDataDTO.setYardAddress(yardJson.get("yard_address"));
                lmsRepossessionDataDTO.setTransactionDate(tranDate);
                lmsRepossessionDTO.setServiceRequestId("");
                lmsRepossessionDTO.setServiceRequestSubtype("2");
                lmsRepossessionDTO.setServiceType("");
                lmsRepossessionDTO.setRequestData(lmsRepossessionDataDTO);
                lmsRepossessionDTO.setServiceRequestType("collateral_repossession");
                // calling LMS repo & seize api
                resp = lmsRepossession(token, lmsRepossessionDTO);
                lmsRepoId = Long.parseLong(String.valueOf(resp.getData()));
            }
            Map<String, Object> remarksJson = new ObjectMapper().convertValue(repossessionEntity.getRemarks(), Map.class);
            remarksJson.put(requestDto.getStatus()+"_remarks", requestDto.getRemarks());
            repossessionEntity.setRemarks(remarksJson);
            repossessionEntity.setStatus(requestDto.getStatus());
            repossessionEntity.setAssignedTo(requestDto.getAssignedTo());
            repossessionEntity.setRecoveryAgency(requestDto.getRecoveryAgency());
            repossessionEntity.setCollateralJson(requestDto.getCollateralJson());
            repossessionEntity.setLmsRepoId(lmsRepoId);
            repossessionEntity.setYardDetailsJson(requestDto.getYardDetailsJson());
            repossessionRepository.save(repossessionEntity);

            // created activity for repossession flow
            collectionActivityLogDTO.setActivityName("repossession_" + requestDto.getStatus());
            collectionActivityLogDTO.setAddress("{}");
            collectionActivityLogDTO.setBatteryPercentage(requestDto.getBatteryPercentage());
            collectionActivityLogDTO.setImages(requestDto.getAttachments());
            collectionActivityLogDTO.setDeleted(false);
            collectionActivityLogDTO.setRemarks("Repossession " + StringUtils.capitalize(requestDto.getStatus()) + " against loan id " + requestDto.getLoanId());
            collectionActivityLogDTO.setGeolocationData(requestDto.getGeoLocationData());
            collectionActivityLogDTO.setLoanId(requestDto.getLoanId());
            collectionActivityLogDTO.setUserId(requestDto.getInitiatedBy());
            collectionActivityLogDTO.setDistanceFromUserBranch(0D);
            Long activityId = activityLogService.createActivityLogs(collectionActivityLogDTO, token);
            CollectionActivityLogsEntity collectionActivityLogsEntity = collectionActivityLogsRepository.findByCollectionActivityLogsId(activityId);
            collectionActivityLogsEntity.setReferenceId(repossessionEntity.getRepossessionId());
            collectionActivityLogsRepository.save(collectionActivityLogsEntity);

        } catch (Exception e) {
            throw new CustomException(e.getMessage());
        }
        return new BaseDTOResponse<>(repossessionEntity);
    }

    @Override
    public BaseDTOResponse<Object> getDataByRepoId(String token, Long repoId) throws CustomException {
        LoanBasicDetailsDTOResponse loanDetailRes;
        List<Map<String, Object>> list = new ArrayList<>();
        RepossessionRepoIdResponseDTO repossessionRepoIdResponseDTO = new RepossessionRepoIdResponseDTO();
        final String[] vehicleType = {""};
        final String[] manufacturer = {""};
        ObjectMapper objectMapper = new ObjectMapper();
        String activityBy = "";
        String activityDate = "";
        try {
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("Authorization", token);
            httpHeaders.add("Content-Type", "application/json");
            Optional<RepossessionEntity> repossessionEntity = repossessionRepository.findById(repoId);
            List<Map<String, Object>> collectionActivityLogsEntityList = collectionActivityLogsRepository.getActivityLogsDataByReferenceIdLoanIdWithRepossession(repoId);
            for(Map<String, Object> entity: collectionActivityLogsEntityList) {
                JsonNode imagesNode = objectMapper.readTree(String.valueOf(entity.get("images")));
                Map<String, Object> ent = new HashMap<>();
                ent.put(ACTIVITY_DATE_STR, entity.get(ACTIVITY_DATE_STR));
                ent.put(ACTIVITY_NAME_STR, WordUtils.capitalizeFully(String.valueOf(entity.get(ACTIVITY_NAME_STR)).replace("_", " ")));
                ent.put("remarks", entity.get("remarks"));
                ent.put("collection_activity_logs_id", entity.get("collection_activity_logs_id"));
                ent.put(ACTIVITY_BY_STR, entity.get(ACTIVITY_BY_STR));
                ent.put("images", new Gson().fromJson(String.valueOf(imagesNode), Object.class));
                list.add(ent);
                if (Objects.equals(String.valueOf(entity.get(ACTIVITY_NAME_STR)), "repossession_yard")) {
                    activityBy = String.valueOf(entity.get(ACTIVITY_BY_STR));
                    activityDate = String.valueOf(entity.get(ACTIVITY_DATE_STR));
                }
            }
            if (repossessionEntity.isPresent()) {
                long loanId = repossessionEntity.get().getLoanId();
                loanDetailRes = HTTPRequestService.<Object, LoanBasicDetailsDTOResponse>builder()
                        .httpMethod(HttpMethod.GET)
                        .url("http://localhost:1102/v1/getBasicLoanDetails?loanId=" + loanId)
                        .httpHeaders(httpHeaders)
                        .typeResponseType(LoanBasicDetailsDTOResponse.class)
                        .build().call(restTemplate);

                // creating api logs
                consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.get_basic_loan_detail, null, null, loanDetailRes, SUCCESS_STATUS, loanId, HttpMethod.GET.name(), "getBasicLoanDetails" + loanId);
                if(loanDetailRes.getData() != null) {
                    String mobileNumber = repossessionRepository.getMobileNumber(loanDetailRes.getData().getCustomerId());

                    CollateralDetailsResponseDTO collateralResponse = HTTPRequestService.<Object, CollateralDetailsResponseDTO>builder()
                            .httpMethod(HttpMethod.GET)
                            .url("http://localhost:1102/v1/getCollaterals?loanId=" + loanId)
                            .httpHeaders(httpHeaders)
                            .typeResponseType(CollateralDetailsResponseDTO.class)
                            .build().call(restTemplate);

                    consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.get_collaterals, null, null, collateralResponse, SUCCESS_STATUS, loanId, HttpMethod.GET.name(), "getCollaterals?loanId=" + loanId);

                    collateralResponse.getData().forEach((key, value) -> {
                        vehicleType[0] = String.valueOf(value.get("vehicle_type"));
                        manufacturer[0] = String.valueOf(value.get("manufacturer"));
                    });

                    int dpd = loanDetailRes.getData().getDpd();
                    String dpdBucket;
                    if (dpd == 0) {
                        dpdBucket = "Current";
                    }else if (dpd >= 1 && dpd <= 30) {
                        dpdBucket = "1-30 DPD";
                    } else if (dpd >= 31 && dpd <= 60) {
                        dpdBucket = "31-60 DPD";
                    } else if (dpd >= 61 && dpd <= 90) {
                        dpdBucket = "61-90 DPD";
                    } else if (dpd >= 91 && dpd <= 120) {
                        dpdBucket = "91-120 DPD";
                    } else if (dpd >= 121 && dpd <= 150) {
                        dpdBucket = "121-150 DPD";
                    } else if (dpd >= 151 && dpd <= 180) {
                        dpdBucket = "151-180 DPD";
                    } else {
                        dpdBucket = "180+ DPD";
                    }
                    Map<String, Object> yardDetailsJson = objectMapper.convertValue(repossessionEntity.get().getYardDetailsJson(), Map.class);
                    if (yardDetailsJson != null) {
                        yardDetailsJson.put(ACTIVITY_BY_STR, activityBy);
                        yardDetailsJson.put(ACTIVITY_DATE_STR, activityDate);
                    }
                    repossessionRepoIdResponseDTO = RepossessionRepoIdResponseDTO.builder().
                            dpd(dpdBucket).
                            manufacturer(manufacturer[0]).
                            vehicleType(vehicleType[0]).
                            repoStatus(repossessionEntity.get().getStatus()).
                            customerName(loanDetailRes.getData().getCustomerName()).
                            loanAmount(loanDetailRes.getData().getLoanAmount()).
                            loanNumber(loanDetailRes.getData().getLoanApplicationNumber()).
                            loanId(loanId).
                            mobileNumber(mobileNumber).
                            emiStartDate(loanDetailRes.getData().getInterestStartDate()).
                            repoInitiateDate(repossessionEntity.get().getCreatedDate()).
                            totalDue(loanDetailRes.getData().getBalancePrincipal()).
                            isYard(Objects.equals(repossessionEntity.get().getStatus(), "yard")).
                            yardDetails(yardDetailsJson).
                            auditLogs(list).
                            build();
                }
            }
        } catch (Exception e) {
            throw new CustomException(e.getMessage());
        }
        return new BaseDTOResponse<>(repossessionRepoIdResponseDTO);
    }
    @Override
    public BaseDTOResponse<Object> lmsRepossession(String bearerToken, LmsRepossessionDTO requestDto) throws CustomException {
        ServiceRequestSaveResponse res;
        long receiptNumber;
        Map<String, Object> baseRequestDto = new HashMap<>();
        baseRequestDto.put("data", requestDto);
        baseRequestDto.put("user_reference_number", "");
        try {
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("Authorization", bearerToken);
            httpHeaders.add("Content-Type", "application/json");

            res = HTTPRequestService.<Object, ServiceRequestSaveResponse>builder()
                    .httpMethod(HttpMethod.POST)
                    .url("http://localhost:1102/v1/createReceipt")
                    .httpHeaders(httpHeaders)
                    .body(baseRequestDto)
                    .typeResponseType(ServiceRequestSaveResponse.class)
                    .build().call(restTemplate);
            receiptNumber = res.getData() != null ? res.getData().getServiceRequestId() : null;
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.lms_repossession, null, null, res, SUCCESS_STATUS, Long.parseLong(requestDto.getLoanId()), HttpMethod.POST.name(), "lms_repossession");

        } catch (Exception e) {
            String errorMessage = e.getMessage();
            String modifiedErrorMessage = utilityService.convertToJSON(errorMessage);
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.lms_repossession, null, null, modifiedErrorMessage, "failure", Long.parseLong(requestDto.getLoanId()), HttpMethod.POST.name(), "lms_repossession");
            log.error("{}", e.getMessage());
            throw new CustomException(e.getMessage());
        }
        return new BaseDTOResponse<>(receiptNumber);
    }
}


