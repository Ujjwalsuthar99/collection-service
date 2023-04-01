package com.synoriq.synofin.collection.collectionservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.synoriq.synofin.collection.collectionservice.rest.request.masterDTOs.MasterDtoRequest;
import com.synoriq.synofin.collection.collectionservice.rest.request.uploadImageOnS3.UploadImageOnS3RequestDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.*;
import com.synoriq.synofin.collection.collectionservice.rest.response.UploadImageResponseDTO.UploadImageOnS3ResponseDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.userDataDTO.UsersDataDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.userDetailByTokenDTOs.UserDetailByTokenDTOResponse;
import com.synoriq.synofin.collection.collectionservice.service.utilityservice.HTTPRequestService;
import com.synoriq.synofin.events.common.interfaces.SynofinEventServiceListener;
import com.synoriq.synofin.events.template.MessageContainerTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Slf4j
public class KafkaListnerService {
    @SynofinEventServiceListener(topics = "${spring.kafka.events.topic}", groupId = "collection", containerFactory = "kafkaListenerContainerFactory")
    public void consumerTest(@Payload MessageContainerTemplate message, @Headers MessageHeaders headers) {
        try {
//            changeAnnotationValue();
            log.info("message datatatatat ->  {}", message);
            log.info(" ---------- Things acknowledged -------------");
        } catch (Exception ee) {
            ee.printStackTrace();
        }
    }
}
