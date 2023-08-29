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

import java.util.Date;
import java.util.List;
import java.util.Map;

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
//            CollectionLimitUserWiseEntity collectionLimitUser = collectionLimitUserWiseRepository.getCollectionLimitUserWiseByUserIdNew(messageObject.getUserId(), messageObject.getPaymentMode());

            Long userId = 0L;
            CollectionReceiptEntity collectionReceiptEntity1 = collectionReceiptRepository.findByReceiptId(messageObject.getServiceRequestId());
            if (collectionReceiptEntity1 != null) {
                userId = collectionReceiptEntity1.getReceiptHolderUserId();
            }

            CollectionLimitUserWiseEntity collectionLimitUser = collectionLimitUserWiseRepository.findByUserIdAndCollectionLimitStrategiesKey(userId, messageObject.getPaymentMode());
            log.info("collection limit user wise surpassed {}", collectionLimitUser);
            CollectionLimitUserWiseEntity collectionLimitUserWiseEntity = new CollectionLimitUserWiseEntity();

            log.info("service request id {}", messageObject.getServiceRequestId());
            CollectionReceiptEntity collectionReceiptEntity = collectionReceiptRepository.findByReceiptId(messageObject.getServiceRequestId());
            Map<String, Object> loanIdByServiceId = receiptRepository.getLoanIdByServiceId(messageObject.getServiceRequestId());
            Long loanId = Long.valueOf(loanIdByServiceId.get("loanId").toString());

            log.info("check service request, {}", collectionReceiptEntity);

            CollectionActivityLogsEntity checkCollectionActivityLogsEntity = collectionActivityLogsRepository.getActivityLogsKafkaByReceiptId(String.valueOf(messageObject.getServiceRequestId()));

            if (collectionLimitUser != null && collectionReceiptEntity != null && checkCollectionActivityLogsEntity == null) {
                log.info("in iffff");
                collectionLimitUserWiseEntity.setCollectionLimitDefinitionsId(collectionLimitUser.getCollectionLimitDefinitionsId());
                collectionLimitUserWiseEntity.setCreatedDate(new Date());
                collectionLimitUserWiseEntity.setDeleted(collectionLimitUser.getDeleted());
                collectionLimitUserWiseEntity.setCollectionLimitStrategiesKey(collectionLimitUser.getCollectionLimitStrategiesKey());
                collectionLimitUserWiseEntity.setUserId(collectionLimitUser.getUserId());
                collectionLimitUserWiseEntity.setTotalLimitValue(collectionLimitUser.getTotalLimitValue());
                collectionLimitUserWiseEntity.setUtilizedLimitValue(collectionLimitUser.getUtilizedLimitValue() - Double.parseDouble(String.valueOf(messageObject.getReceiptAmount())));
                collectionLimitUserWiseEntity.setUserName(messageObject.getUserName());
                log.info("collection limit user wise entity {}", collectionLimitUserWiseEntity);
                collectionLimitUserWiseRepository.save(collectionLimitUserWiseEntity);

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

                Map<String, Object> receiptHistoryCount = receiptTransferHistoryRepository.getDepositPendingReceipt(messageObject.getServiceRequestId());

                Long totalReceiptCountFromReceiptTransfer = receiptTransferHistoryRepository.getReceiptCountFromReceiptTransfer(Long.valueOf(String.valueOf(receiptHistoryCount.get("receipt_transfer_id"))));

                if (Long.valueOf(String.valueOf(receiptHistoryCount.get("pending_receipt_count"))).equals(totalReceiptCountFromReceiptTransfer)) {
                    ReceiptTransferEntity receiptTransferEntity = receiptTransferRepository.findByReceiptTransferId(Long.parseLong(String.valueOf(receiptHistoryCount.get("receipt_transfer_id"))));
                    receiptTransferEntity.setStatus("approved");
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
