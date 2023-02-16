package com.synoriq.synofin.collection.collectionservice.service;

import com.synoriq.synofin.collection.collectionservice.repository.DashboardRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Slf4j
public class DashboardService {

    @Autowired
    private DashboardRepository dashboardRepository;

    public Map<String, Map> getDashboardCountByUserId(Long userId, String startDate, String toDate) throws Exception {
        Map<String, Map> responseLoans = new HashMap<>();
        // adding 1 day in incoming toDate //
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        Calendar c = Calendar.getInstance();
        c.setTime(formatter.parse(toDate));
        c.add(Calendar.DATE, 1);  // number of days to add
        toDate = formatter.format(c.getTime());
        Date endDate = formatter.parse(toDate);
        Date fromDate = formatter.parse(startDate);

        try {
            Map<String, Object> followupDataCounts = dashboardRepository.getFollowupCountByUserIdByDuration(userId, fromDate, endDate);
            Map<String, Object> amountTransferDataCounts = dashboardRepository.getAmountTransferCountByUserIdByDuration(userId, fromDate, endDate);
            Map<String, Object> amountTransferInProcessDataCounts = dashboardRepository.getAmountTransferInProcessCountByUserIdByDuration(userId, fromDate, endDate);
            Map<String, Object> receiptDataCounts = dashboardRepository.getReceiptCountByUserIdByDuration(userId.toString(), startDate, toDate);
            Map<String, Object> cashInHandDataCounts = dashboardRepository.getCashInHandByUserIdByDuration(userId.toString(), startDate, toDate);
            Map<String, Object> chequeAmountData = dashboardRepository.getChequeByUserIdByDuration(userId.toString(), startDate, toDate);
            log.info("my data counts from followup {}", followupDataCounts);
            responseLoans.put("followup", followupDataCounts);
            responseLoans.put("receipt", receiptDataCounts);
            responseLoans.put("amount_transfer", amountTransferDataCounts);
            responseLoans.put("amount_transfer_inprocess", amountTransferInProcessDataCounts);
            responseLoans.put("cash_in_hand", cashInHandDataCounts);
            responseLoans.put("cheque_amount", chequeAmountData);
        } catch (Exception e) {
            throw new Exception("1017000");
        }
        return responseLoans;


    }

}
