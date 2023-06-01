package com.synoriq.synofin.collection.collectionservice.implementation;

import com.synoriq.synofin.collection.collectionservice.common.EnumSQLConstants;
import com.synoriq.synofin.collection.collectionservice.entity.ConsumedApiLogsEntity;
import com.synoriq.synofin.collection.collectionservice.repository.ConsumedApiLogRepository;
import com.synoriq.synofin.collection.collectionservice.rest.request.consumedApiLogDTOs.ConsumedApiLogRequestDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.UserDetailByTokenDTOs.UserDetailByTokenDTOResponse;
import com.synoriq.synofin.collection.collectionservice.service.ConsumedApiLogService;
import com.synoriq.synofin.collection.collectionservice.service.UtilityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Service
@Slf4j
public class ConsumedApiLogServiceImpl implements ConsumedApiLogService {

    @Autowired
    private ConsumedApiLogRepository consumedApiLogRepository;

    @Autowired
    private HttpServletRequest httpServletRequest;

    @Autowired
    private UtilityService utilityService;

    @Override
    public void createConsumedApiLog(EnumSQLConstants.LogNames logName, Long userId, Object requestBody, Object responseBody, String responseStatus, Long loanId) {

        String apiType = httpServletRequest.getMethod();
        String endPoint = utilityService.getApiUrl();

        if (userId == null) {
            userId = 0L;
        }

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
    }
}
