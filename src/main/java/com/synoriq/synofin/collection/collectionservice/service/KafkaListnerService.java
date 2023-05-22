package com.synoriq.synofin.collection.collectionservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.synoriq.synofin.collection.collectionservice.config.DatabaseContextHolder;
import com.synoriq.synofin.collection.collectionservice.entity.CollectionActivityLogsEntity;
import com.synoriq.synofin.collection.collectionservice.entity.CollectionLimitUserWiseEntity;
import com.synoriq.synofin.collection.collectionservice.entity.CollectionReceiptEntity;
import com.synoriq.synofin.collection.collectionservice.repository.CollectionActivityLogsRepository;
import com.synoriq.synofin.collection.collectionservice.repository.CollectionLimitUserWiseRepository;
import com.synoriq.synofin.collection.collectionservice.repository.CollectionReceiptRepository;
import com.synoriq.synofin.collection.collectionservice.repository.ReceiptRepository;
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
            CollectionLimitUserWiseEntity collectionLimitUser = collectionLimitUserWiseRepository.findByUserIdAndCollectionLimitStrategiesKey(messageObject.getUserId(), messageObject.getPaymentMode());
            log.info("collection limit user wise surpassed {}", collectionLimitUser);
            CollectionLimitUserWiseEntity collectionLimitUserWiseEntity = new CollectionLimitUserWiseEntity();

            log.info("service request id {}", messageObject.getServiceRequestId());
            CollectionReceiptEntity collectionReceiptEntity = collectionReceiptRepository.findByReceiptId(messageObject.getServiceRequestId());
//            Map<String, Object> collectionReceiptEntity = null;
//            collectionReceiptEntity = collectionReceiptRepository.findDataByReceiptId(messageObject.getServiceRequestId());

            log.info("check service request, {}", collectionReceiptEntity);

            if(collectionLimitUser != null && collectionReceiptEntity != null) {
                log.info("in iffff");
                collectionLimitUserWiseEntity.setCollectionLimitDefinitionsId(collectionLimitUser.getCollectionLimitDefinitionsId());
                collectionLimitUserWiseEntity.setCreatedDate(new Date());
                collectionLimitUserWiseEntity.setDeleted(collectionLimitUser.getDeleted());
                collectionLimitUserWiseEntity.setCollectionLimitStrategiesKey(collectionLimitUser.getCollectionLimitStrategiesKey());
                collectionLimitUserWiseEntity.setUserId(collectionLimitUser.getUserId());
                collectionLimitUserWiseEntity.setTotalLimitValue(collectionLimitUser.getTotalLimitValue());
                collectionLimitUserWiseEntity.setUtilizedLimitValue(collectionLimitUser.getUtilizedLimitValue() - Double.parseDouble(String.valueOf(messageObject.getReceiptAmount())));
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
                collectionActivityLogsEntity.setLoanId(0L);
                collectionActivityLogsEntity.setDistanceFromUserBranch(0.0);
                collectionActivityLogsEntity.setAddress("{}");
                collectionActivityLogsEntity.setImages(null);
                collectionActivityLogsEntity.setGeolocation("{}");
                collectionActivityLogsRepository.save(collectionActivityLogsEntity);
            }
            acknowledgment.acknowledge();
            log.info(" ---------- Things acknowledged -------------");
        } catch (Exception ee) {
            ee.printStackTrace();
        }
    }
}
