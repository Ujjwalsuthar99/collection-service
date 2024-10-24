package com.synoriq.synofin.collection.collectionservice.service.implementation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.synoriq.synofin.collection.collectionservice.common.errorcode.ErrorCode;
import com.synoriq.synofin.collection.collectionservice.common.exception.CollectionException;
import com.synoriq.synofin.collection.collectionservice.common.exception.CustomException;
import com.synoriq.synofin.collection.collectionservice.entity.CollectionActivityLogsEntity;
import com.synoriq.synofin.collection.collectionservice.entity.RegisteredDeviceInfoEntity;
import com.synoriq.synofin.collection.collectionservice.repository.CollectionActivityLogsRepository;
import com.synoriq.synofin.collection.collectionservice.repository.RegisteredDeviceInfoRepository;
import com.synoriq.synofin.collection.collectionservice.rest.request.CollectionActivityLogDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.activitylogdtos.ActivityLogBaseResponseDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.activitylogdtos.ActivityLogCustomResponseDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.activitylogdtos.ActivityLogDataDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.activitylogdtos.ActivityLogResponseDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.BaseDTOResponse;
import com.synoriq.synofin.collection.collectionservice.rest.response.userdetailbytokendtos.UserDetailByTokenDTOResponse;
import com.synoriq.synofin.collection.collectionservice.service.ActivityLogService;
import com.synoriq.synofin.collection.collectionservice.service.UtilityService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.synoriq.synofin.collection.collectionservice.common.ActivityRemarks.LOGOUT_REMARKS;

@Slf4j
@Service
public class ActivityLogServiceImpl implements ActivityLogService {
    @Autowired
    private CollectionActivityLogsRepository collectionActivityLogsRepository;
    @Autowired
    private UtilityService utilityService;

    @Autowired
    private RegisteredDeviceInfoRepository registeredDeviceInfoRepository;
    @Override
    public BaseDTOResponse<Object> getActivityLogsById(Long activityLogsId) throws CollectionException {

        BaseDTOResponse<Object> response;
        CollectionActivityLogsEntity collectionActivityLogsEntity = collectionActivityLogsRepository.findByCollectionActivityLogsId(activityLogsId);
        if (collectionActivityLogsEntity != null) {
            ActivityLogResponseDTO activityLogResponseDTO = new ActivityLogResponseDTO();
            activityLogResponseDTO.setCollectionActivityLogsId(collectionActivityLogsEntity.getCollectionActivityLogsId());
            activityLogResponseDTO.setLoanId(collectionActivityLogsEntity.getLoanId());
            activityLogResponseDTO.setUserId(collectionActivityLogsEntity.getActivityBy());
            activityLogResponseDTO.setActivityDate(collectionActivityLogsEntity.getActivityDate());
            activityLogResponseDTO.setActivityName(collectionActivityLogsEntity.getActivityName());
            activityLogResponseDTO.setAddress(collectionActivityLogsEntity.getAddress());
            activityLogResponseDTO.setGeolocation(collectionActivityLogsEntity.getGeolocation());
            activityLogResponseDTO.setImages(collectionActivityLogsEntity.getImages());

            response = new BaseDTOResponse<>(activityLogResponseDTO);
            return response;
        } else {
            ErrorCode errCode = ErrorCode.getErrorCode(1016025);
            throw new CollectionException(errCode, 1016025);
        }

    }

    @Override
    public BaseDTOResponse<Object> getActivityLogsByUserIdWithDuration(Integer page, Integer size, Long userId, Date fromDate, Date endDate) throws CustomException {
        try {
            Date toDate = checkToDate(endDate);
            Pageable pageable = PageRequest.of(page, size);
            List<ActivityLogResponseDTO> collectionActivityLogs = collectionActivityLogsRepository.getActivityLogsUserWIseByDuration(userId, fromDate, toDate, pageable);
            return new BaseDTOResponse<>(collectionActivityLogs);
        } catch (Exception e) {
            throw new CustomException(e.getMessage());
        }
    }


    @Override
    public ActivityLogBaseResponseDTO getActivityLogsByLoanIdWithDuration(Integer page, Integer size, Long loanId, Date fromDate, Date endDate, String filterBy) throws CustomException {
        final String collectionActivityId = "collection_activity_logs_id";
        Date toDate = checkToDate(endDate);
        Pageable pageable = PageRequest.of(page, size);
        List<Map<String, Object>> collectionActivityLogs;
        ActivityLogBaseResponseDTO activityLogBaseResponseDTO = new ActivityLogBaseResponseDTO();
        ActivityLogDataDTO activityLogDataDTO = new ActivityLogDataDTO();
        List<ActivityLogCustomResponseDTO> responseData = new ArrayList<>();
        try{
            if (!filterBy.isEmpty()) {
                collectionActivityLogs = collectionActivityLogsRepository.getActivityLogsLoanWiseByDurationByFilter(loanId, fromDate, toDate, filterBy, pageable);
            } else {
                collectionActivityLogs = collectionActivityLogsRepository.getActivityLogsLoanWiseByDuration(loanId, fromDate, toDate, pageable);
            }
            if (!collectionActivityLogs.isEmpty()) {
                for (Map<String, Object> collectionActivityLog : collectionActivityLogs) {
                    String followUpReason = "";
                    ActivityLogCustomResponseDTO activityLogCustomResponseDTO = new ActivityLogCustomResponseDTO();
                    ObjectMapper objectMapper = new ObjectMapper();
                    JsonNode addressNode = objectMapper.readTree(String.valueOf(collectionActivityLog.get("address")));
                    JsonNode imagesNode = objectMapper.readTree(String.valueOf(collectionActivityLog.get("images")));
                    JsonNode geoLocationDataNode = objectMapper.readTree(String.valueOf(collectionActivityLog.get("geo_location_data")));

                    activityLogCustomResponseDTO.setCollectionActivityLogsId(Long.parseLong(String.valueOf(collectionActivityLog.get(collectionActivityId))));
                    activityLogCustomResponseDTO.setActivityDate(String.valueOf(collectionActivityLog.get("activity_date")));
                    activityLogCustomResponseDTO.setActivityBy(Long.parseLong(String.valueOf(collectionActivityLog.get("activity_by"))));
                    activityLogCustomResponseDTO.setActivityName(String.valueOf(collectionActivityLog.get("activity_name")));
                    activityLogCustomResponseDTO.setRemarks(String.valueOf(collectionActivityLog.get("remarks")));
                    if (Objects.equals(String.valueOf(collectionActivityLog.get("activity_name")), "create_followup")) {
                        followUpReason = collectionActivityLogsRepository.getFollowUpReason(Long.parseLong(String.valueOf(collectionActivityLog.get(collectionActivityId))));
                        activityLogCustomResponseDTO.setRemarks("FollowUp Id " + collectionActivityLog.get(collectionActivityId) + ", FollowUp Reason: " + followUpReason);
                    }
                    activityLogCustomResponseDTO.setDistanceFromUserBranch(Double.parseDouble(String.valueOf(collectionActivityLog.get("distance_from_user_branch"))));
                    activityLogCustomResponseDTO.setLoanId(Long.parseLong(String.valueOf(collectionActivityLog.get("loan_id"))));
                    if (Objects.equals(String.valueOf(collectionActivityLog.get("battery_percentage")), "null")) {
                        activityLogCustomResponseDTO.setBatteryPercentage(0L);
                    } else {
                        activityLogCustomResponseDTO.setBatteryPercentage(Long.parseLong(String.valueOf(collectionActivityLog.get("battery_percentage"))));
                    }
                    activityLogCustomResponseDTO.setUserName(String.valueOf(collectionActivityLog.get("user_name")));
                    activityLogCustomResponseDTO.setIsReceipt(Boolean.valueOf(String.valueOf(collectionActivityLog.get("is_receipt"))));
                    activityLogCustomResponseDTO.setReceiptId((!Objects.equals(collectionActivityLog.get("receipt_id"), null) ? Long.parseLong(String.valueOf(collectionActivityLog.get("receipt_id"))) : null));
                    activityLogCustomResponseDTO.setAddress(new Gson().fromJson(String.valueOf(addressNode), Object.class));
                    activityLogCustomResponseDTO.setImages(new Gson().fromJson(String.valueOf(imagesNode), Object.class));
                    activityLogCustomResponseDTO.setGeolocation(new Gson().fromJson(String.valueOf(geoLocationDataNode), Object.class));

                    responseData.add(activityLogCustomResponseDTO);
                }
                activityLogDataDTO.setData(responseData);
                activityLogDataDTO.setTotalCount(Long.parseLong(String.valueOf(collectionActivityLogs.get(0).get("total_rows"))));
                activityLogBaseResponseDTO.setData(activityLogDataDTO);
            } else {
                activityLogDataDTO.setData(new ArrayList<>());
                activityLogDataDTO.setTotalCount(0L);
                activityLogBaseResponseDTO.setData(activityLogDataDTO);
            }
            return activityLogBaseResponseDTO;
        }
        catch (Exception ee){
            throw new CustomException(ee.getMessage());
        }
    }

    @Override
    public Long createActivityLogs(CollectionActivityLogDTO activityLogRequest, String token) throws CustomException {

        try{
            if (activityLogRequest.getLoanId() == null) {
                log.error("Requested parameter loan id cannot be blank");
                ErrorCode errCode = ErrorCode.getErrorCode(101809);
                throw new CollectionException(errCode, 101809);
            }
            CollectionActivityLogsEntity collectionActivityLogsEntity = new CollectionActivityLogsEntity();

            if (activityLogRequest.getActivityName().equals("login")) {
                UserDetailByTokenDTOResponse resp = utilityService.getUserDetailsByToken(token);
                List<String> permissions = resp.getData().getUserData().getPermissions();
                if (!permissions.contains("collection_login")) {
                    return 0L;
                }
            }

            if (activityLogRequest.getActivityName().equals("logout")) {
                UserDetailByTokenDTOResponse res = utilityService.getUserDetailsByToken(token);
                collectionActivityLogsEntity.setActivityBy(res.getData().getUserData().getUserId());
                String userName = res.getData().getUserData().getUserName();
                String updatedRemarks = LOGOUT_REMARKS;
                updatedRemarks = updatedRemarks.replace("{user_name}", userName);
                collectionActivityLogsEntity.setRemarks(updatedRemarks);
            } else {
                collectionActivityLogsEntity.setActivityBy(activityLogRequest.getUserId());
                collectionActivityLogsEntity.setRemarks(activityLogRequest.getRemarks());
            }
            collectionActivityLogsEntity.setActivityDate(new Date());
            collectionActivityLogsEntity.setActivityName(activityLogRequest.getActivityName());
            collectionActivityLogsEntity.setDeleted(activityLogRequest.getDeleted());
            collectionActivityLogsEntity.setLoanId(activityLogRequest.getLoanId());
            collectionActivityLogsEntity.setDistanceFromUserBranch(activityLogRequest.getDistanceFromUserBranch());
            collectionActivityLogsEntity.setAddress(activityLogRequest.getAddress());
            collectionActivityLogsEntity.setImages(activityLogRequest.getImages());
            collectionActivityLogsEntity.setGeolocation(activityLogRequest.getGeolocationData());
            collectionActivityLogsEntity.setBatteryPercentage(activityLogRequest.getBatteryPercentage());

            collectionActivityLogsRepository.save(collectionActivityLogsEntity);

            List<RegisteredDeviceInfoEntity> registeredDeviceInfoEntityList = registeredDeviceInfoRepository.findDeviceInfoByUserId(activityLogRequest.getUserId());
            for (RegisteredDeviceInfoEntity registeredDeviceInfoEntity : registeredDeviceInfoEntityList) {
                if (Objects.equals(registeredDeviceInfoEntity.getStatus(), "active")) {
                    registeredDeviceInfoEntity.setLastAppUsage(new Date());
                    registeredDeviceInfoRepository.save(registeredDeviceInfoEntity);
                }
            }
            return collectionActivityLogsEntity.getCollectionActivityLogsId();
        } catch(Exception e){
            throw new CustomException(e.getMessage());
        }

    }

    private Date checkToDate(Date toDate){return DateUtils.addDays(toDate, 1);}

    @Override
    public List<CollectionActivityLogsEntity> getActivityLogsByReferenceId(Long referenceId) throws CustomException {
        try {
            return collectionActivityLogsRepository.findActivityLogByReferenceId(referenceId);
        } catch (Exception e) {
            throw new CustomException(e.getMessage());
        }
    }
}
