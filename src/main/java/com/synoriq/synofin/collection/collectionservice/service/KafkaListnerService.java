package com.synoriq.synofin.collection.collectionservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.synoriq.synofin.collection.collectionservice.entity.CollectionActivityLogsEntity;
import com.synoriq.synofin.collection.collectionservice.entity.CollectionLimitUserWiseEntity;
import com.synoriq.synofin.collection.collectionservice.repository.CollectionActivityLogsRepository;
import com.synoriq.synofin.collection.collectionservice.repository.CollectionLimitUserWiseRepository;
import com.synoriq.synofin.collection.collectionservice.repository.ReceiptRepository;
import com.synoriq.synofin.collection.collectionservice.rest.request.masterDTOs.MasterDtoRequest;
import com.synoriq.synofin.collection.collectionservice.rest.request.uploadImageOnS3.UploadImageOnS3RequestDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.*;
import com.synoriq.synofin.collection.collectionservice.rest.response.UploadImageResponseDTO.UploadImageOnS3ResponseDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.userDataDTO.UsersDataDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.userDetailByTokenDTOs.UserDetailByTokenDTOResponse;
import com.synoriq.synofin.collection.collectionservice.service.utilityservice.HTTPRequestService;
import com.synoriq.synofin.events.common.interfaces.SynofinEventServiceListener;
import com.synoriq.synofin.events.template.MessageContainerTemplate;
import com.synoriq.synofin.events.template.lms.CollectionRequestActionEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.synoriq.synofin.collection.collectionservice.common.ActivityRemarks.*;

@Service
@Slf4j
public class KafkaListnerService {

    @Autowired
    private CollectionLimitUserWiseRepository collectionLimitUserWiseRepository;

    @Autowired
    private ReceiptRepository receiptRepository;

    @Autowired
    private CollectionActivityLogsRepository collectionActivityLogsRepository;

    @Transactional
    @SynofinEventServiceListener(topics = "${spring.kafka.events.topic}", groupId = "collection", containerFactory = "kafkaListenerContainerFactory")
    public void consumerTest(@Payload MessageContainerTemplate message, @Headers MessageHeaders headers) {
        try {
//            changeAnnotationValue();
            log.info("message datatatatat ->  {}", message.getMessage());
            CollectionRequestActionEvent messageObject = new ObjectMapper().convertValue(message.getMessage(), CollectionRequestActionEvent.class);
            log.info("message object, {}", messageObject);
            CollectionLimitUserWiseEntity collectionLimitUser = (CollectionLimitUserWiseEntity) collectionLimitUserWiseRepository.getCollectionLimitUserWiseByUserId(messageObject.getUserId(), messageObject.getPaymentMode());
            CollectionLimitUserWiseEntity collectionLimitUserWiseEntity = new CollectionLimitUserWiseEntity();

            List<Object> serviceRequestData = receiptRepository.getServiceRequestId(String.valueOf(messageObject.getServiceRequestId()));

            if(collectionLimitUser != null && serviceRequestData != null) {
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
            log.info(" ---------- Things acknowledged -------------");
        } catch (Exception ee) {
            ee.printStackTrace();
        }
    }
}
