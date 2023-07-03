package com.synoriq.synofin.collection.collectionservice.implementation;

import com.synoriq.synofin.collection.collectionservice.repository.CollectionConfigurationsRepository;
import com.synoriq.synofin.collection.collectionservice.repository.DashboardRepository;
import com.synoriq.synofin.collection.collectionservice.service.DashboardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.synoriq.synofin.collection.collectionservice.common.GlobalVariables.*;

@Service
@Slf4j
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    private DashboardRepository dashboardRepository;

    @Autowired
    private CollectionConfigurationsRepository collectionConfigurationsRepository;
    @Override
    public Map<String, Map> getDashboardCountByUserId(Long userId, String userName, String startDate, String toDate) throws Exception {
        Map<String, Map> responseLoans = new HashMap<>();
        // adding 1 day in incoming toDate //
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        Calendar c = Calendar.getInstance();
        c.setTime(formatter.parse(toDate));
        c.add(Calendar.DATE, 1);  // number of days to add
        String to = formatter.format(c.getTime());
        Date endDate = formatter.parse(to);
        Date fromDate = formatter.parse(startDate);

        try {
            Map<String, Object> followupDataCounts = dashboardRepository.getFollowupCountByUserIdByDuration(userId, fromDate, endDate);
            Map<String, Object> amountTransferDataCounts = dashboardRepository.getAmountTransferCountByUserIdByDuration(userId, fromDate, endDate);
            Map<String, Object> amountTransferInProcessDataCounts = dashboardRepository.getAmountTransferInProcessCountByUserIdByDuration(userId, fromDate, endDate);
            Map<String, Object> receiptDataCounts = dashboardRepository.getReceiptCountByUserIdByDuration(userName, startDate, toDate);
            Map<String, Object> cashInHandDataCounts = dashboardRepository.getCashInHandByUserIdByDuration(userId);
            Map<String, Object> chequeAmountData = dashboardRepository.getChequeByUserIdByDuration(userId);
            Map<String, Object> upiAmountData = dashboardRepository.getUpiByUserIdByDuration(userId);
            if (cashInHandDataCounts.isEmpty()) {
                Double totalLimitValue = Double.valueOf(collectionConfigurationsRepository.findConfigurationValueByConfigurationName(CASH_COLLECTION_DEFAULT_LIMIT));
                Map<String, Object> newCashInHand = new HashMap<>();
                newCashInHand.put("cash_in_hand", 0);
                newCashInHand.put("cash_in_hand_limit", totalLimitValue);
                cashInHandDataCounts = newCashInHand;
            }
            if (chequeAmountData.isEmpty()) {
                Double totalLimitValue = Double.valueOf(collectionConfigurationsRepository.findConfigurationValueByConfigurationName(CHEQUE_COLLECTION_DEFAULT_LIMIT));
                Map<String, Object> newChequeAmount = new HashMap<>();
                newChequeAmount.put("cheque_amount", 0);
                newChequeAmount.put("cheque_limit", totalLimitValue);
                chequeAmountData = newChequeAmount;
            }
            if (upiAmountData.isEmpty()) {
                Double totalLimitValue = Double.valueOf(collectionConfigurationsRepository.findConfigurationValueByConfigurationName(ONLINE_COLLECTION_DEFAULT_LIMIT));
                Map<String, Object> newUpiAmount = new HashMap<>();
                newUpiAmount.put("upi_amount", 0);
                newUpiAmount.put("upi_limit", totalLimitValue);
                upiAmountData = newUpiAmount;
            }
            responseLoans.put("followup", followupDataCounts);
            responseLoans.put("receipt", receiptDataCounts);
            responseLoans.put("amount_transfer", amountTransferDataCounts);
            responseLoans.put("amount_transfer_inprocess", amountTransferInProcessDataCounts);
            responseLoans.put("cash_in_hand", cashInHandDataCounts);
            responseLoans.put("cheque_amount", chequeAmountData);
            responseLoans.put("upi_amount", upiAmountData);

        } catch (Exception e) {
            throw new Exception("1017000");
        }
        return responseLoans;


    }

}
