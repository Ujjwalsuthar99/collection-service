package com.synoriq.synofin.collection.collectionservice.service;

import com.synoriq.synofin.collection.collectionservice.entity.CollectionActivityLogsEntity;
import com.synoriq.synofin.collection.collectionservice.repository.CollectionActivityLogsRepository;
import com.synoriq.synofin.collection.collectionservice.rest.response.ActivityLogResponse;
import com.synoriq.synofin.collection.collectionservice.rest.response.BaseDTOResponse;
import com.synoriq.synofin.lms.commondto.dto.collection.CollectionActivityLogDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Slf4j
@Service
public class ActivityLogService {

    @Autowired
    CollectionActivityLogsRepository collectionActivityLogsRepository;


    public BaseDTOResponse<Object> getActivityLogsById(Long activityLogsId) throws Exception {

        BaseDTOResponse<Object> response;

        CollectionActivityLogsEntity collectionActivityLogsEntity =
                collectionActivityLogsRepository.findByCollectionActivityLogsId(activityLogsId);

        if(collectionActivityLogsEntity != null){

            log.info("Getting activity log for id {} ", activityLogsId);

            ActivityLogResponse activityLogResponse = new ActivityLogResponse();
            activityLogResponse.setCollectionActivityLogsId(collectionActivityLogsEntity.getCollectionActivityLogsId());
            activityLogResponse.setLoanId(collectionActivityLogsEntity.getLoanId());
            activityLogResponse.setUserId(collectionActivityLogsEntity.getActivityBy());
            activityLogResponse.setActivityDate(collectionActivityLogsEntity.getActivityDate());
            activityLogResponse.setActivityName(collectionActivityLogsEntity.getActivityName());
            activityLogResponse.setAddress(collectionActivityLogsEntity.getAddress());
            activityLogResponse.setGeolocation(collectionActivityLogsEntity.getGeolocation());
            activityLogResponse.setImages(collectionActivityLogsEntity.getImages());

            response = new BaseDTOResponse<>(activityLogResponse);
            return response;


        }else{
            log.error("Activity log for id {} not found", activityLogsId);
            throw new Exception("1016025");
        }

    }

    public BaseDTOResponse<Object> getActivityLogsByUserIdWithDuration(Integer page, Integer size,Long userId, Date fromDate, Date toDate) throws Exception {

        if(fromDate.compareTo(toDate) == 0){
            toDate = checkToDate(toDate);
        }

        BaseDTOResponse<Object> response;

        Pageable pageable = PageRequest.of(page, size);

        List<CollectionActivityLogsEntity> collectionActivityLogs =
                collectionActivityLogsRepository.getActivityLogsUserWIseByDuration(userId, fromDate, toDate, pageable);
        if (page > 0) {
            if (collectionActivityLogs.size() == 0) {
                return new BaseDTOResponse<>(collectionActivityLogs);
            }
        }

        List<ActivityLogResponse> activityLogResponses = new LinkedList<>();

        if(!(collectionActivityLogs.isEmpty())){

            log.info("Getting activity log for userId {} ", userId);

            for(CollectionActivityLogsEntity collectionActivityLogsEntity : collectionActivityLogs){

                ActivityLogResponse activityLogResponse = new ActivityLogResponse();
                activityLogResponse.setCollectionActivityLogsId(collectionActivityLogsEntity.getCollectionActivityLogsId());
                activityLogResponse.setLoanId(collectionActivityLogsEntity.getLoanId());
                activityLogResponse.setUserId(collectionActivityLogsEntity.getActivityBy());
                activityLogResponse.setActivityDate(collectionActivityLogsEntity.getActivityDate());
                activityLogResponse.setActivityName(collectionActivityLogsEntity.getActivityName());
                activityLogResponse.setAddress(collectionActivityLogsEntity.getAddress());
                activityLogResponse.setGeolocation(collectionActivityLogsEntity.getGeolocation());
                activityLogResponse.setImages(collectionActivityLogsEntity.getImages());

                activityLogResponses.add(activityLogResponse);
            }

            response = new BaseDTOResponse<>(activityLogResponses);

            return response;

        }else{
            log.error("Activity log user id {} not found", userId);
            throw new Exception("1016025");
        }
    }

    public BaseDTOResponse<Object> getActivityLogsByLoanIdWithDuration(Integer page, Integer size,Long loanId, Date fromDate, Date toDate) throws Exception {

        if(fromDate.compareTo(toDate) == 0){
            toDate = checkToDate(toDate);
        }

        BaseDTOResponse<Object> response;

        Pageable pageable = PageRequest.of(page,size);

        List<CollectionActivityLogsEntity> collectionActivityLogs = collectionActivityLogsRepository.getActivityLogsLoanWiseByDuration(loanId, fromDate, toDate, pageable);
        if (page > 0) {
            if (collectionActivityLogs.size() == 0) {
                return new BaseDTOResponse<>(collectionActivityLogs);
            }
        }

        List<ActivityLogResponse> activityLogResponses = new LinkedList<>();

        if(!collectionActivityLogs.isEmpty()){

            log.info("Getting activity log for loanId {} ", loanId);

            for(CollectionActivityLogsEntity collectionActivityLogsEntity : collectionActivityLogs){

                ActivityLogResponse activityLogResponse = new ActivityLogResponse();
                activityLogResponse.setCollectionActivityLogsId(collectionActivityLogsEntity.getCollectionActivityLogsId());
                activityLogResponse.setLoanId(collectionActivityLogsEntity.getLoanId());
                activityLogResponse.setUserId(collectionActivityLogsEntity.getActivityBy());
                activityLogResponse.setActivityDate(collectionActivityLogsEntity.getActivityDate());
                activityLogResponse.setActivityName(collectionActivityLogsEntity.getActivityName());
                activityLogResponse.setAddress(collectionActivityLogsEntity.getAddress());
                activityLogResponse.setRemarks(collectionActivityLogsEntity.getRemarks());
                activityLogResponse.setDistanceFromUserBranch(collectionActivityLogsEntity.getDistanceFromUserBranch());
                activityLogResponse.setGeolocation(collectionActivityLogsEntity.getGeolocation());
                activityLogResponse.setImages(collectionActivityLogsEntity.getImages());
                activityLogResponse.setIsReceipt(collectionActivityLogsEntity.getActivityName().equals("Create Receipt"));

                activityLogResponses.add(activityLogResponse);
            }

            response = new BaseDTOResponse<>(activityLogResponses);
            return response;

        } else {
            log.error("Activity log loan id {} not found", loanId);
            throw new Exception("1016025");
        }
    }


    public Long createActivityLogs(CollectionActivityLogDTO activityLogRequest) throws Exception {


        if(activityLogRequest.getLoanId() == null){
            log.error("Requested parameter loan id cannot be blank");
            throw new Exception("101809");
        }
        CollectionActivityLogsEntity collectionActivityLogsEntity = new CollectionActivityLogsEntity();

            collectionActivityLogsEntity.setActivityBy(activityLogRequest.getUserId());
            collectionActivityLogsEntity.setActivityDate(new Date());
            collectionActivityLogsEntity.setActivityName(activityLogRequest.getActivityName());
            collectionActivityLogsEntity.setDeleted(activityLogRequest.getDeleted());
            collectionActivityLogsEntity.setLoanId(activityLogRequest.getLoanId());
            collectionActivityLogsEntity.setDistanceFromUserBranch(activityLogRequest.getDistanceFromUserBranch());
            collectionActivityLogsEntity.setRemarks(activityLogRequest.getRemarks());
            collectionActivityLogsEntity.setAddress(activityLogRequest.getAddress());
            collectionActivityLogsEntity.setImages(activityLogRequest.getImages());
            collectionActivityLogsEntity.setGeolocation(activityLogRequest.getGeolocationData());

            collectionActivityLogsRepository.save(collectionActivityLogsEntity);
            log.info("Saving collection activity log for user id {}", activityLogRequest.getUserId());

            return collectionActivityLogsEntity.getCollectionActivityLogsId();

    }


    private Date checkToDate(Date toDate){

        return DateUtils.addDays(toDate,1);

    }



}
