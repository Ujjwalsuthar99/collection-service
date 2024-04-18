package com.synoriq.synofin.collection.collectionservice.implementation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.synoriq.synofin.collection.collectionservice.config.oauth.CurrentUserInfo;
import com.synoriq.synofin.collection.collectionservice.entity.CollectionActivityLogsEntity;
import com.synoriq.synofin.collection.collectionservice.entity.FollowUpEntity;
import com.synoriq.synofin.collection.collectionservice.repository.CollectionActivityLogsRepository;
import com.synoriq.synofin.collection.collectionservice.repository.FollowUpRepository;
import com.synoriq.synofin.collection.collectionservice.repository.ReceiptRepository;
import com.synoriq.synofin.collection.collectionservice.rest.request.followUpDTOs.FollowUpDtoRequest;
import com.synoriq.synofin.collection.collectionservice.rest.request.followUpDTOs.FollowUpStatusRequestDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.BaseDTOResponse;
import com.synoriq.synofin.collection.collectionservice.rest.response.FollowUpResponseDTO.FollowUpCustomDataResponseDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.FollowUpResponseDTO.FollowUpDataResponseDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.UtilsDTOs.FollowupResponseDTO;
import com.synoriq.synofin.collection.collectionservice.service.ActivityLogService;
import com.synoriq.synofin.collection.collectionservice.service.FollowUpService;
import com.synoriq.synofin.dataencryptionservice.service.RSAUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.synoriq.synofin.collection.collectionservice.common.ActivityRemarks.*;

@Service
@Slf4j
public class FollowUpServiceImpl implements FollowUpService {

    @Autowired
    private RSAUtils rsaUtils;

    @Autowired
    private CurrentUserInfo currentUserInfo;

    @Autowired
    FollowUpRepository followUpRepository;
    @Autowired
    ReceiptRepository receiptRepository;
    @Autowired
    CollectionActivityLogsRepository collectionActivityLogsRepository;
    @Autowired
    ActivityLogService activityLogService;

    @Autowired
    EntityManager entityManager;

    @Override
    public BaseDTOResponse<Object> getFollowupById(Long followupById) throws Exception {

        BaseDTOResponse<Object> response;
        FollowupResponseDTO followupResponseDTO = new FollowupResponseDTO();

        FollowUpEntity followUpEntity = followUpRepository.findByFollowupId(followupById);
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        if(followUpEntity != null){
//            FollowupResponseDTO.builder().followUpId(followUpEntity.getFollowupId()).followUpReason().otherFollowupReason().loanId().remarks().createdBy().
            followupResponseDTO.setFollowUpId(followUpEntity.getFollowupId());
            followupResponseDTO.setFollowUpReason(followUpEntity.getFollowUpReason());
            followupResponseDTO.setOtherFollowupReason(followUpEntity.getOtherFollowUpReason());
            followupResponseDTO.setLoanId(followUpEntity.getLoanId());
            followupResponseDTO.setRemarks(followUpEntity.getRemarks());
            followupResponseDTO.setCreatedBy(followUpEntity.getCreatedBy());
            followupResponseDTO.setCreatedDate(followUpEntity.getCreatedDate());
            followupResponseDTO.setNextFollowUpDateTime(formatter.format(followUpEntity.getNextFollowUpDateTime()));
            followupResponseDTO.setIsDeleted(followUpEntity.getIsDeleted());

            response = new BaseDTOResponse<>(followupResponseDTO);
            return response;

        }else{
            log.error("Followup for id {} not found", followupById);
            throw new Exception("1016025");
        }

    }
    @Override
    public Map<String, Object> getFollowupDetailsById(Long followupId) throws Exception {

        Map<String, Object> followupData;
        try {
            String encryptionKey = rsaUtils.getEncryptionKey(currentUserInfo.getClientId());
            String password = rsaUtils.getPassword(currentUserInfo.getClientId());
//            Boolean piiPermission = rsaUtils.getPiiPermission();
            Boolean piiPermission = true;
            followupData = followUpRepository.getFollowupDetailsById(followupId, encryptionKey, password, piiPermission);
        } catch (Exception e) {
            throw new Exception("1017002");
        }
        return followupData;
    }
    @Override
    public BaseDTOResponse<Object> getFollowupLoanWiseWithDuration(Integer page, Integer size, Long loanId, Date fromDate, Date toDate) throws Exception {

        if(fromDate.compareTo(toDate) == 0){
            toDate = checkToDate(toDate);
        }
        ObjectMapper objectMapper = new ObjectMapper();
        List<FollowUpCustomDataResponseDTO> followUpArr = new ArrayList<>();
        Pageable pageable = PageRequest.of(page,size);
        FollowUpDataResponseDTO followUpDataResponseDTO = new FollowUpDataResponseDTO();
        String encryptionKey = rsaUtils.getEncryptionKey(currentUserInfo.getClientId());
        String password = rsaUtils.getPassword(currentUserInfo.getClientId());
//            Boolean piiPermission = rsaUtils.getPiiPermission();
        Boolean piiPermission = true;

        List<Map<String,Object>> followUpEntityPages = followUpRepository.getFollowupsLoanWiseByDuration(loanId, fromDate, toDate, encryptionKey, password, piiPermission, pageable);
        if (followUpEntityPages.size() > 0) {
            for (Map<String, Object> followUpEntity : followUpEntityPages) {
                JsonNode geoLocationDataNode = objectMapper.readTree(String.valueOf(followUpEntity.get("geo_location_data")));
                JsonNode imagesNode = objectMapper.readTree(String.valueOf(followUpEntity.get("followup_images")));
                FollowUpCustomDataResponseDTO followUpCustomDataResponseDTO = new FollowUpCustomDataResponseDTO();
                followUpCustomDataResponseDTO.setFollowUpId(Long.parseLong(String.valueOf(followUpEntity.get("followup_id"))));
                followUpCustomDataResponseDTO.setCreatedDate(String.valueOf(followUpEntity.get("created_date")));
                followUpCustomDataResponseDTO.setCreatedBy(String.valueOf(followUpEntity.get("created_by")));
                followUpCustomDataResponseDTO.setFollowUpReason(String.valueOf(followUpEntity.get("followup_reason")));
                followUpCustomDataResponseDTO.setNextFollowupDate(String.valueOf(followUpEntity.get("next_followup_date")));
                followUpCustomDataResponseDTO.setRemarks(String.valueOf(followUpEntity.get("remarks")));
                followUpCustomDataResponseDTO.setGeoLocationData(new Gson().fromJson(String.valueOf(geoLocationDataNode), Object.class));
                Map<String, String> imagesObj = new HashMap<>();
                for (int i = 1; i <= imagesNode.size(); i++) {
                    String imageVal = String.valueOf(imagesNode.get("url" + i));
                    String updatedImageVal = "followUp/" + followUpCustomDataResponseDTO.getCreatedBy() + "/" + new Gson().fromJson(String.valueOf(imageVal), String.class);
                    imagesObj.put("url" + i, updatedImageVal);
                }
                followUpCustomDataResponseDTO.setFollowUpImages(imagesObj);
                followUpArr.add(followUpCustomDataResponseDTO);
            }
            followUpDataResponseDTO.setData(followUpArr);
            followUpDataResponseDTO.setTotalCount(Long.parseLong(String.valueOf(followUpEntityPages.get(0).get("total_rows"))));
        } else {
            log.error("Followup data not found for loan Id {}", loanId);
            followUpDataResponseDTO.setData(followUpArr);
            followUpDataResponseDTO.setTotalCount(0L);
            return new BaseDTOResponse<>(followUpDataResponseDTO);
        }
        return new BaseDTOResponse<>(followUpDataResponseDTO);
    }
    @Override
    public BaseDTOResponse<Object> getFollowupUserWiseWithDuration(Integer page, Integer size, Long userId, Date fromDate, Date toDate, String searchKey) throws Exception {

        List<Map<String,Object>> followUpEntityPages;
        toDate = checkToDate(toDate);

        BaseDTOResponse<Object> baseDTOResponse;
        Pageable pageable = PageRequest.of(page,size);
        String encryptionKey = rsaUtils.getEncryptionKey(currentUserInfo.getClientId());
        String password = rsaUtils.getPassword(currentUserInfo.getClientId());
//            Boolean piiPermission = rsaUtils.getPiiPermission();
        Boolean piiPermission = true;
        List<String> statusList = new ArrayList<>();
        if (searchKey.isEmpty()) {
            statusList.add("pending");
            statusList.add("reschedule");
            statusList.add("closed");
        } else {
            statusList.add(searchKey.toLowerCase());
        }

        followUpEntityPages = followUpRepository.getFollowupsUserWiseByDurationForCreated(userId, fromDate, toDate, statusList, encryptionKey, password, piiPermission, pageable);
        if (page > 0) {
            if (followUpEntityPages.isEmpty()) {
                return new BaseDTOResponse<>(followUpEntityPages);
            }
        }
        baseDTOResponse = new BaseDTOResponse<>(followUpEntityPages);

        return baseDTOResponse;

    }
    @Override
    public BaseDTOResponse<Object> createFollowup(FollowUpDtoRequest followUpDtoRequest, String token) throws Exception {

        BaseDTOResponse<Object> baseResponse;

        try {

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

            Long collectionActivityLogsId = activityLogService.
                    createActivityLogs(followUpDtoRequest.getActivityLog(), token);
            FollowUpEntity followUpEntity = new FollowUpEntity();
            followUpEntity.setLoanId(followUpDtoRequest.getLoanId());
            followUpEntity.setIsDeleted(false);
            followUpEntity.setCreatedDate(new Date());
            followUpEntity.setCreatedBy(followUpDtoRequest.getCreatedBy());
            followUpEntity.setFollowUpStatus(followUpDtoRequest.getStatus());

            followUpEntity.setFollowUpReason(followUpDtoRequest.getFollowUpReason());
            followUpEntity.setOtherFollowUpReason(followUpDtoRequest.getOtherFollowupReason());

            followUpEntity.setNextFollowUpDateTime(formatter.parse(followUpDtoRequest.getNextFollowUpDateTime()));
            followUpEntity.setRemarks(followUpDtoRequest.getRemarks());
            followUpEntity.setCollectionActivityLogsId(collectionActivityLogsId);

            followUpRepository.save(followUpEntity);

            CollectionActivityLogsEntity collectionActivityLogsEntity1 = collectionActivityLogsRepository.findByCollectionActivityLogsId(collectionActivityLogsId);
            String remarks = collectionActivityLogsEntity1.getRemarks();
            String updatedRemarks = Objects.equals(followUpDtoRequest.getStatus(), "pending") ? CREATE_FOLLOWUP : RESCHEDULE_FOLLOWUP;
            updatedRemarks = updatedRemarks.replace("{request_id}", followUpEntity.getFollowupId().toString());
            updatedRemarks = updatedRemarks.replace("{loan_number}", followUpDtoRequest.getLoanId().toString());
            updatedRemarks = updatedRemarks + remarks;
            collectionActivityLogsEntity1.setRemarks(updatedRemarks);
            collectionActivityLogsRepository.save(collectionActivityLogsEntity1);
            log.info("Followup Saved successfully");

            baseResponse = new BaseDTOResponse<>(followUpEntity);

        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
        return baseResponse;

    }


    @Override
    @Transactional(rollbackOn = RuntimeException.class)
    public BaseDTOResponse<Object> updateStatus(FollowUpStatusRequestDTO followUpStatusRequestDTO, String token) throws Exception {
        try {
            Optional<FollowUpEntity> followUpEntity = followUpRepository.findById(followUpStatusRequestDTO.getFollowUpId());
            if (followUpEntity.isPresent()) {
                Map<String, Object> receiptExist = receiptRepository.getServiceRequestDataById(followUpStatusRequestDTO.getServiceRequestId(), followUpStatusRequestDTO.getLoanId());
                if (!receiptExist.isEmpty()) {
                    Optional<List<FollowUpEntity>> var = Optional.ofNullable(followUpRepository.findDataByServiceRequestId(followUpStatusRequestDTO.getServiceRequestId()));
                    if (var.isPresent()) {
                        throw new Exception("1016051");
                    }
                    // receipt created date
                    Date date = (Date) receiptExist.get("created_date");
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(date);
                    int createdDateMonth = cal.get(Calendar.MONTH);

                    // current date
                    Date toDay = new Date();
                    Calendar currentCalendar = Calendar.getInstance();
                    currentCalendar.setTime(toDay);
                    int currentMonth = currentCalendar.get(Calendar.MONTH);

                    if (currentMonth != createdDateMonth) {
                        throw new Exception("1016048");
                    }

                    followUpEntity.get().setClosingRemarks(followUpStatusRequestDTO.getRemarks());
                    followUpEntity.get().setServiceRequestId(followUpStatusRequestDTO.getServiceRequestId());
                    followUpEntity.get().setFollowUpStatus(followUpStatusRequestDTO.getStatus());

                    followUpRepository.save(followUpEntity.get());
                    String updatedRemarks = CLOSE_FOLLOWUP;
                    updatedRemarks = updatedRemarks.replace("{request_id}", followUpEntity.get().getFollowupId().toString());
                    updatedRemarks = updatedRemarks.replace("{loan_number}", followUpEntity.get().getLoanId().toString());
                    followUpStatusRequestDTO.getActivityLog().setRemarks(updatedRemarks);

                    // creating activity logs
                    activityLogService.createActivityLogs(followUpStatusRequestDTO.getActivityLog(), token);
                } else {
                    throw new Exception("1016049");
                }
            } else {
                throw new Exception("1016025");
            }

        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }

        return new BaseDTOResponse<>("Updated Successfully");
    }

    private Date checkToDate(Date toDate) {

        return DateUtils.addDays(toDate,1);

    }

}
