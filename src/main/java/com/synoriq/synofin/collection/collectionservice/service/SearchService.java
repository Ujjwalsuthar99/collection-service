package com.synoriq.synofin.collection.collectionservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.synoriq.synofin.collection.collectionservice.rest.request.searchDTOs.SearchDtoRequest;
import com.synoriq.synofin.collection.collectionservice.rest.response.BaseDTOResponse;
import com.synoriq.synofin.collection.collectionservice.rest.response.globalSearchDTOs.LMSLoanDataDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.globalSearchDTOs.SearchDTOResponse;
import com.synoriq.synofin.collection.collectionservice.rest.response.globalSearchDTOs.SearchDTOReturnResponse;
import com.synoriq.synofin.collection.collectionservice.rest.response.globalSearchDTOs.TaskListDTOReturnResponse;
import com.synoriq.synofin.collection.collectionservice.service.utilityservice.HTTPRequestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class SearchService {

    public BaseDTOResponse<Object> getLoanDataBySearch(String token, SearchDtoRequest requestBody) throws Exception {

        BaseDTOResponse<Object> baseDTOResponse = null;
        SearchDTOResponse res;
        List<TaskListDTOReturnResponse> result = new ArrayList<>();
        SearchDTOReturnResponse searchDataResponse = new SearchDTOReturnResponse();
        SearchDtoRequest searchBody = new ObjectMapper().convertValue(requestBody, SearchDtoRequest.class);
        int stringSize= searchBody.getRequestData().getSearchTerm().length();
       //  Restrict Global Search Loan id for user with last 7 digit
        if (stringSize > 7) {
            String data = searchBody.getRequestData().getSearchTerm();
            String search = data.substring((stringSize- 7));
            searchBody.getRequestData().setSearchTerm(search);
            searchBody.getRequestData().setFilterBy(searchBody.getRequestData().getFilterBy());
            searchBody.getRequestData().setPaginationDTO(searchBody.getRequestData().getPaginationDTO());
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
            for(LMSLoanDataDTO loanDataDTO : res.getData().getLoanDetails()) {
                TaskListDTOReturnResponse taskListDTOReturnResponse = new TaskListDTOReturnResponse();
                taskListDTOReturnResponse.setAddress("Incoming From LMS soon");
                taskListDTOReturnResponse.setCustomerName(loanDataDTO.getCustomerDetails().getName());
                taskListDTOReturnResponse.setProduct("IncomingLMS");
                taskListDTOReturnResponse.setLoanApplicationId(Long.parseLong(loanDataDTO.getLoanId()));
                taskListDTOReturnResponse.setLoanApplicationNumber(loanDataDTO.getLoanApplicationNumber());
                taskListDTOReturnResponse.setOverdueRepayment(0L);
                taskListDTOReturnResponse.setDaysPastDue(0L);

                int dpd = Integer.parseInt((loanDataDTO.getLoanId()));
                if (dpd >= 0 && dpd <= 30) {
                    taskListDTOReturnResponse.setDpdTextColorKey("#323232");
                    taskListDTOReturnResponse.setDpdBgColorKey("#ABCFFF");
                    taskListDTOReturnResponse.setDaysPastDueBucket("0-30 DPD");
                } else if (dpd >= 31 && dpd <= 60) {
                    taskListDTOReturnResponse.setDpdTextColorKey("#323232");
                    taskListDTOReturnResponse.setDpdBgColorKey("#FDB4FF");
                    taskListDTOReturnResponse.setDaysPastDueBucket("31-60 DPD");
                } else if (dpd >= 61 && dpd <= 90) {
                    taskListDTOReturnResponse.setDpdTextColorKey("#323232");
                    taskListDTOReturnResponse.setDpdBgColorKey("#FDAAAA");
                    taskListDTOReturnResponse.setDaysPastDueBucket("61-90 DPD");
                } else if (dpd >= 91 && dpd <= 120) {
                    taskListDTOReturnResponse.setDpdTextColorKey("#323232");
                    taskListDTOReturnResponse.setDpdBgColorKey("#FCDA8B");
                    taskListDTOReturnResponse.setDaysPastDueBucket("91-120 DPD");
                } else if (dpd >= 121 && dpd <= 150) {
                    taskListDTOReturnResponse.setDpdTextColorKey("#323232");
                    taskListDTOReturnResponse.setDpdBgColorKey("#F2994A");
                    taskListDTOReturnResponse.setDaysPastDueBucket("121-150 DPD");
                } else if (dpd >= 151 && dpd <= 180) {
                    taskListDTOReturnResponse.setDpdTextColorKey("#ffffff");
                    taskListDTOReturnResponse.setDpdBgColorKey("#FF5359");
                    taskListDTOReturnResponse.setDaysPastDueBucket("151-180 DPD");
                } else {
                    taskListDTOReturnResponse.setDpdTextColorKey("#ffffff");
                    taskListDTOReturnResponse.setDpdBgColorKey("#F9000A");
                    taskListDTOReturnResponse.setDaysPastDueBucket("180++ DPD");
                }
                result.add(taskListDTOReturnResponse);
            }
            searchDataResponse.setData(result);
            baseDTOResponse = new BaseDTOResponse<>(searchDataResponse.getData());
        } catch (Exception ee) {
//            ee.printStackTrace();
            log.error("{}", ee.getMessage());
        }
        return baseDTOResponse;
    }
}
