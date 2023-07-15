package com.synoriq.synofin.collection.collectionservice.implementation;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.synoriq.synofin.collection.collectionservice.entity.CollectionActivityLogsEntity;
import com.synoriq.synofin.collection.collectionservice.entity.RegisteredDeviceInfoEntity;
import com.synoriq.synofin.collection.collectionservice.repository.CollectionActivityLogsRepository;
import com.synoriq.synofin.collection.collectionservice.repository.RegisteredDeviceInfoRepository;
import com.synoriq.synofin.collection.collectionservice.rest.response.ActivityLogDTOs.ActivityLogBaseResponseDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.ActivityLogDTOs.ActivityLogCustomResponseDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.ActivityLogDTOs.ActivityLogDataDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.ActivityLogDTOs.ActivityLogResponseDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.BaseDTOResponse;
import com.synoriq.synofin.collection.collectionservice.rest.response.UserDetailByTokenDTOs.UserDetailByTokenDTOResponse;
import com.synoriq.synofin.collection.collectionservice.service.UtilityService;
import com.synoriq.synofin.collection.collectionservice.service.ActivityLogService;
import com.synoriq.synofin.collection.collectionservice.rest.request.CollectionActivityLogDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.tomcat.util.json.JSONParser;
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
    public BaseDTOResponse<Object> getActivityLogsById(Long activityLogsId) throws Exception {

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
            throw new Exception("1016025");
        }

    }
    @Override
    public BaseDTOResponse<Object> getActivityLogsByUserIdWithDuration(Integer page, Integer size, Long userId, Date fromDate, Date endDate) throws Exception {

        Date toDate = checkToDate(endDate);

        BaseDTOResponse<Object> response;

        Pageable pageable = PageRequest.of(page, size);
        List<CollectionActivityLogsEntity> collectionActivityLogs = collectionActivityLogsRepository.getActivityLogsUserWIseByDuration(userId, fromDate, toDate, pageable);
        if (page > 0) {
            if (collectionActivityLogs.size() == 0) {
                return new BaseDTOResponse<>(collectionActivityLogs);
            }
        }

        List<ActivityLogResponseDTO> activityLogResponsDTOS = new LinkedList<>();
        if (!collectionActivityLogs.isEmpty()) {
            for(CollectionActivityLogsEntity collectionActivityLogsEntity : collectionActivityLogs){

                ActivityLogResponseDTO activityLogResponseDTO = new ActivityLogResponseDTO();
                activityLogResponseDTO.setCollectionActivityLogsId(collectionActivityLogsEntity.getCollectionActivityLogsId());
                activityLogResponseDTO.setLoanId(collectionActivityLogsEntity.getLoanId());
                activityLogResponseDTO.setUserId(collectionActivityLogsEntity.getActivityBy());
                activityLogResponseDTO.setActivityDate(collectionActivityLogsEntity.getActivityDate());
                activityLogResponseDTO.setActivityName(collectionActivityLogsEntity.getActivityName());
                activityLogResponseDTO.setAddress(collectionActivityLogsEntity.getAddress());
                activityLogResponseDTO.setGeolocation(collectionActivityLogsEntity.getGeolocation());
                activityLogResponseDTO.setImages(collectionActivityLogsEntity.getImages());
                activityLogResponseDTO.setRemarks(collectionActivityLogsEntity.getRemarks());

                activityLogResponsDTOS.add(activityLogResponseDTO);
            }

            response = new BaseDTOResponse<>(activityLogResponsDTOS);
            return response;
        } else {
            return new BaseDTOResponse<>(activityLogResponsDTOS);
        }
    }
    @Override
    public ActivityLogBaseResponseDTO getActivityLogsByLoanIdWithDuration(Integer page, Integer size,Long loanId, Date fromDate, Date endDate, String filterBy) throws Exception {

        Date toDate = checkToDate(endDate);
        Pageable pageable = PageRequest.of(page,size);
        List<Map<String, Object>> collectionActivityLogs;
        ActivityLogBaseResponseDTO activityLogBaseResponseDTO = new ActivityLogBaseResponseDTO();
        ActivityLogDataDTO activityLogDataDTO = new ActivityLogDataDTO();
        List<ActivityLogCustomResponseDTO> responseData = new ArrayList<>();

        if (!filterBy.equals("")) {
            collectionActivityLogs = collectionActivityLogsRepository.getActivityLogsLoanWiseByDurationByFilter(loanId, fromDate, toDate, filterBy, pageable);
        } else {
            collectionActivityLogs = collectionActivityLogsRepository.getActivityLogsLoanWiseByDuration(loanId, fromDate, toDate, pageable);
        }
        if (collectionActivityLogs.size() > 0) {
            for (Map<String, Object> collectionActivityLog : collectionActivityLogs) {
                ActivityLogCustomResponseDTO activityLogCustomResponseDTO = new ActivityLogCustomResponseDTO();
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode addressNode = objectMapper.readTree(String.valueOf(collectionActivityLog.get("address")));
                JsonNode imagesNode = objectMapper.readTree(String.valueOf(collectionActivityLog.get("images")));
                JsonNode geoLocationDataNode = objectMapper.readTree(String.valueOf(collectionActivityLog.get("geo_location_data")));

                activityLogCustomResponseDTO.setCollectionActivityLogsId(Long.parseLong(String.valueOf(collectionActivityLog.get("collection_activity_logs_id"))));
                activityLogCustomResponseDTO.setActivityDate(String.valueOf(collectionActivityLog.get("activity_date")));
                activityLogCustomResponseDTO.setActivityBy(Long.parseLong(String.valueOf(collectionActivityLog.get("activity_by"))));
                activityLogCustomResponseDTO.setActivityName(String.valueOf(collectionActivityLog.get("activity_name")));
                activityLogCustomResponseDTO.setDistanceFromUserBranch(Double.parseDouble(String.valueOf(collectionActivityLog.get("distance_from_user_branch"))));
                activityLogCustomResponseDTO.setRemarks(String.valueOf(collectionActivityLog.get("remarks")));
                activityLogCustomResponseDTO.setLoanId(Long.parseLong(String.valueOf(collectionActivityLog.get("loan_id"))));
                activityLogCustomResponseDTO.setBatteryPercentage(Long.parseLong(String.valueOf(collectionActivityLog.get("battery_percentage"))));
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
    @Override
    public Long createActivityLogs(CollectionActivityLogDTO activityLogRequest, String token) throws Exception {

        if (activityLogRequest.getLoanId() == null) {
            log.error("Requested parameter loan id cannot be blank");
            throw new Exception("101809");
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
    }

    private Date checkToDate(Date toDate){
        return DateUtils.addDays(toDate,1);
    }
}
