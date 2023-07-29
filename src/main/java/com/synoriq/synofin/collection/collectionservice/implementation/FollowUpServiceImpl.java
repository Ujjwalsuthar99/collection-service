package com.synoriq.synofin.collection.collectionservice.implementation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.synoriq.synofin.collection.collectionservice.entity.CollectionActivityLogsEntity;
import com.synoriq.synofin.collection.collectionservice.entity.FollowUpEntity;
import com.synoriq.synofin.collection.collectionservice.repository.CollectionActivityLogsRepository;
import com.synoriq.synofin.collection.collectionservice.repository.FollowUpRepository;
import com.synoriq.synofin.collection.collectionservice.rest.request.FollowUpDtoRequest;
import com.synoriq.synofin.collection.collectionservice.rest.response.BaseDTOResponse;
import com.synoriq.synofin.collection.collectionservice.rest.response.FollowUpResponseDTO.FollowUpCustomDataResponseDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.FollowUpResponseDTO.FollowUpDataResponseDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.ReceiptTransferDTOs.ReceiptTransferCustomDataResponseDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.UtilsDTOs.FollowupResponseDTO;
import com.synoriq.synofin.collection.collectionservice.service.ActivityLogService;
import com.synoriq.synofin.collection.collectionservice.service.FollowUpService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.synoriq.synofin.collection.collectionservice.common.ActivityRemarks.CREATE_FOLLOWUP;

@Service
@Slf4j
public class FollowUpServiceImpl implements FollowUpService {

    @Autowired
    FollowUpRepository followUpRepository;
    @Autowired
    CollectionActivityLogsRepository collectionActivityLogsRepository;
    @Autowired
    ActivityLogService activityLogService;

    @Override
    public BaseDTOResponse<Object> getFollowupById(Long followupById) throws Exception {

        BaseDTOResponse<Object> response;
        FollowupResponseDTO followupResponseDTO = new FollowupResponseDTO();

        FollowUpEntity followUpEntity = followUpRepository.findByFollowupId(followupById);
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        if(followUpEntity != null){

            followupResponseDTO.setFollowUpId(followUpEntity.getFollowupId());
            followupResponseDTO.setFollowUpReason(followUpEntity.getFollowUpReason());
            followupResponseDTO.setOtherFollowupReason(followUpEntity.getOtherFollowUpReason());
            followupResponseDTO.setLoanId(followUpEntity.getLoanId());
            followupResponseDTO.setRemarks(followUpEntity.getRemarks());
            followupResponseDTO.setCreatedBy(followUpEntity.getCreatedBy());
            followupResponseDTO.setCreatedDate(followUpEntity.getCreatedDate());
            followupResponseDTO.setNextFollowUpDateTime(formatter.format(followUpEntity.getNextFollowUpDateTime()));
            followupResponseDTO.setIsDeleted(followUpEntity.getIsDeleted());

            response = new BaseDTOResponse<Object>(followupResponseDTO);
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
            followupData = followUpRepository.getFollowupDetailsById(followupId);
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

        List<Map<String,Object>> followUpEntityPages = followUpRepository.getFollowupsLoanWiseByDuration(loanId, fromDate,toDate, pageable);
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
                followUpCustomDataResponseDTO.setFollowUpImages(new Gson().fromJson(String.valueOf(imagesNode), Object.class));
                followUpCustomDataResponseDTO.setGeoLocationData(new Gson().fromJson(String.valueOf(geoLocationDataNode), Object.class));
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
    public BaseDTOResponse<Object> getFollowupUserWiseWithDuration(Integer page, Integer size, Long userId, Date fromDate, Date toDate, String type) throws Exception {

        List<Map<String,Object>> followUpEntityPages;
        toDate = checkToDate(toDate);

        BaseDTOResponse<Object> baseDTOResponse;
        Pageable pageable = PageRequest.of(page,size);

        if (type.equals("pending")) {
            followUpEntityPages = followUpRepository.getFollowupsUserWiseByDurationForPending(userId, fromDate, toDate, pageable);
        } else {
            followUpEntityPages = followUpRepository.getFollowupsUserWiseByDurationForCreated(userId, fromDate, toDate, pageable);
        }
        if (page > 0) {
            if (followUpEntityPages.size() == 0) {
                return new BaseDTOResponse<>(followUpEntityPages);
            }
        }
        if (!followUpEntityPages.isEmpty()) {
            baseDTOResponse = new BaseDTOResponse<>(followUpEntityPages);
        } else {
            log.error("Followup data not found for loan Id {}", userId);
            throw new Exception("1016025");
        }

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

            followUpEntity.setFollowUpReason(followUpDtoRequest.getFollowUpReason());
            followUpEntity.setOtherFollowUpReason(followUpDtoRequest.getOtherFollowupReason());

            followUpEntity.setNextFollowUpDateTime(formatter.parse(followUpDtoRequest.getNextFollowUpDateTime()));
            followUpEntity.setRemarks(followUpDtoRequest.getRemarks());
            followUpEntity.setCollectionActivityLogsId(collectionActivityLogsId);

            followUpRepository.save(followUpEntity);

            CollectionActivityLogsEntity collectionActivityLogsEntity1 = collectionActivityLogsRepository.findByCollectionActivityLogsId(collectionActivityLogsId);
            String remarks = collectionActivityLogsEntity1.getRemarks();
            String updatedRemarks = CREATE_FOLLOWUP;
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

    private Date checkToDate(Date toDate) {

        return DateUtils.addDays(toDate,1);

    }

}
