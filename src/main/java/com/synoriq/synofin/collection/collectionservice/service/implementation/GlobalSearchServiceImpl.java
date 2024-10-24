package com.synoriq.synofin.collection.collectionservice.service.implementation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.synoriq.synofin.collection.collectionservice.common.EnumSQLConstants;
import com.synoriq.synofin.collection.collectionservice.common.errorcode.ErrorCode;
import com.synoriq.synofin.collection.collectionservice.common.exception.CollectionException;
import com.synoriq.synofin.collection.collectionservice.rest.request.searchdtos.SearchDtoRequest;
import com.synoriq.synofin.collection.collectionservice.common.exception.CustomException;
import com.synoriq.synofin.collection.collectionservice.rest.response.BaseDTOResponse;
import com.synoriq.synofin.collection.collectionservice.rest.response.globalsearchdtos.LMSLoanDataDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.globalsearchdtos.SearchDTOResponse;
import com.synoriq.synofin.collection.collectionservice.rest.response.globalsearchdtos.SearchDTOReturnResponse;
import com.synoriq.synofin.collection.collectionservice.rest.response.globalsearchdtos.TaskListDTOReturnResponse;
import com.synoriq.synofin.collection.collectionservice.service.ConsumedApiLogService;
import com.synoriq.synofin.collection.collectionservice.service.GlobalSearchService;
import com.synoriq.synofin.collection.collectionservice.service.UtilityService;
import com.synoriq.synofin.collection.collectionservice.service.utilityservice.HTTPRequestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class GlobalSearchServiceImpl implements GlobalSearchService {
    private final ConsumedApiLogService consumedApiLogService;

    private final UtilityService utilityService;
    private final RestTemplate restTemplate;

    public GlobalSearchServiceImpl(ConsumedApiLogService consumedApiLogService, UtilityService utilityService, RestTemplate restTemplate){
        this.consumedApiLogService = consumedApiLogService;
        this.utilityService = utilityService;
        this.restTemplate = restTemplate;
    }
    @Override
    public BaseDTOResponse<Object> getLoanDataBySearch(String token, SearchDtoRequest requestBody) throws CustomException {

        BaseDTOResponse<Object> baseDTOResponse = null;
        SearchDTOResponse res;
        List<TaskListDTOReturnResponse> result = new ArrayList<>();
        SearchDTOReturnResponse searchDataResponse = new SearchDTOReturnResponse();
        SearchDtoRequest searchBody = new ObjectMapper().convertValue(requestBody, SearchDtoRequest.class);
        searchBody.getRequestData().setSearchTerm(searchBody.getRequestData().getSearchTerm().trim());
        //  Restrict Global Search Loan id for user with last 7 digit
        try {

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("Authorization", token);
            httpHeaders.add("Content-Type", "application/json");

            res = HTTPRequestService.<Object, SearchDTOResponse>builder()
                    .httpMethod(HttpMethod.POST)
                    .url("http://localhost:1102/v1/getLoanDataBySearch")
                    .httpHeaders(httpHeaders)
                    .body(searchBody)
                    .typeResponseType(SearchDTOResponse.class)
                    .build().call(restTemplate);

            // creating api logs
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.global_search, null, searchBody, res, "success", null, HttpMethod.POST.name(), "getLoanDataBySearch");
            if (res.getData() != null && res.getData().getLoanDetails() != null) {
                for (LMSLoanDataDTO loanDataDTO : res.getData().getLoanDetails()) {
                    TaskListDTOReturnResponse taskListDTOReturnResponse = getTaskListDTOReturnResponse(loanDataDTO);
                    result.add(taskListDTOReturnResponse);
                }
            } else {
                ErrorCode errCode = ErrorCode.getErrorCode(1016035);
                throw new CollectionException(errCode, 1016035);
            }
            searchDataResponse.setData(result);
            baseDTOResponse = new BaseDTOResponse<>(searchDataResponse.getData());
        } catch (Exception ee) {
            String errorMessage = ee.getMessage();
            String modifiedErrorMessage = utilityService.convertToJSON(errorMessage);
            // creating api logs
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.global_search, null, searchBody, modifiedErrorMessage, "failure", null, HttpMethod.POST.name(), "getLoanDataBySearch");
            log.error("{}", ee.getMessage());
            throw new CustomException(ee.getMessage());
        }
        return baseDTOResponse;
    }


    private static TaskListDTOReturnResponse getTaskListDTOReturnResponse(LMSLoanDataDTO loanDataDTO) {

        final String dpdTextColorKey1 = "#ffffff";
        final String dpdTextColorKey2 = "#323232";

        TaskListDTOReturnResponse taskListDTOReturnResponse = new TaskListDTOReturnResponse();
        taskListDTOReturnResponse.setAddress(loanDataDTO.getCustomerDetails().getCustomerAddress());
        taskListDTOReturnResponse.setCustomerName(loanDataDTO.getCustomerDetails().getName());
        taskListDTOReturnResponse.setProduct(loanDataDTO.getProduct());
        taskListDTOReturnResponse.setLoanApplicationId(Long.parseLong(loanDataDTO.getLoanId()));
        taskListDTOReturnResponse.setLoanApplicationNumber(loanDataDTO.getLoanApplicationNumber());
        taskListDTOReturnResponse.setOverdueRepayment(loanDataDTO.getOverDueAmount());
        taskListDTOReturnResponse.setBranch(loanDataDTO.getBranch());
        taskListDTOReturnResponse.setDaysPastDue(loanDataDTO.getDpd());
        taskListDTOReturnResponse.setMobile(loanDataDTO.getCustomerDetails().getPhoneNumber());

        int dpd = loanDataDTO.getDpd();
        if (dpd == 0) {
            taskListDTOReturnResponse.setDpdTextColorKey("#000000");
            taskListDTOReturnResponse.setDpdBgColorKey("#a2e890");
            taskListDTOReturnResponse.setDaysPastDueBucket("Current");
        }
        else if (dpd > 0 && dpd <= 30) {
            taskListDTOReturnResponse.setDpdTextColorKey(dpdTextColorKey2);
            taskListDTOReturnResponse.setDpdBgColorKey("#61B2FF");
            taskListDTOReturnResponse.setDaysPastDueBucket("1-30 DPD");
        } else if (dpd >= 31 && dpd <= 60) {
            taskListDTOReturnResponse.setDpdTextColorKey(dpdTextColorKey1);
            taskListDTOReturnResponse.setDpdBgColorKey("#2F80ED");
            taskListDTOReturnResponse.setDaysPastDueBucket("31-60 DPD");
        } else if (dpd >= 61 && dpd <= 90) {
            taskListDTOReturnResponse.setDpdTextColorKey(dpdTextColorKey2);
            taskListDTOReturnResponse.setDpdBgColorKey("#FDAAAA");
            taskListDTOReturnResponse.setDaysPastDueBucket("61-90 DPD");
        } else if (dpd >= 91 && dpd <= 120) {
            taskListDTOReturnResponse.setDpdTextColorKey(dpdTextColorKey2);
            taskListDTOReturnResponse.setDpdBgColorKey("#F2994A");
            taskListDTOReturnResponse.setDaysPastDueBucket("91-120 DPD");
        } else if (dpd >= 121 && dpd <= 150) {
            taskListDTOReturnResponse.setDpdTextColorKey(dpdTextColorKey1);
            taskListDTOReturnResponse.setDpdBgColorKey("#FF5359");
            taskListDTOReturnResponse.setDaysPastDueBucket("121-150 DPD");
        } else if (dpd >= 151 && dpd <= 180) {
            taskListDTOReturnResponse.setDpdTextColorKey(dpdTextColorKey1);
            taskListDTOReturnResponse.setDpdBgColorKey("#C83939");
            taskListDTOReturnResponse.setDaysPastDueBucket("151-180 DPD");
        } else {
            taskListDTOReturnResponse.setDpdTextColorKey(dpdTextColorKey1);
            taskListDTOReturnResponse.setDpdBgColorKey("#722F37");
            taskListDTOReturnResponse.setDaysPastDueBucket("180+ DPD");
        }
        return taskListDTOReturnResponse;
    }
}
