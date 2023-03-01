package com.synoriq.synofin.collection.collectionservice.service;

import com.synoriq.synofin.collection.collectionservice.common.errorcode.ErrorCode;
import com.synoriq.synofin.collection.collectionservice.entity.FollowUpEntity;
import com.synoriq.synofin.collection.collectionservice.repository.FollowUpRepository;
import com.synoriq.synofin.collection.collectionservice.rest.request.FollowUpDtoRequest;
import com.synoriq.synofin.collection.collectionservice.rest.response.BaseDTOResponse;
import com.synoriq.synofin.collection.collectionservice.rest.response.FollowupResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class FollowUpService {

    @Autowired
    FollowUpRepository followUpRepository;

    @Autowired
    ActivityLogService activityLogService;

    public BaseDTOResponse<Object> getFollowupById(Long followupById) throws Exception {

        BaseDTOResponse<Object> response;
        FollowupResponse followupResponse = new FollowupResponse();

        FollowUpEntity followUpEntity = followUpRepository.findByFollowupId(followupById);

        if(followUpEntity != null){

            followupResponse.setFollowUpId(followUpEntity.getFollowupId());
            followupResponse.setFollowUpReason(followUpEntity.getFollowUpReason());
            followupResponse.setOtherFollowupReason(followUpEntity.getOtherFollowUpReason());
            followupResponse.setLoanId(followUpEntity.getLoanId());
            followupResponse.setRemarks(followUpEntity.getRemarks());
            followupResponse.setCreatedBy(followUpEntity.getCreatedBy());
            followupResponse.setCreatedDate(followUpEntity.getCreatedDate());
            followupResponse.setNextFollowUpDateTime(followUpEntity.getNextFollowUpDateTime());
            followupResponse.setIsDeleted(followUpEntity.getIsDeleted());

            response = new BaseDTOResponse<Object>(followupResponse);
            return response;

        }else{
            log.error("Followup for id {} not found", followupById);
            throw new Exception("1016025");
        }

    }
    public Map<String, Object> getFollowupDetailsById(Long followupId) throws Exception {

        Map<String, Object> followupData;
        try {
            followupData = followUpRepository.getFollowupDetailsById(followupId);
        } catch (Exception e) {
            throw new Exception("1017002");
        }
        return followupData;
    }



    public BaseDTOResponse<Object> getFollowupLoanWiseWithDuration(Integer page, Integer size, Long loanId, Date fromDate, Date toDate) throws Exception {

        if(fromDate.compareTo(toDate) == 0){
            toDate = checkToDate(toDate);
        }

        BaseDTOResponse<Object> baseDTOResponse;
        Pageable pageable = PageRequest.of(page,size);

        List<Map<String,Object>> followUpEntityPages =
                followUpRepository.getFollowupsLoanWiseByDuration(loanId, fromDate,toDate, pageable);

//        List<FollowUpEntity> followUpEntities;

        if(!followUpEntityPages.isEmpty()){
            baseDTOResponse = new BaseDTOResponse<>(followUpEntityPages);
        }else{
            log.error("Followup data not found for loan Id {}", loanId);
            throw new Exception("1016025");
        }
//
//        List<FollowupResponse> followupResponseList = new LinkedList<>();

//        for(FollowUpEntity followUpEntity : followUpEntities){
//
//            FollowupResponse followupResponse = new FollowupResponse();
//            followupResponse.setLoanId(followUpEntity.getLoanId());
//            followupResponse.setFollowUpId(followUpEntity.getFollowupId());
//            followupResponse.setRemarks(followUpEntity.getRemarks());
//            followupResponse.setOtherFollowupReason(followupResponse.getOtherFollowupReason());
//            followupResponse.setNextFollowUpDateTime(followUpEntity.getNextFollowUpDateTime());
//            followupResponse.setCreatedBy(followUpEntity.getCreatedBy());
//            followupResponse.setCreatedDate(followUpEntity.getCreatedDate());
//            followupResponse.setIsDeleted(followUpEntity.getIsDeleted());
//            followupResponse.setFollowUpReason(followUpEntity.getFollowUpReason());
//
//            followupResponseList.add(followupResponse);
//
//        }

//        baseDTOResponse = new BaseDTOResponse<>(followUpEntityPages);

        return baseDTOResponse;

    }

    public BaseDTOResponse<Object> createFollowup(FollowUpDtoRequest followUpDtoRequest){

        BaseDTOResponse<Object> baseResponse;

        try {

            Long collectionActivityLogsId = activityLogService.
                    createActivityLogs(followUpDtoRequest.getActivityLog());

            FollowUpEntity followUpEntity = new FollowUpEntity();
            followUpEntity.setLoanId(followUpDtoRequest.getLoanId());
            followUpEntity.setIsDeleted(false);
            followUpEntity.setCreatedDate(new Date());
            followUpEntity.setCreatedBy(followUpDtoRequest.getCreatedBy());

            followUpEntity.setFollowUpReason(followUpDtoRequest.getFollowUpReason());
            followUpEntity.setOtherFollowUpReason(followUpDtoRequest.getOtherFollowupReason());

            followUpEntity.setNextFollowUpDateTime(followUpDtoRequest.getNextFollowUpDateTime());
            followUpEntity.setRemarks(followUpDtoRequest.getRemarks());
            followUpEntity.setCollectionActivityLogsId(collectionActivityLogsId);

            followUpRepository.save(followUpEntity);

            log.info("Followup Saved successfully");

            baseResponse = new BaseDTOResponse<Object>(followUpEntity);

        }catch (Exception e) {
            log.error("RestControllers error occurred for vanWebHookDetails: {} ", e.getMessage());
            if (ErrorCode.getErrorCode(Integer.valueOf(e.getMessage().trim())) == null) {
                baseResponse = new BaseDTOResponse<Object>(ErrorCode.DATA_FETCH_ERROR);
            } else {
                baseResponse = new BaseDTOResponse<Object>(ErrorCode.getErrorCode(Integer.valueOf(e.getMessage().trim())));
            }
        }
        return baseResponse;

    }



    private Date checkToDate(Date toDate){

        return DateUtils.addDays(toDate,1);

    }

}
