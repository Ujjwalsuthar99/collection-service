package com.synoriq.synofin.collection.collectionservice.implementation;

import com.synoriq.synofin.collection.collectionservice.repository.CollectionConfigurationsRepository;
import com.synoriq.synofin.collection.collectionservice.repository.DashboardRepository;
import com.synoriq.synofin.collection.collectionservice.repository.ReceiptRepository;
import com.synoriq.synofin.collection.collectionservice.rest.response.DashboardDTOs.*;
import com.synoriq.synofin.collection.collectionservice.service.DashboardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

import static com.synoriq.synofin.collection.collectionservice.common.GlobalVariables.*;

@Service
@Slf4j
public class DashboardServiceImpl implements DashboardService {
    @Autowired
    private ReceiptRepository receiptRepository;

    @Autowired
    private DashboardRepository dashboardRepository;

    @Autowired
    private CollectionConfigurationsRepository collectionConfigurationsRepository;
    @Override
    public DashboardResponseDTO getDashboardCountByUserId(Long userId, String userName, String startDate, String toDate) throws Exception {
//        Map<String, Map> responseLoans = new HashMap<>();
        // adding 1 day in incoming toDate //
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        Calendar c = Calendar.getInstance();
        c.setTime(formatter.parse(toDate));
        c.add(Calendar.DATE, 1);  // number of days to add
        String to = formatter.format(c.getTime());
        Date endDate = formatter.parse(to);
        Date fromDate = formatter.parse(startDate);

        DashboardResponseDTO dashboardResponseDTO = new DashboardResponseDTO();
        try {
            CashInHandDashboardDTO cashInHandDashboardDTO = new CashInHandDashboardDTO();
            ChequeAmountDashboardDTO chequeAmountDashboardDTO = new ChequeAmountDashboardDTO();
            UpiAmountDashboardDTO upiAmountDashboardDTO = new UpiAmountDashboardDTO();
            CommonCountDashboardDTO commonCountDashboardDTO = new CommonCountDashboardDTO();
            FollowUpDashboardDTO followUpDashboardDTO = new FollowUpDashboardDTO();

            Map<String, Object> followupDataCounts = dashboardRepository.getFollowupCountByUserIdByDuration(userId, fromDate, endDate);
            Map<String, Object> amountTransferDataCounts = dashboardRepository.getAmountTransferCountByUserIdByDuration(userId, fromDate, endDate);
            Map<String, Object> amountTransferInProcessDataCounts = dashboardRepository.getAmountTransferInProcessCountByUserIdByDuration(userId, fromDate, endDate);
            Map<String, Object> receiptDataCounts = dashboardRepository.getReceiptCountByUserIdByDuration(userName, startDate, toDate);
            Map<String, Object> cashInHandDataCounts = dashboardRepository.getCashInHandByUserIdByDuration(userId);
            Map<String, Object> chequeAmountData = dashboardRepository.getChequeByUserIdByDuration(userId);
            Map<String, Object> upiAmountData = dashboardRepository.getUpiByUserIdByDuration(userId);
            if (cashInHandDataCounts.isEmpty()) {
                Double totalLimitValue = Double.valueOf(collectionConfigurationsRepository.findConfigurationValueByConfigurationName(CASH_COLLECTION_DEFAULT_LIMIT));
                cashInHandDashboardDTO.setCashInHand(0D);
                cashInHandDashboardDTO.setCashInHandLimit(totalLimitValue);
            }
            if (chequeAmountData.isEmpty()) {
                Double totalLimitValue = Double.valueOf(collectionConfigurationsRepository.findConfigurationValueByConfigurationName(CHEQUE_COLLECTION_DEFAULT_LIMIT));
                chequeAmountDashboardDTO.setChequeAmount(0D);
                chequeAmountDashboardDTO.setChequeLimit(totalLimitValue);
            }
            if (upiAmountData.isEmpty()) {
                Double totalLimitValue = Double.valueOf(collectionConfigurationsRepository.findConfigurationValueByConfigurationName(ONLINE_COLLECTION_DEFAULT_LIMIT));
                upiAmountDashboardDTO.setUpiAmount(0D);
                upiAmountDashboardDTO.setUpiLimit(totalLimitValue);
            }
            // cash in hand
            cashInHandDashboardDTO.setCashInHand(Double.valueOf(String.valueOf(cashInHandDataCounts.get("cash_in_hand"))));
            cashInHandDashboardDTO.setCashInHandLimit(Double.valueOf(String.valueOf(cashInHandDataCounts.get("cash_in_hand_limit"))));

            // cheque in hand
            chequeAmountDashboardDTO.setChequeAmount(Double.valueOf(String.valueOf(chequeAmountData.get("cheque_amount"))));
            chequeAmountDashboardDTO.setChequeLimit(Double.valueOf(String.valueOf(chequeAmountData.get("cheque_limit"))));

            // upi amount
            upiAmountDashboardDTO.setUpiAmount(Double.valueOf(String.valueOf(upiAmountData.get("upi_amount"))));
            upiAmountDashboardDTO.setUpiLimit(Double.valueOf(String.valueOf(upiAmountData.get("upi_limit"))));

            // followUp
            followUpDashboardDTO.setActionCount(0D);
            followUpDashboardDTO.setTotalCount(Double.valueOf(String.valueOf(followupDataCounts.get("total_count"))));

            // receipts data
            commonCountDashboardDTO.setTotalCount(Double.valueOf(String.valueOf(receiptDataCounts.get("total_count"))));
            commonCountDashboardDTO.setTotalAmount(Double.valueOf(String.valueOf(receiptDataCounts.get("total_amount"))));
            dashboardResponseDTO.setReceipt(commonCountDashboardDTO);

            // amount transfer
            commonCountDashboardDTO.setTotalCount(Double.valueOf(String.valueOf(amountTransferDataCounts.get("total_count"))));
            commonCountDashboardDTO.setTotalAmount(Double.valueOf(String.valueOf(amountTransferDataCounts.get("total_amount"))));
            dashboardResponseDTO.setAmountTransfer(commonCountDashboardDTO);

            // amount transfer in process
            commonCountDashboardDTO.setTotalCount(Double.valueOf(String.valueOf(amountTransferInProcessDataCounts.get("total_count"))));
            commonCountDashboardDTO.setTotalAmount(Double.valueOf(String.valueOf(amountTransferInProcessDataCounts.get("total_amount"))));
            dashboardResponseDTO.setAmountTransferInProcess(commonCountDashboardDTO);


            dashboardResponseDTO.setCashInHand(cashInHandDashboardDTO);
            dashboardResponseDTO.setChequeAmount(chequeAmountDashboardDTO);
            dashboardResponseDTO.setUpiAmount(upiAmountDashboardDTO);
            dashboardResponseDTO.setFollowUp(followUpDashboardDTO);
            dashboardResponseDTO.setDepositReminder(false);
//            String depositReminder = collectionConfigurationsRepository.findConfigurationValueByConfigurationName(DEPOSIT_REMINDER);
//            if (Objects.equals(depositReminder, "true")) {
//                String depositReminderHours = collectionConfigurationsRepository.findConfigurationValueByConfigurationName(DEPOSIT_REMINDER_HOURS);
//                List<Map<String, Object>> notDepositedReceipts = receiptRepository.depositReminderData(userId, depositReminderHours);
////                List<Map<String, Object>> notDepositedReceipts = receiptRepository.depositReminderData(userId);
//                if (notDepositedReceipts.size() > 0) {
//                    dashboardResponseDTO.setDepositReminder(true);
//                }
//            }
        } catch (Exception e) {
            throw new Exception("1017000");
        }
        return dashboardResponseDTO;
    }
}
