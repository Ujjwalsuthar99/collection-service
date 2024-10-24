package com.synoriq.synofin.collection.collectionservice.service.implementation;

import com.synoriq.synofin.collection.collectionservice.common.EnumSQLConstants;
import com.synoriq.synofin.collection.collectionservice.entity.ConsumedApiLogsEntity;
import com.synoriq.synofin.collection.collectionservice.repository.ConsumedApiLogRepository;
import com.synoriq.synofin.collection.collectionservice.rest.request.consumedapilogdtos.ConsumedApiLogRequestDTO;
import com.synoriq.synofin.collection.collectionservice.service.ConsumedApiLogService;
import com.synoriq.synofin.collection.collectionservice.service.UtilityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;

import java.util.Date;

@Service
@Slf4j
public class ConsumedApiLogServiceImpl implements ConsumedApiLogService {

    @Autowired
    private ConsumedApiLogRepository consumedApiLogRepository;

    @Autowired
    private UtilityService utilityService;

    @Override
    @org.springframework.transaction.annotation.Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void createConsumedApiLog(EnumSQLConstants.LogNames logName, Long userId, Object requestBody, Object responseBody, String responseStatus, Long loanId, String apiType, String endPoint) {

        log.info("create consumed log start");


        if (userId == null) {
            userId = 0L;
        }
        try {
            ConsumedApiLogRequestDTO consumedApiLogRequestDTO = new ConsumedApiLogRequestDTO();
            consumedApiLogRequestDTO.setLogName(logName);
            consumedApiLogRequestDTO.setCreatedBy(userId);
            consumedApiLogRequestDTO.setRequestBody(requestBody);
            consumedApiLogRequestDTO.setResponseData(responseBody);
            consumedApiLogRequestDTO.setResponseStatus(responseStatus);
            consumedApiLogRequestDTO.setLoanId(loanId);
            consumedApiLogRequestDTO.setApiType(apiType);
            consumedApiLogRequestDTO.setEndPoint(endPoint);

            ConsumedApiLogsEntity consumedApiLogsEntity = new ConsumedApiLogsEntity();
            consumedApiLogsEntity.setCreatedDate(new Date());
            consumedApiLogsEntity.setCreatedBy(consumedApiLogRequestDTO.getCreatedBy());
            consumedApiLogsEntity.setLogName(consumedApiLogRequestDTO.getLogName());
            consumedApiLogsEntity.setLoanId(consumedApiLogRequestDTO.getLoanId());
            consumedApiLogsEntity.setApiType(consumedApiLogRequestDTO.getApiType());
            consumedApiLogsEntity.setRequestBody(consumedApiLogRequestDTO.getRequestBody());
            consumedApiLogsEntity.setResponseData(consumedApiLogRequestDTO.getResponseData());
            consumedApiLogsEntity.setResponseStatus(consumedApiLogRequestDTO.getResponseStatus());
            consumedApiLogsEntity.setEndPoint(consumedApiLogRequestDTO.getEndPoint());

            consumedApiLogRepository.save(consumedApiLogsEntity);
        } catch (Exception ee) {
            log.error("Error in consumed Log", ee);
            log.error("Error in consumed Log Message {}", ee.getMessage());
        }
        log.info("consumed log created successfully");
    }

    public ConsumedApiLogsEntity getLastDataByLoanIdAndLogName(Long loanId, EnumSQLConstants.LogNames logName) {
        return consumedApiLogRepository.findFirstByLoanIdAndLogNameAndResponseStatusOrderByConsumedApiLogsIdDesc(loanId, logName, "success");
    }
}
