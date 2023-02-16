package com.synoriq.synofin.collection.collectionservice.service;

import com.synoriq.synofin.collection.collectionservice.entity.FollowUpEntity;
import com.synoriq.synofin.collection.collectionservice.repository.DashboardRepository;
import com.synoriq.synofin.collection.collectionservice.repository.TaskRepository;
import com.synoriq.synofin.collection.collectionservice.rest.response.BaseDTOResponse;
import com.synoriq.synofin.collection.collectionservice.rest.response.FollowupResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class TaskService {

    @Autowired
    private DashboardRepository dashboardRepository;

    @Autowired
    private FollowUpService followUpService;

    @Autowired
    private TaskRepository taskRepository;

//    public Map<String, Map> getDashboardCountByUserId(Long userId, Date fromDate, Date toDate) throws Exception {
//        Map<String, Map> responseLoans = new HashMap<>();
//
//        try {
//            Map<String, Object> followupDataCounts = dashboardRepository.getFollowupCountByUserIdByDuration(userId, fromDate, toDate);
//            Map<String, Object> amountTransferDataCounts = dashboardRepository.getAmountTransferCountByUserIdByDuration(userId, fromDate, toDate);
//            Map<String, Object> receiptDataCounts = dashboardRepository.getReceiptCountByUserIdByDuration(userId.toString(), fromDate, toDate);
//            log.info("my data counts from followup {}", followupDataCounts);
//            responseLoans.put("followup", followupDataCounts);
//            responseLoans.put("receipt", receiptDataCounts);
//            responseLoans.put("amount_transfer", amountTransferDataCounts);
//        } catch (Exception e) {
//            throw new Exception("1017000");
//        }
//        return responseLoans;
//
//
//    }

    public BaseDTOResponse<Object> getTaskDetails(Integer pageNo, Integer pageSize) throws Exception {


        BaseDTOResponse<Object> baseDTOResponse;
        Pageable pageRequest;
        if(pageNo>0){
            pageNo = pageNo-1;
        }
        pageRequest = PageRequest.of(pageNo,pageSize);

        List<Map<String,Object>> list = new ArrayList<>();



        List<Map<String,Object>> taskDetailPages = taskRepository.getTaskDetailsByPages(pageRequest);

        List<Object> taskDetailsData;

        if (!taskDetailPages.isEmpty()) {
//            taskDetailsData = taskDetailPages.getContent();
        } else {
            log.error("Task Data for page {}", pageNo);
            throw new Exception("1016025");
        }





        baseDTOResponse = new BaseDTOResponse<>(taskDetailPages);

        return baseDTOResponse;

    }

}
