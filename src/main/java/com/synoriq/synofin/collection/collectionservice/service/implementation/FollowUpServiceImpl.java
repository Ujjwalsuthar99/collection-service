package com.synoriq.synofin.collection.collectionservice.service.implementation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.synoriq.synofin.collection.collectionservice.common.errorcode.ErrorCode;
import com.synoriq.synofin.collection.collectionservice.common.exception.CollectionException;
import com.synoriq.synofin.collection.collectionservice.common.exception.CustomException;
import com.synoriq.synofin.collection.collectionservice.config.oauth.CurrentUserInfo;
import com.synoriq.synofin.collection.collectionservice.entity.CollectionActivityLogsEntity;
import com.synoriq.synofin.collection.collectionservice.entity.FollowUpEntity;
import com.synoriq.synofin.collection.collectionservice.repository.CollectionActivityLogsRepository;
import com.synoriq.synofin.collection.collectionservice.repository.CollectionConfigurationsRepository;
import com.synoriq.synofin.collection.collectionservice.repository.FollowUpRepository;
import com.synoriq.synofin.collection.collectionservice.repository.ReceiptRepository;
import com.synoriq.synofin.collection.collectionservice.rest.request.followupdtos.FollowUpDtoRequest;
import com.synoriq.synofin.collection.collectionservice.rest.request.followupdtos.FollowUpStatusRequestDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.BaseDTOResponse;
import com.synoriq.synofin.collection.collectionservice.rest.response.followupresponsedto.FollowUpCustomDataResponseDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.followupresponsedto.FollowUpDataResponseDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.utilsdtos.FollowupResponseDTO;
import com.synoriq.synofin.collection.collectionservice.service.ActivityLogService;
import com.synoriq.synofin.collection.collectionservice.service.FollowUpService;
import com.synoriq.synofin.dataencryptionservice.service.RSAUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.synoriq.synofin.collection.collectionservice.common.ActivityRemarks.*;

@Service
@Slf4j
public class FollowUpServiceImpl implements FollowUpService {

    private static final String PENDING_STATUS = "pending";

    private static final String RESCH_STATUS = "reschedule";

    private final RSAUtils rsaUtils;
    private final CurrentUserInfo currentUserInfo;
    private final FollowUpRepository followUpRepository;
    private final ReceiptRepository receiptRepository;
    private final CollectionActivityLogsRepository collectionActivityLogsRepository;
    private final ActivityLogService activityLogService;
    private final CollectionConfigurationsRepository collectionConfigurationsRepository;

    public FollowUpServiceImpl(RSAUtils rsaUtils,
            CurrentUserInfo currentUserInfo,
            FollowUpRepository followUpRepository,
            ReceiptRepository receiptRepository,
            CollectionActivityLogsRepository collectionActivityLogsRepository,
            ActivityLogService activityLogService,
            CollectionConfigurationsRepository collectionConfigurationsRepository) {
        this.rsaUtils = rsaUtils;
        this.currentUserInfo = currentUserInfo;
        this.followUpRepository = followUpRepository;
        this.receiptRepository = receiptRepository;
        this.collectionActivityLogsRepository = collectionActivityLogsRepository;
        this.activityLogService = activityLogService;
        this.collectionConfigurationsRepository = collectionConfigurationsRepository;
    }

    @Override
    public BaseDTOResponse<Object> getFollowupById(Long followupById) throws CollectionException {

        BaseDTOResponse<Object> response;
        FollowupResponseDTO followupResponseDTO = new FollowupResponseDTO();
        FollowUpEntity followUpEntity = followUpRepository.findByFollowupId(followupById);
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        if (followUpEntity != null) {
            // FollowupResponseDTO.builder().followUpId(followUpEntity.getFollowupId()).followUpReason().otherFollowupReason().loanId().remarks().createdBy().
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

        } else {
            log.error("Followup for id {} not found", followupById);
            ErrorCode errCode = ErrorCode.getErrorCode(1016025);
            throw new CollectionException(errCode, 1016025);
        }

    }

    @Override
    public Map<String, Object> getFollowupDetailsById(Long followupId) throws CollectionException {

        Map<String, Object> followupData;
        try {
            String encryptionKey = rsaUtils.getEncryptionKey(currentUserInfo.getClientId());
            String password = rsaUtils.getPassword(currentUserInfo.getClientId());
            Boolean piiPermission = true;
            followupData = followUpRepository.getFollowupDetailsById(followupId, encryptionKey, password,
                    piiPermission);
        } catch (Exception e) {
            ErrorCode errCode = ErrorCode.getErrorCode(1017002);
            throw new CollectionException(errCode, 1017002);
        }
        return followupData;
    }

    @Override
    public BaseDTOResponse<Object> getFollowupLoanWiseWithDuration(Integer page, Integer size, Long loanId,
            Date fromDate, Date toDate) throws CustomException {

        ObjectMapper objectMapper = new ObjectMapper();
        List<FollowUpCustomDataResponseDTO> followUpArr = new ArrayList<>();
        Pageable pageable = PageRequest.of(page, size);
        FollowUpDataResponseDTO followUpDataResponseDTO = new FollowUpDataResponseDTO();
        try {
            String encryptionKey = rsaUtils.getEncryptionKey(currentUserInfo.getClientId());
            String password = rsaUtils.getPassword(currentUserInfo.getClientId());
            Boolean piiPermission = true;

            List<Map<String, Object>> followUpEntityPages = followUpRepository.getFollowupsLoanWiseByDuration(loanId,
                    fromDate, toDate, encryptionKey, password, piiPermission, pageable);
            if (!followUpEntityPages.isEmpty()) {
                for (Map<String, Object> followUpEntity : followUpEntityPages) {
                    JsonNode geoLocationDataNode = objectMapper
                            .readTree(String.valueOf(followUpEntity.get("geo_location_data")));
                    JsonNode imagesNode = objectMapper.readTree(String.valueOf(followUpEntity.get("followup_images")));
                    FollowUpCustomDataResponseDTO followUpCustomDataResponseDTO = new FollowUpCustomDataResponseDTO();
                    followUpCustomDataResponseDTO
                            .setFollowUpId(Long.parseLong(String.valueOf(followUpEntity.get("followup_id"))));
                    followUpCustomDataResponseDTO.setCreatedDate(String.valueOf(followUpEntity.get("created_date")));
                    followUpCustomDataResponseDTO.setCreatedBy(String.valueOf(followUpEntity.get("created_by")));
                    followUpCustomDataResponseDTO
                            .setFollowUpReason(String.valueOf(followUpEntity.get("followup_reason")));
                    followUpCustomDataResponseDTO
                            .setNextFollowupDate(String.valueOf(followUpEntity.get("next_followup_date")));
                    followUpCustomDataResponseDTO.setRemarks(String.valueOf(followUpEntity.get("remarks")));
                    followUpCustomDataResponseDTO
                            .setGeoLocationData(new Gson().fromJson(String.valueOf(geoLocationDataNode), Object.class));
                    Map<String, String> imagesObj = new HashMap<>();
                    for (int i = 1; i <= imagesNode.size(); i++) {
                        String imageVal = String.valueOf(imagesNode.get("url" + i));
                        String updatedImageVal = "followUp/" + followUpCustomDataResponseDTO.getCreatedBy() + "/"
                                + new Gson().fromJson(String.valueOf(imageVal), String.class);
                        imagesObj.put("url" + i, updatedImageVal);
                    }
                    followUpCustomDataResponseDTO.setFollowUpImages(imagesObj);
                    followUpArr.add(followUpCustomDataResponseDTO);
                }
                followUpDataResponseDTO.setData(followUpArr);
                followUpDataResponseDTO
                        .setTotalCount(Long.parseLong(String.valueOf(followUpEntityPages.get(0).get("total_rows"))));
            } else {
                log.error("Followup data not found for loan Id {}", loanId);
                followUpDataResponseDTO.setData(followUpArr);
                followUpDataResponseDTO.setTotalCount(0L);
                return new BaseDTOResponse<>(followUpDataResponseDTO);
            }
            return new BaseDTOResponse<>(followUpDataResponseDTO);
        } catch (Exception ee) {
            throw new CustomException(ee.getMessage());
        }
    }

    @Override
    public BaseDTOResponse<Object> getFollowupUserWiseWithDuration(Integer page, Integer size, Long userId,
            Date fromDate, Date toDate, String searchKey) throws CustomException {

        List<Map<String, Object>> followUpEntityPages;
        try {
            toDate = checkToDate(toDate);

            BaseDTOResponse<Object> baseDTOResponse;
            Pageable pageable = PageRequest.of(page, size);
            String encryptionKey = rsaUtils.getEncryptionKey(currentUserInfo.getClientId());
            String password = rsaUtils.getPassword(currentUserInfo.getClientId());
            Boolean piiPermission = true;
            List<String> statusList = new ArrayList<>();
            if (searchKey.isEmpty()) {
                statusList.add(PENDING_STATUS);
                statusList.add(RESCH_STATUS);
                statusList.add("closed");
            } else {
                statusList.add(searchKey.toLowerCase().contains(RESCH_STATUS) ? RESCH_STATUS : searchKey.toLowerCase());
            }

            followUpEntityPages = followUpRepository.getFollowupsUserWiseByDurationForCreated(userId, fromDate, toDate,
                    statusList, encryptionKey, password, piiPermission, pageable);
            if (page > 0 && followUpEntityPages.isEmpty()) {
                return new BaseDTOResponse<>(followUpEntityPages);
            }
            baseDTOResponse = new BaseDTOResponse<>(followUpEntityPages);

            return baseDTOResponse;
        } catch (Exception ee) {
            throw new CustomException(ee.getMessage());
        }

    }

    @Override
    public BaseDTOResponse<Object> createFollowup(FollowUpDtoRequest followUpDtoRequest, String token)
            throws CustomException {

        BaseDTOResponse<Object> baseResponse;

        try {

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

            Long collectionActivityLogsId = activityLogService.createActivityLogs(followUpDtoRequest.getActivityLog(),
                    token);

            if (Objects.equals(collectionConfigurationsRepository
                    .findConfigurationValueByConfigurationName("show_close_reschedule_followup_button"), "true")) {
                if (Boolean.TRUE.equals(followUpDtoRequest.getIsReschedule()))
                    followUpRepository.updateStatus(followUpDtoRequest.getToBeRescheduledId());
                List<FollowUpEntity> followUpEntities = followUpRepository.findByLoanIdAndCreatedByAndFollowUpStatus(
                        followUpDtoRequest.getLoanId(), followUpDtoRequest.getCreatedBy(), PENDING_STATUS);
                if (!followUpEntities.isEmpty()) {
                    ErrorCode errCode = ErrorCode.getErrorCode(1016054);
                    throw new CollectionException(errCode, 1016054);
                }
            }

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

            CollectionActivityLogsEntity collectionActivityLogsEntity1 = collectionActivityLogsRepository
                    .findByCollectionActivityLogsId(collectionActivityLogsId);
            String remarks = collectionActivityLogsEntity1.getRemarks();
            String updatedRemarks = Objects.equals(followUpDtoRequest.getStatus(), PENDING_STATUS) ? CREATE_FOLLOWUP
                    : RESCHEDULE_FOLLOWUP;
            updatedRemarks = updatedRemarks.replace("{request_id}", followUpEntity.getFollowupId().toString());
            updatedRemarks = updatedRemarks.replace("{loan_number}", followUpDtoRequest.getLoanId().toString());
            updatedRemarks = updatedRemarks + remarks;
            collectionActivityLogsEntity1.setRemarks(updatedRemarks);
            collectionActivityLogsRepository.save(collectionActivityLogsEntity1);
            log.info("Followup Saved successfully");

            baseResponse = new BaseDTOResponse<>(followUpEntity);

        } catch (Exception e) {
            throw new CustomException(e.getMessage());
        }
        return baseResponse;

    }

    @Override
    @Transactional(rollbackOn = RuntimeException.class)
    public BaseDTOResponse<Object> updateStatus(FollowUpStatusRequestDTO followUpStatusRequestDTO, String token)
            throws CollectionException {
        try {
            Optional<FollowUpEntity> followUpEntity = followUpRepository
                    .findById(followUpStatusRequestDTO.getFollowUpId());
            if (followUpEntity.isPresent()) {
                if (followUpStatusRequestDTO.getStatus().equals(RESCH_STATUS)) {
                    updateStatus(followUpEntity.get(), followUpStatusRequestDTO, token);
                    return new BaseDTOResponse<>("Updated Successfully");
                }
                Map<String, Object> receiptExist = receiptRepository.getServiceRequestDataById(
                        followUpStatusRequestDTO.getServiceRequestId(), followUpStatusRequestDTO.getLoanId());
                if (!receiptExist.isEmpty()) {
                    List<FollowUpEntity> followUpData = followUpRepository
                            .findDataByServiceRequestId(followUpStatusRequestDTO.getServiceRequestId());
                    if (!followUpData.isEmpty()) {
                        ErrorCode errCode = ErrorCode.getErrorCode(1016051);
                        throw new CollectionException(errCode, 1016051);
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
                        ErrorCode errCode = ErrorCode.getErrorCode(1016048);
                        throw new CollectionException(errCode, 1016048);
                    }
                    // updating the status and creating activity log
                    updateStatus(followUpEntity.get(), followUpStatusRequestDTO, token);
                } else {
                    ErrorCode errCode = ErrorCode.getErrorCode(1016049);
                    throw new CollectionException(errCode, 1016049);
                }
            } else {
                ErrorCode errCode = ErrorCode.getErrorCode(1016025);
                throw new CollectionException(errCode, 1016025);
            }

        } catch (Exception e) {
            throw new CustomException(e.getMessage());
        }

        return new BaseDTOResponse<>("Updated Successfully");
    }

    private Date checkToDate(Date toDate) {

        return DateUtils.addDays(toDate, 1);

    }

    private void updateStatus(FollowUpEntity followUpEntity, FollowUpStatusRequestDTO followUpStatusRequestDTO,
            String token) throws CollectionException {

        followUpEntity.setClosingRemarks(followUpStatusRequestDTO.getRemarks());
        followUpEntity.setServiceRequestId(followUpStatusRequestDTO.getServiceRequestId());
        followUpEntity.setFollowUpStatus(followUpStatusRequestDTO.getStatus());

        followUpRepository.save(followUpEntity);
        String updatedRemarks = CLOSE_FOLLOWUP;
        updatedRemarks = updatedRemarks.replace("{request_id}", followUpEntity.getFollowupId().toString());
        updatedRemarks = updatedRemarks.replace("{loan_number}", followUpEntity.getLoanId().toString());
        followUpStatusRequestDTO.getActivityLog().setRemarks(updatedRemarks);

        // creating activity logs
        activityLogService.createActivityLogs(followUpStatusRequestDTO.getActivityLog(), token);
    }

}
