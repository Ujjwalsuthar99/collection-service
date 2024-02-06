package com.synoriq.synofin.collection.collectionservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.synoriq.synofin.collection.collectionservice.config.DatabaseContextHolder;
import com.synoriq.synofin.collection.collectionservice.entity.*;
import com.synoriq.synofin.collection.collectionservice.repository.*;
import com.synoriq.synofin.events.template.MessageContainerTemplate;
import com.synoriq.synofin.events.template.lms.CollectionRequestActionEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

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

    @KafkaListener(topics = "${spring.kafka.events.topic}", containerFactory = "kafkaListenerContainerFactory", groupId = "${spring.kafka.groupId}")
    public void consumerTest(@Payload MessageContainerTemplate message, @Headers MessageHeaders headers, Acknowledgment acknowledgment) {
        try {
//            changeAnnotationValue();
            log.info("client id in kafka {}", message.getClientId());
            DatabaseContextHolder.set(message.getClientId());
            log.info("message datatatatat ->  {}", message.getMessage());
            CollectionRequestActionEvent messageObject = new ObjectMapper().convertValue(message.getMessage(), CollectionRequestActionEvent.class);
            log.info("message object, {}", messageObject);
            log.info("messageObject.getUserId() {}", messageObject.getUserId());
            log.info("messageObject.getPaymentMode() {}", messageObject.getPaymentMode());

            Long userId = 0L;
            CollectionReceiptEntity collectionReceiptEntity = collectionReceiptRepository.findByReceiptId(messageObject.getServiceRequestId());
            log.info("collectionReceiptEntity1 {}", collectionReceiptEntity);
            if (collectionReceiptEntity != null) {
                userId = collectionReceiptEntity.getReceiptHolderUserId();
            }

            CollectionLimitUserWiseEntity collectionLimitUser = collectionLimitUserWiseRepository.findByUserIdAndCollectionLimitStrategiesKey(userId, messageObject.getPaymentMode());
            log.info("collection limit user wise surpassed {}", collectionLimitUser);
//            CollectionLimitUserWiseEntity collectionLimitUserWiseEntity = new CollectionLimitUserWiseEntity();

            log.info("service request id {}", messageObject.getServiceRequestId());
            Map<String, Object> loanIdByServiceId = receiptRepository.getLoanIdByServiceId(messageObject.getServiceRequestId());
            Long loanId = Long.valueOf(loanIdByServiceId.get("loanId").toString());
            String serviceRequestTypeString = String.valueOf(loanIdByServiceId.get("service_request_type_string"));

            log.info("check service request, {}", collectionReceiptEntity);

            CollectionActivityLogsEntity checkCollectionActivityLogsEntity = collectionActivityLogsRepository.getActivityLogsKafkaByReceiptId(String.valueOf(messageObject.getServiceRequestId()));
            log.info("checkCollectionActivityLogsEntity {}", checkCollectionActivityLogsEntity);
            if (collectionLimitUser != null && collectionReceiptEntity != null && checkCollectionActivityLogsEntity == null) {
                if(serviceRequestTypeString.equals("receipt")) {
                    if(collectionLimitUser.getUtilizedLimitValue() - Double.parseDouble(String.valueOf(messageObject.getReceiptAmount())) < 0 ) {
                        log.info("in iff for limit minus check {}", collectionLimitUser.getUtilizedLimitValue() - Double.parseDouble(String.valueOf(messageObject.getReceiptAmount())));
                        collectionLimitUser.setUtilizedLimitValue(0D);
                    } else {
                        log.info("in else for limit minus check {}", collectionLimitUser.getUtilizedLimitValue() - Double.parseDouble(String.valueOf(messageObject.getReceiptAmount())));
                        collectionLimitUser.setUtilizedLimitValue(collectionLimitUser.getUtilizedLimitValue() - Double.parseDouble(String.valueOf(messageObject.getReceiptAmount())));
                    }
                    collectionLimitUser.setUserName(messageObject.getUserName());
                    log.info("collection limit user wise entity {}", collectionLimitUser);
                    collectionLimitUserWiseRepository.save(collectionLimitUser);
                }

                CollectionActivityLogsEntity collectionActivityLogsEntity = new CollectionActivityLogsEntity();
                collectionActivityLogsEntity.setActivityBy(messageObject.getUserId());
                String updatedRemarks = KAFKA_RECEIPT_STATUS;
                updatedRemarks = updatedRemarks.replace("{status}", messageObject.getStatus());
                updatedRemarks = updatedRemarks.replace("{receipt_id}", String.valueOf(messageObject.getServiceRequestId()));
                collectionActivityLogsEntity.setRemarks(updatedRemarks);
                collectionActivityLogsEntity.setActivityDate(new Date());
                collectionActivityLogsEntity.setActivityName("receipt_" + messageObject.getStatus());
                collectionActivityLogsEntity.setDeleted(false);
                collectionActivityLogsEntity.setLoanId(loanId);
                collectionActivityLogsEntity.setDistanceFromUserBranch(0.0);
                collectionActivityLogsEntity.setAddress("{}");
                collectionActivityLogsEntity.setImages(null);
                collectionActivityLogsEntity.setGeolocation("{}");
                collectionActivityLogsRepository.save(collectionActivityLogsEntity);

                Long currentTotalReceiptsCount = 0L;
                // find the receipt transfer id in which this current receipt lies
                Long receiptTransferId = receiptTransferHistoryRepository.getReceiptTransferIdUsingReceiptId(messageObject.getServiceRequestId());
                log.info("receiptTransferId {}", receiptTransferId);
                // find the total number of receipts lies within the receipt transfer id
                Long totalReceiptCountFromReceiptTransfer = receiptTransferHistoryRepository.getReceiptCountFromReceiptTransfer(receiptTransferId);
                log.info("totalReceiptCountFromReceiptTransfer {}", totalReceiptCountFromReceiptTransfer);
                // find the number of approved receipts within the receipt transfer and add the current receipt id count to it

                List<Map<String, Object>> receiptHistoryCount = receiptTransferHistoryRepository.getDepositPendingReceipt(receiptTransferId);
                log.info("receiptHistoryCount {}", Collections.singletonList(receiptHistoryCount));
                log.info("receiptHistoryCount {}", Arrays.asList(receiptHistoryCount));
                log.info("receiptHistoryCount {}", receiptHistoryCount);

                log.info("receiptHistoryCount.size() {}", receiptHistoryCount.size());
                // add the condition where will compare the approved count with total number of receipts

                currentTotalReceiptsCount += receiptHistoryCount.size();

                log.info("currentTotalReceiptsCount {}", currentTotalReceiptsCount);

                // if the total number of receipts are equal to the approved counts then only will approve the receipt transfer

                if (currentTotalReceiptsCount.equals(totalReceiptCountFromReceiptTransfer)) {
                    log.info("in ifffff");
                    ReceiptTransferEntity receiptTransferEntity = receiptTransferRepository.findByReceiptTransferId(receiptTransferId);
                    receiptTransferEntity.setStatus("approved");
                    receiptTransferEntity.setActionBy(messageObject.getUserId());
                    receiptTransferEntity.setActionDatetime(new Date());
                    receiptTransferRepository.save(receiptTransferEntity);
                }

                log.info(" ---------- Things acknowledged -------------");
            }
            acknowledgment.acknowledge();
        } catch (Exception ee) {
            ee.printStackTrace();
        }
    }
}
