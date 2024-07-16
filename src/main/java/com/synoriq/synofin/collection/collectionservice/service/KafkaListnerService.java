package com.synoriq.synofin.collection.collectionservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.synoriq.synofin.collection.collectionservice.common.EnumSQLConstants;
import com.synoriq.synofin.collection.collectionservice.config.DatabaseContextHolder;
import com.synoriq.synofin.collection.collectionservice.entity.*;
import com.synoriq.synofin.collection.collectionservice.repository.*;
import com.synoriq.synofin.events.template.MessageContainerTemplate;
import com.synoriq.synofin.events.template.lms.CollectionRequestActionEvent;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.synoriq.synofin.collection.collectionservice.common.ActivityRemarks.KAFKA_RECEIPT_STATUS;

@Service
@Slf4j
public class KafkaListnerService {

    @Autowired
    private CollectionLimitUserWiseRepository collectionLimitUserWiseRepository;

    @Autowired
    private ReceiptRepository receiptRepository;

    @Autowired
    private CollectionReceiptRepository collectionReceiptRepository;

    @Autowired
    private CollectionActivityLogsRepository collectionActivityLogsRepository;

    @Autowired
    private ReceiptTransferHistoryRepository receiptTransferHistoryRepository;

    @Autowired
    private ReceiptTransferRepository receiptTransferRepository;

    @Autowired
    private ConsumedApiLogService consumedApiLogService;

    @KafkaListener(topics = "${spring.kafka.events.topic}", containerFactory = "kafkaListenerContainerFactory", groupId = "${spring.kafka.groupId}")
    public void consumerTest(@Payload MessageContainerTemplate message, @Headers MessageHeaders headers, Acknowledgment acknowledgment) {
        try {

            log.info("client id in kafka {}", message.getClientId());
            DatabaseContextHolder.set(message.getClientId());
            log.info("message datatatatat ->  {}", message.getMessage());
            CollectionRequestActionEvent messageObject = new ObjectMapper().convertValue(message.getMessage(), CollectionRequestActionEvent.class);
            MDC.put("SERVICE ID", messageObject.getServiceRequestId().toString());
            log.info("message object, {}", messageObject);
            log.info("messageObject.getUserId() {}", messageObject.getUserId());
            log.info("messageObject.getPaymentMode() {}", messageObject.getPaymentMode());
            log.info("service request id {}", messageObject.getServiceRequestId());
            log.info("servi id {}", messageObject.getReceiptAmount());

            double loanAmount = messageObject.getReceiptAmount();
            long receiptId = messageObject.getServiceRequestId();
            String status = messageObject.getStatus();
            String paymentMode = messageObject.getPaymentMode();
            long userId = 0;
            String userName = "";
            boolean isDepositHit = false;
            Map<String, Object> response = new HashMap<>();

            // checking this receipt id is already processed or not
            log.info("1");
            CollectionActivityLogsEntity checkCollectionActivityLogsEntity = collectionActivityLogsRepository.getActivityLogsKafkaByReceiptId(String.valueOf(receiptId));
            log.info("checkCollectionActivityLogsEntity {}", checkCollectionActivityLogsEntity);

            log.info("2");
            Optional<CollectionReceiptEntity> collectionReceiptEntity = Optional.ofNullable(collectionReceiptRepository.findByReceiptId(receiptId));
            log.info("collectionReceiptEntity {}", collectionReceiptEntity);
            if (collectionReceiptEntity.isPresent()) {
                userId = collectionReceiptEntity.get().getReceiptHolderUserId();
                userName = collectionLimitUserWiseRepository.getUserNameFromUser(userId);
            }

            log.info("3");
            Optional<CollectionLimitUserWiseEntity> collectionLimitUser = Optional.ofNullable(collectionLimitUserWiseRepository.findByUserIdAndCollectionLimitStrategiesKey(userId, paymentMode));
            log.info("collection limit user wise surpassed {}", collectionLimitUser);

            // Adding delay for one second
            log.info("adding 3 seconds delay here");
            TimeUnit.SECONDS.sleep(3);

            // getting some data of receipt id
            Map<String, Object> loanIdByServiceId = receiptRepository.getLoanIdByServiceId(receiptId);
            log.info("loanIdByServiceId {}", loanIdByServiceId);

            log.info("4");
            // loanId & serviceRequestTypeString
            long loanId = Long.parseLong(loanIdByServiceId.get("loanId").toString());
            log.info("loanId {}", loanId);
            String serviceRequestTypeString = String.valueOf(loanIdByServiceId.get("service_request_type_string"));
            log.info("serviceRequestTypeString {}", serviceRequestTypeString);



            if (collectionLimitUser.isPresent() && collectionReceiptEntity.isPresent() && checkCollectionActivityLogsEntity == null) {
                if(serviceRequestTypeString.equals("receipt")) {
                    log.info("in serviceRequestTypeString");
                    if(collectionLimitUser.get().getUtilizedLimitValue() - loanAmount < 0) {
                        log.info("iff limit check {}", collectionLimitUser.get().getUtilizedLimitValue() - loanAmount);
                        collectionLimitUser.get().setUtilizedLimitValue(0D);
                    } else {
                        log.info("else limit check {}", collectionLimitUser.get().getUtilizedLimitValue() - loanAmount);
                        collectionLimitUser.get().setUtilizedLimitValue(collectionLimitUser.get().getUtilizedLimitValue() - loanAmount);
                    }
                    collectionLimitUser.get().setUserName(userName);
                    log.info("collectionLimitUserWise entity {}", collectionLimitUser);
                    collectionLimitUserWiseRepository.save(collectionLimitUser.get());
                }
                // activity creating
                createActivityLogs(messageObject.getUserId(), status, receiptId, loanId);

                Long restApprovedReceipts = 0L;
                // find the receipt transfer id in which this current receipt lies
                Long receiptTransferId = receiptTransferHistoryRepository.getReceiptTransferIdUsingReceiptId(receiptId);

                log.info("receiptTransferId {}", receiptTransferId);
                if (receiptTransferId != null) {

                    // find the total number of receipts lies within the receipt transfer id
                    Long totalReceiptCountFromReceiptTransfer = receiptTransferHistoryRepository.getReceiptCountFromReceiptTransfer(receiptTransferId);
                    log.info("totalReceiptCountFromReceiptTransfer {}", totalReceiptCountFromReceiptTransfer);


                    // find the number of approved receipts within the receipt transfer and add the current receipt id count to it
                    int approvedCount = 0;
                    int pendingCount = 0;
                    List<Map<String, Object>> receiptHistoryList = receiptTransferHistoryRepository.getDepositPendingReceipt(receiptTransferId);
                    log.info("receiptHistoryList {}", receiptHistoryList);
                    for (Map<String, Object> mp : receiptHistoryList) {
                        log.info("map data {}", mp);
                        if (Objects.equals(Long.parseLong(String.valueOf(mp.get("service_request_id"))), receiptId) && Objects.equals(String.valueOf(mp.get("status")), "pending") && status.equals("approved")) {
                            pendingCount++;
                            log.info("in pending count {}", pendingCount);
                        } else if (Objects.equals(Long.parseLong(String.valueOf(mp.get("service_request_id"))), receiptId) && Objects.equals(String.valueOf(mp.get("status")), "approved") && status.equals("approved")) {
                            approvedCount++;
                            log.info("in approve count {}", approvedCount);
                        } else if (status.equals("approved")) {
                            restApprovedReceipts++;
                            log.info("restApprovedReceipts {}", restApprovedReceipts);
                            log.info("receiptId and receiptStatus {}", mp);
                        }
                    }

                    // 2 += 0 + 1/ 1 + 0
                    restApprovedReceipts += pendingCount + approvedCount;
                    log.info("restApprovedReceiptsrestApprovedReceiptsrestApprovedReceipts {}", restApprovedReceipts);

                    // if the total number of receipts are equal to the approved counts then only will approve the receipt transfer

                    if (restApprovedReceipts.equals(totalReceiptCountFromReceiptTransfer)) {
                        log.info("in ifffff");
                        isDepositHit = true;
                        ReceiptTransferEntity receiptTransferEntity = receiptTransferRepository.findByReceiptTransferId(receiptTransferId);
                        receiptTransferEntity.setStatus("approved");
                        receiptTransferEntity.setActionBy(messageObject.getUserId());
                        receiptTransferEntity.setActionDatetime(new Date());
                        receiptTransferRepository.save(receiptTransferEntity);
                    }

                    response.put("isDepositHit", isDepositHit);
                    response.put("approvedCount", approvedCount);
                    response.put("pendingCount", pendingCount);
                    response.put("restApprovedReceipts", restApprovedReceipts);
                    response.put("totalReceiptCountFromReceiptTransfer", totalReceiptCountFromReceiptTransfer);
                    log.info("response {}", response);
                    consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.kafka_activity, userId, messageObject, response, "success", loanId, "POST", "");

                    log.info("end consumedApiLog");
                } else {
                    log.info("receipt transfer id not found");
                }

                log.info(" ---------- Things acknowledged -------------");
            }
            acknowledgment.acknowledge();
        } catch (Exception ee) {
            log.error("Kafka having an issue", ee);
        }
        finally {
            MDC.clear();
        }
    }
    public void createActivityLogs(long userId, String status, long receiptId, long loanId) {
        log.info("begin createActivityLogs");
        CollectionActivityLogsEntity collectionActivityLogsEntity = new CollectionActivityLogsEntity();
        collectionActivityLogsEntity.setActivityBy(userId);
        String updatedRemarks = KAFKA_RECEIPT_STATUS;
        updatedRemarks = updatedRemarks.replace("{status}", status);
        updatedRemarks = updatedRemarks.replace("{receipt_id}", String.valueOf(receiptId));
        collectionActivityLogsEntity.setRemarks(updatedRemarks);
        collectionActivityLogsEntity.setActivityDate(new Date());
        collectionActivityLogsEntity.setActivityName("receipt_" + status);
        collectionActivityLogsEntity.setDeleted(false);
        collectionActivityLogsEntity.setLoanId(loanId);
        collectionActivityLogsEntity.setDistanceFromUserBranch(0.0);
        collectionActivityLogsEntity.setAddress("{}");
        collectionActivityLogsEntity.setImages(null);
        collectionActivityLogsEntity.setGeolocation("{}");
        collectionActivityLogsRepository.save(collectionActivityLogsEntity);
        log.info("end createActivityLogs");
    }
}
