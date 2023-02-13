package com.synoriq.synofin.collection.collectionservice.service;

import com.synoriq.synofin.collection.collectionservice.repository.DashboardRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class DashboardService {

    @Autowired
    private DashboardRepository dashboardRepository;

    public Map<String, Map> getDashboardCountByUserId(Long userId, Date fromDate, Date toDate) throws Exception {
        Map<String, Map> responseLoans = new HashMap<>();

        try {
            Map<String, Object> dataCounts = dashboardRepository.getFollowupCountByUserIdByDuration(userId, fromDate, toDate);
            log.info("my data counts from followup {}", dataCounts);
            responseLoans.put("followup", dataCounts);
        } catch (Exception e) {
            throw new Exception("1017000");
        }
        return responseLoans;


    }

}
