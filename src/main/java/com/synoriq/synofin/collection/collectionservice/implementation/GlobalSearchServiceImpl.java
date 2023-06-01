package com.synoriq.synofin.collection.collectionservice.implementation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.synoriq.synofin.collection.collectionservice.common.EnumSQLConstants;
import com.synoriq.synofin.collection.collectionservice.rest.request.searchDTOs.SearchDtoRequest;
import com.synoriq.synofin.collection.collectionservice.rest.response.BaseDTOResponse;
import com.synoriq.synofin.collection.collectionservice.rest.response.GlobalSearchDTOs.LMSLoanDataDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.GlobalSearchDTOs.SearchDTOResponse;
import com.synoriq.synofin.collection.collectionservice.rest.response.GlobalSearchDTOs.SearchDTOReturnResponse;
import com.synoriq.synofin.collection.collectionservice.rest.response.GlobalSearchDTOs.TaskListDTOReturnResponse;
import com.synoriq.synofin.collection.collectionservice.service.ConsumedApiLogService;
import com.synoriq.synofin.collection.collectionservice.service.UtilityService;
import com.synoriq.synofin.collection.collectionservice.service.utilityservice.HTTPRequestService;
import com.synoriq.synofin.collection.collectionservice.service.GlobalSearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class GlobalSearchServiceImpl implements GlobalSearchService {
    @Autowired
    private ConsumedApiLogService consumedApiLogService;
    @Autowired
    private UtilityService utilityService;
    @Override
    public BaseDTOResponse<Object> getLoanDataBySearch(String token, SearchDtoRequest requestBody) throws Exception {

        BaseDTOResponse<Object> baseDTOResponse = null;
        SearchDTOResponse res;
        List<TaskListDTOReturnResponse> result = new ArrayList<>();
        SearchDTOReturnResponse searchDataResponse = new SearchDTOReturnResponse();
        SearchDtoRequest searchBody = new ObjectMapper().convertValue(requestBody, SearchDtoRequest.class);
        int stringSize = searchBody.getRequestData().getSearchTerm().length();
        String data = searchBody.getRequestData().getSearchTerm();
        //  Restrict Global Search Loan id for user with last 7 digit
        if (Objects.equals(requestBody.getRequestData().getFilterBy(), "loan_account_number")) {
            if (stringSize >= 7) {
                String search = data.substring((stringSize - 7));
                searchBody.getRequestData().setSearchTerm(search);
                searchBody.getRequestData().setFilterBy(searchBody.getRequestData().getFilterBy());
                searchBody.getRequestData().setPaginationDTO(searchBody.getRequestData().getPaginationDTO());
            } else {
                final Pattern pattern = Pattern.compile("(?=.*[A-Z])(?=.*\\d).{2,}", Pattern.CASE_INSENSITIVE);
                final Matcher matcher = pattern.matcher(data);

                if (!matcher.matches()) {
                    throw new Exception("1016034");
                }
            }
        }
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
                    .build().call();

            log.info("responseData {}", res);
            // creating api logs
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.global_search, null, searchBody, res, "success", null);
            if (res.getData() != null && res.getData().getLoanDetails() != null) {
                for (LMSLoanDataDTO loanDataDTO : res.getData().getLoanDetails()) {
                    TaskListDTOReturnResponse taskListDTOReturnResponse = new TaskListDTOReturnResponse();
                    taskListDTOReturnResponse.setAddress(loanDataDTO.getCustomerDetails().getCustomerAddress());
                    taskListDTOReturnResponse.setCustomerName(loanDataDTO.getCustomerDetails().getName());
                    taskListDTOReturnResponse.setProduct(loanDataDTO.getProduct());
                    taskListDTOReturnResponse.setLoanApplicationId(Long.parseLong(loanDataDTO.getLoanId()));
                    taskListDTOReturnResponse.setLoanApplicationNumber(loanDataDTO.getLoanApplicationNumber());
                    taskListDTOReturnResponse.setOverdueRepayment(loanDataDTO.getOverDueAmount());
                    taskListDTOReturnResponse.setBranch(loanDataDTO.getBranch());
                    taskListDTOReturnResponse.setDaysPastDue(loanDataDTO.getDpd());

                    int dpd = loanDataDTO.getDpd();
                    if (dpd >= 0 && dpd <= 30) {
                        taskListDTOReturnResponse.setDpdTextColorKey("#323232");
                        taskListDTOReturnResponse.setDpdBgColorKey("#61B2FF");
                        taskListDTOReturnResponse.setDaysPastDueBucket("0-30 DPD");
                    } else if (dpd >= 31 && dpd <= 60) {
                        taskListDTOReturnResponse.setDpdTextColorKey("#ffffff");
                        taskListDTOReturnResponse.setDpdBgColorKey("#2F80ED");
                        taskListDTOReturnResponse.setDaysPastDueBucket("31-60 DPD");
                    } else if (dpd >= 61 && dpd <= 90) {
                        taskListDTOReturnResponse.setDpdTextColorKey("#323232");
                        taskListDTOReturnResponse.setDpdBgColorKey("#FDAAAA");
                        taskListDTOReturnResponse.setDaysPastDueBucket("61-90 DPD");
                    } else if (dpd >= 91 && dpd <= 120) {
                        taskListDTOReturnResponse.setDpdTextColorKey("#323232");
                        taskListDTOReturnResponse.setDpdBgColorKey("#F2994A");
                        taskListDTOReturnResponse.setDaysPastDueBucket("91-120 DPD");
                    } else if (dpd >= 121 && dpd <= 150) {
                        taskListDTOReturnResponse.setDpdTextColorKey("#ffffff");
                        taskListDTOReturnResponse.setDpdBgColorKey("#FF5359");
                        taskListDTOReturnResponse.setDaysPastDueBucket("121-150 DPD");
                    } else if (dpd >= 151 && dpd <= 180) {
                        taskListDTOReturnResponse.setDpdTextColorKey("#ffffff");
                        taskListDTOReturnResponse.setDpdBgColorKey("#C83939");
                        taskListDTOReturnResponse.setDaysPastDueBucket("151-180 DPD");
                    } else {
                        taskListDTOReturnResponse.setDpdTextColorKey("#ffffff");
                        taskListDTOReturnResponse.setDpdBgColorKey("#722F37");
                        taskListDTOReturnResponse.setDaysPastDueBucket("180+ DPD");
                    }
                    result.add(taskListDTOReturnResponse);
                }
            } else {
                throw new Exception("1016035");
            }
            searchDataResponse.setData(result);
            baseDTOResponse = new BaseDTOResponse<>(searchDataResponse.getData());
        } catch (Exception ee) {
            String errorMessage = ee.getMessage();
            String modifiedErrorMessage = utilityService.convertToJSON(errorMessage);
            // creating api logs
            consumedApiLogService.createConsumedApiLog(EnumSQLConstants.LogNames.global_search, null, searchBody, modifiedErrorMessage, "failure", null);
            log.error("{}", ee.getMessage());
            throw new Exception(ee.getMessage());
        }
        return baseDTOResponse;
    }
}
