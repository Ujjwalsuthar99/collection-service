package com.synoriq.synofin.collection.collectionservice.service.implementation;

import com.synoriq.synofin.collection.collectionservice.repository.CollectionConfigurationsRepository;
import com.synoriq.synofin.collection.collectionservice.repository.DashboardRepository;
import com.synoriq.synofin.collection.collectionservice.repository.LoanAllocationRepository;
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
    private LoanAllocationRepository loanAllocationRepository;

    @Autowired
    private CollectionConfigurationsRepository collectionConfigurationsRepository;
    @Override
    public DashboardResponseDTO getDashboardCountByUserId(Long userId, String userName, String startDate, String toDate) throws Exception {
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
            NeftAmountDashboardDTO neftAmountDashboardDTO = new NeftAmountDashboardDTO();
            RtgsAmountDashboardDTO rtgsAmountDashboardDTO = new RtgsAmountDashboardDTO();
            Map<String, Object> followupDataCounts = dashboardRepository.getFollowupCountByUserIdByDuration(userId, fromDate, endDate);
            Map<String, Object> amountTransferDataCounts = dashboardRepository.getAmountTransferCountByUserIdByDuration(userId, fromDate, endDate);
            Map<String, Object> amountTransferInProcessDataCounts = dashboardRepository.getAmountTransferInProcessCountByUserIdByDuration(userId, fromDate, endDate);
            Map<String, Object> receiptDataCounts = dashboardRepository.getReceiptCountByUserIdByDuration(userId, startDate, toDate);
            Map<String, Object> cashInHandDataCounts = dashboardRepository.getCashInHandByUserIdByDuration(userId);
            Map<String, Object> chequeAmountData = dashboardRepository.getChequeByUserIdByDuration(userId);
            Map<String, Object> upiAmountData = dashboardRepository.getUpiByUserIdByDuration(userId);
            int taskCountData = loanAllocationRepository.getCountByAllocatedToUserIdAndDeleted(userId);
            Map<String, Object> netfAmountData = dashboardRepository.getNeftByUserIdByDuration(userId);
            Map<String, Object> rtgsAmountData = dashboardRepository.getRtgsByUserIdByDuration(userId);
            double totalCashInHand = 0.0;
            // cash in hand
            if (cashInHandDataCounts.isEmpty()) {
                Double totalLimitValue = Double.valueOf(collectionConfigurationsRepository.findConfigurationValueByConfigurationName(CASH_COLLECTION_DEFAULT_LIMIT));
                cashInHandDashboardDTO.setCashInHand(0D);
                cashInHandDashboardDTO.setCashInHandLimit(totalLimitValue);
            } else {
                cashInHandDashboardDTO.setCashInHand(Double.valueOf(String.valueOf(cashInHandDataCounts.get("cash_in_hand"))));
                totalCashInHand += cashInHandDashboardDTO.getCashInHand();
                cashInHandDashboardDTO.setCashInHandLimit(Double.valueOf(String.valueOf(cashInHandDataCounts.get("cash_in_hand_limit"))));
            }

            // cheque in hand
            if (chequeAmountData.isEmpty()) {
                Double totalLimitValue = Double.valueOf(collectionConfigurationsRepository.findConfigurationValueByConfigurationName(CHEQUE_COLLECTION_DEFAULT_LIMIT));
                chequeAmountDashboardDTO.setChequeAmount(0D);
                chequeAmountDashboardDTO.setChequeLimit(totalLimitValue);
            } else {
                chequeAmountDashboardDTO.setChequeAmount(Double.valueOf(String.valueOf(chequeAmountData.get("cheque_amount"))));
                totalCashInHand += chequeAmountDashboardDTO.getChequeAmount();
                chequeAmountDashboardDTO.setChequeLimit(Double.valueOf(String.valueOf(chequeAmountData.get("cheque_limit"))));
            }

            // upi amount
            if (upiAmountData.isEmpty()) {
                Double totalLimitValue = Double.valueOf(collectionConfigurationsRepository.findConfigurationValueByConfigurationName(ONLINE_COLLECTION_DEFAULT_LIMIT));
                upiAmountDashboardDTO.setUpiAmount(0D);
                upiAmountDashboardDTO.setUpiLimit(totalLimitValue);
            } else {
                upiAmountDashboardDTO.setUpiAmount(Double.valueOf(String.valueOf(upiAmountData.get("upi_amount"))));
                totalCashInHand += upiAmountDashboardDTO.getUpiAmount();
                upiAmountDashboardDTO.setUpiLimit(Double.valueOf(String.valueOf(upiAmountData.get("upi_limit"))));
            }

            // neft amount
            if (netfAmountData.isEmpty()) {
                Double totalLimitValue = Double.valueOf(collectionConfigurationsRepository.findConfigurationValueByConfigurationName(NEFT_COLLECTION_DEFAULT_LIMIT));
                neftAmountDashboardDTO.setNeftAmount(0D);
                neftAmountDashboardDTO.setNeftLimit(totalLimitValue);
            } else {
                neftAmountDashboardDTO.setNeftAmount(Double.valueOf(String.valueOf(netfAmountData.get("neft_amount"))));
                totalCashInHand += neftAmountDashboardDTO.getNeftAmount();
                neftAmountDashboardDTO.setNeftLimit(Double.valueOf(String.valueOf(netfAmountData.get("neft_limit"))));
            }

            // rtgs amount
            if (rtgsAmountData.isEmpty()) {
                Double totalLimitValue = Double.valueOf(collectionConfigurationsRepository.findConfigurationValueByConfigurationName(RTGS_COLLECTION_DEFAULT_LIMIT));
                rtgsAmountDashboardDTO.setRtgsAmount(0D);
                rtgsAmountDashboardDTO.setRtgsLimit(totalLimitValue);
            } else {
                rtgsAmountDashboardDTO.setRtgsAmount(Double.valueOf(String.valueOf(rtgsAmountData.get("rtgs_amount"))));
                totalCashInHand += rtgsAmountDashboardDTO.getRtgsAmount();
                rtgsAmountDashboardDTO.setRtgsLimit(Double.valueOf(String.valueOf(rtgsAmountData.get("rtgs_limit"))));
            }
            // followUp
            followUpDashboardDTO.setActionCount(0D);
            followUpDashboardDTO.setTotalCount(Double.valueOf(String.valueOf(followupDataCounts.get("total_count"))));

            // receipts data
            commonCountDashboardDTO.setTotalCount(Double.valueOf(String.valueOf(receiptDataCounts.get("total_count"))));
            commonCountDashboardDTO.setTotalAmount(Double.valueOf(String.valueOf(receiptDataCounts.get("total_amount"))));
            dashboardResponseDTO.setReceipt(commonCountDashboardDTO);

            // amount transfer
            CommonCountDashboardDTO amountTransferDataCount = new CommonCountDashboardDTO();
            amountTransferDataCount.setTotalCount(Double.valueOf(String.valueOf(amountTransferDataCounts.get("total_count"))));
            amountTransferDataCount.setTotalAmount(Double.valueOf(String.valueOf(amountTransferDataCounts.get("total_amount"))));
            dashboardResponseDTO.setAmountTransfer(amountTransferDataCount);

            // amount transfer in process
            CommonCountDashboardDTO amountTransferInProcessDataCount = new CommonCountDashboardDTO();
            amountTransferInProcessDataCount.setTotalCount(Double.valueOf(String.valueOf(amountTransferInProcessDataCounts.get("total_count"))));
            amountTransferInProcessDataCount.setTotalAmount(Double.valueOf(String.valueOf(amountTransferInProcessDataCounts.get("total_amount"))));
            dashboardResponseDTO.setAmountTransferInProcess(amountTransferInProcessDataCount);

            dashboardResponseDTO.setCashInHand(cashInHandDashboardDTO);
            dashboardResponseDTO.setChequeAmount(chequeAmountDashboardDTO);
            dashboardResponseDTO.setUpiAmount(upiAmountDashboardDTO);
            dashboardResponseDTO.setFollowUp(followUpDashboardDTO);
            dashboardResponseDTO.setTaskCount(taskCountData);
            dashboardResponseDTO.setDepositReminder(false);
            dashboardResponseDTO.setNeftAmount(neftAmountDashboardDTO);
            dashboardResponseDTO.setRtgsAmount(rtgsAmountDashboardDTO);
            dashboardResponseDTO.setTotalPaymentAmount(totalCashInHand);
            String depositReminder = collectionConfigurationsRepository.findConfigurationValueByConfigurationName(DEPOSIT_REMINDER);
            if (Objects.equals(depositReminder, "hours")) {
                String depositReminderHours = collectionConfigurationsRepository.findConfigurationValueByConfigurationName(DEPOSIT_REMINDER_HOURS);
                List<Map<String, Object>> notDepositedReceipts = receiptRepository.depositReminderData(userId, depositReminderHours);
                if (notDepositedReceipts.size() > 0) {
                    dashboardResponseDTO.setDepositReminder(true);
                }
            } else if (Objects.equals(depositReminder, "daytime")) {
                String depositReminderDayTime = collectionConfigurationsRepository.findConfigurationValueByConfigurationName(DEPOSIT_REMINDER_DAYTIME);
                String[] dayTime = depositReminderDayTime.split("/");
                Map<String, Object> getReceiptDataNotDeposited = receiptRepository.depositReminderDataByDayTime(userId);
                if(getReceiptDataNotDeposited.size() != 0) {
                    String strDate = String.valueOf(getReceiptDataNotDeposited.get("created_date"));
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    Date date = dateFormat.parse(strDate);

                    int daysToAdd = Integer.parseInt(dayTime[0]);
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(date);
                    calendar.add(Calendar.DAY_OF_YEAR, daysToAdd);
                    calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(dayTime[1]));
                    calendar.set(Calendar.MINUTE, 0);
                    calendar.set(Calendar.SECOND, 0);

                    Date finalDate = calendar.getTime();
                    Date currentDate = new Date();
                    if (finalDate.compareTo(currentDate) < 0) {
                        dashboardResponseDTO.setDepositReminder(true);
                    } else {
                        dashboardResponseDTO.setDepositReminder(false);
                    }
                } else {
                    dashboardResponseDTO.setDepositReminder(false);
                }
            }
        } catch (Exception e) {
            log.info("errorerrorerrorerrorerrorerror message {}", e.getMessage());
            throw new Exception("1017000");
        }
        return dashboardResponseDTO;
    }
}
