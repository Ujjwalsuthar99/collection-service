package com.synoriq.synofin.collection.collectionservice.service;

import com.synoriq.synofin.collection.collectionservice.repository.TaskRepository;
import com.synoriq.synofin.collection.collectionservice.rest.response.BaseDTOResponse;
import com.synoriq.synofin.collection.collectionservice.rest.response.dummyTaskDetail.DUMMyCUST;
import com.synoriq.synofin.collection.collectionservice.rest.response.dummyTaskDetail.DummyBasicInfo;
import com.synoriq.synofin.collection.collectionservice.rest.response.dummyTaskDetail.DummyLoanDetails;
import com.synoriq.synofin.collection.collectionservice.rest.response.dummyTaskDetail.DummyResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    public BaseDTOResponse<Object> getTaskDetails(Integer pageNo, Integer pageSize) throws Exception {


        BaseDTOResponse<Object> baseDTOResponse;
        try {
            Pageable pageRequest;
            if (pageNo > 0) {
                pageNo = pageNo - 1;
            }
            pageRequest = PageRequest.of(pageNo, pageSize);
            List<Map<String, Object>> taskDetailPages = taskRepository.getTaskDetailsByPages(pageRequest);

            baseDTOResponse = new BaseDTOResponse<>(taskDetailPages);
        } catch (Exception e) {
            throw new Exception("1017002");
        }

        return baseDTOResponse;

    }

    // for task details --> wrapper binding should be called here //
    public BaseDTOResponse<Object> getTaskDetailByLoanId(Long loanId) throws Exception {


        BaseDTOResponse<Object> baseDTOResponse  = null;

        if(true) {
            DummyResponse response = new DummyResponse();
            DummyLoanDetails loanDetails = new DummyLoanDetails();
            DummyBasicInfo basicInfo = new DummyBasicInfo();
            DUMMyCUST dUMMyCUST = new DUMMyCUST();
            loanDetails.setLoanId("123");
            loanDetails.setLpp(56738.0);
            loanDetails.setBounceCharges(678.0);
            loanDetails.setLegalCharges(678.0);
            loanDetails.setEmiAmount(5679.0);
            loanDetails.setLoanId("567898");
            loanDetails.setCollectionVisitCharges(6578.0);
            basicInfo.setDob("12-06-2000");
            basicInfo.setFirstName("Ujjwal");
            basicInfo.setMiddleName("Singh");
            basicInfo.setLastName("Towar");
            basicInfo.setFullAddress("WO BABALU HARIJAN 136 HARIJAN MOHALLA SEWA JAIPUR SEWA RAJASTHAN 303008");
            dUMMyCUST.setId(1567L);
            dUMMyCUST.setBasicInfo(basicInfo);
            response.setLoanDetails(loanDetails);
            response.setCustomerDetails(Collections.singletonList(dUMMyCUST));
            baseDTOResponse = new BaseDTOResponse<>(response);
        } else {
            Map<String,Object> taskDetailPages = taskRepository.getTaskDetailsByLoanId(loanId);
            baseDTOResponse = new BaseDTOResponse<>(taskDetailPages);
        }

        List<Object> taskDetailsData;

//        baseDTOResponse = ;
        return baseDTOResponse;

    }

    public BaseDTOResponse<Object> getTaskDetailsBySearchKey(String searchKey, Integer pageNo, Integer pageSize) throws Exception {


        BaseDTOResponse<Object> baseDTOResponse;
        try {
            Pageable pageRequest;
            if (pageNo > 0) {
                pageNo = pageNo - 1;
            }
            pageRequest = PageRequest.of(pageNo, pageSize);
            List<Map<String, Object>> taskDetailPages = taskRepository.getTaskDetailsBySearchKey(searchKey, pageRequest);

            baseDTOResponse = new BaseDTOResponse<>(taskDetailPages);
        } catch (Exception e) {
            throw new Exception("1017002");
        }

        return baseDTOResponse;

    }

}
