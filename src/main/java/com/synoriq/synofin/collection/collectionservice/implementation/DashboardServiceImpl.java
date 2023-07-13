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
    @Override
    public DashboardResponseDTO temp(Long userId, String userName, String startDate, String toDate) throws Exception {
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
            log.info("--------------------------111111");
            Map<String, Object> followupDataCounts = dashboardRepository.getFollowupCountByUserIdByDuration(userId, fromDate, endDate);
            Map<String, Object> amountTransferDataCounts = dashboardRepository.getAmountTransferCountByUserIdByDuration(userId, fromDate, endDate);
            Map<String, Object> amountTransferInProcessDataCounts = dashboardRepository.getAmountTransferInProcessCountByUserIdByDuration(userId, fromDate, endDate);
            Map<String, Object> receiptDataCounts = dashboardRepository.getReceiptCountByUserIdByDuration(userName, startDate, toDate);
            Map<String, Object> cashInHandDataCounts = dashboardRepository.getCashInHandByUserIdByDuration(userId);
            Map<String, Object> chequeAmountData = dashboardRepository.getChequeByUserIdByDuration(userId);
            Map<String, Object> upiAmountData = dashboardRepository.getUpiByUserIdByDuration(userId);
            log.info("--------------------------222222");
            if (cashInHandDataCounts.isEmpty()) {
                Double totalLimitValue = Double.valueOf(collectionConfigurationsRepository.findConfigurationValueByConfigurationName(CASH_COLLECTION_DEFAULT_LIMIT));
                cashInHandDashboardDTO.setCashInHand(0D);
                cashInHandDashboardDTO.setCashInHandLimit(totalLimitValue);
            }
            log.info("--------------------------333333 {}", cashInHandDashboardDTO);
            if (chequeAmountData.isEmpty()) {
                Double totalLimitValue = Double.valueOf(collectionConfigurationsRepository.findConfigurationValueByConfigurationName(CHEQUE_COLLECTION_DEFAULT_LIMIT));
                chequeAmountDashboardDTO.setChequeAmount(0D);
                chequeAmountDashboardDTO.setChequeLimit(totalLimitValue);
            }
            log.info("--------------------------444444 {}", chequeAmountDashboardDTO);
            if (upiAmountData.isEmpty()) {
                Double totalLimitValue = Double.valueOf(collectionConfigurationsRepository.findConfigurationValueByConfigurationName(ONLINE_COLLECTION_DEFAULT_LIMIT));
                upiAmountDashboardDTO.setUpiAmount(0D);
                upiAmountDashboardDTO.setUpiLimit(totalLimitValue);
            }
            log.info("--------------------------555555 {}", upiAmountDashboardDTO);
            // cash in hand
            cashInHandDashboardDTO.setCashInHand(Double.valueOf(String.valueOf(cashInHandDataCounts.get("cash_in_hand"))));
            cashInHandDashboardDTO.setCashInHandLimit(Double.valueOf(String.valueOf(cashInHandDataCounts.get("cash_in_hand_limit"))));

            log.info("--------------------------666666 {}", cashInHandDashboardDTO);
            // cheque in hand
            chequeAmountDashboardDTO.setChequeAmount(Double.valueOf(String.valueOf(chequeAmountData.get("cheque_amount"))));
            chequeAmountDashboardDTO.setChequeLimit(Double.valueOf(String.valueOf(chequeAmountData.get("cheque_limit"))));

            log.info("--------------------------777777 {}", chequeAmountDashboardDTO);
            // upi amount
            upiAmountDashboardDTO.setUpiAmount(Double.valueOf(String.valueOf(upiAmountData.get("upi_amount"))));
            upiAmountDashboardDTO.setUpiLimit(Double.valueOf(String.valueOf(upiAmountData.get("upi_limit"))));

            log.info("--------------------------888888 {}", upiAmountDashboardDTO);
            // followUp
            followUpDashboardDTO.setActionCount(0D);
            followUpDashboardDTO.setTotalCount(Double.valueOf(String.valueOf(followupDataCounts.get("total_count"))));

            log.info("--------------------------999999 {}", followUpDashboardDTO);
            // receipts data
            commonCountDashboardDTO.setTotalCount(Double.valueOf(String.valueOf(receiptDataCounts.get("total_count"))));
            commonCountDashboardDTO.setTotalAmount(Double.valueOf(String.valueOf(receiptDataCounts.get("total_amount"))));
            dashboardResponseDTO.setReceipt(commonCountDashboardDTO);

            log.info("--------------------------000000 {}", commonCountDashboardDTO);
            // amount transfer
            CommonCountDashboardDTO amountTransferDataCount = new CommonCountDashboardDTO();
            amountTransferDataCount.setTotalCount(Double.valueOf(String.valueOf(amountTransferDataCounts.get("total_count"))));
            amountTransferDataCount.setTotalAmount(Double.valueOf(String.valueOf(amountTransferDataCounts.get("total_amount"))));
            dashboardResponseDTO.setAmountTransfer(amountTransferDataCount);

            log.info("--------------------------010101010 {}", amountTransferDataCount);
            // amount transfer in process
            CommonCountDashboardDTO amountTransferInProcessDataCount = new CommonCountDashboardDTO();
            amountTransferInProcessDataCount.setTotalCount(Double.valueOf(String.valueOf(amountTransferInProcessDataCounts.get("total_count"))));
            amountTransferInProcessDataCount.setTotalAmount(Double.valueOf(String.valueOf(amountTransferInProcessDataCounts.get("total_amount"))));
            dashboardResponseDTO.setAmountTransferInProcess(amountTransferInProcessDataCount);

            log.info("--------------------------111111111 {}", amountTransferInProcessDataCount);

            dashboardResponseDTO.setCashInHand(cashInHandDashboardDTO);
            dashboardResponseDTO.setChequeAmount(chequeAmountDashboardDTO);
            dashboardResponseDTO.setUpiAmount(upiAmountDashboardDTO);
            dashboardResponseDTO.setFollowUp(followUpDashboardDTO);
            dashboardResponseDTO.setDepositReminder(false);
            log.info("--------------------------121212121212 {}", dashboardResponseDTO);
            String depositReminder = collectionConfigurationsRepository.findConfigurationValueByConfigurationName(DEPOSIT_REMINDER);
            log.info("--------------------------131313131313 {}", depositReminder);
            if (Objects.equals(depositReminder, "true")) {
                log.info("--------------------------141414141414 iniff");
                String depositReminderHours = collectionConfigurationsRepository.findConfigurationValueByConfigurationName(DEPOSIT_REMINDER_HOURS);
                log.info("--------------------------151515151515 {}", depositReminderHours);
                List<Map<String, Object>> notDepositedReceipts = receiptRepository.depositReminderData(userId, depositReminderHours);
                log.info("--------------------------161616161616 {}", notDepositedReceipts);
                if (notDepositedReceipts.size() > 0) {
                    log.info("--------------------------17171717171 {}", notDepositedReceipts.size());
                    dashboardResponseDTO.setDepositReminder(true);
                }
            }
            log.info("--------------------------18181818181818 {}", dashboardResponseDTO);
        } catch (Exception e) {
            log.info("errorerrorerrorerrorerrorerror message {}", e.getMessage());
            throw new Exception("1017000");
        }
        return dashboardResponseDTO;
    }
}
