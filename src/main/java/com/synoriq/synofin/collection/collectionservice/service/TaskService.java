package com.synoriq.synofin.collection.collectionservice.service;

import com.synoriq.synofin.collection.collectionservice.repository.TaskRepository;
import com.synoriq.synofin.collection.collectionservice.rest.response.BaseDTOResponse;
import com.synoriq.synofin.collection.collectionservice.rest.response.dummyTaskDetail.DUMMyCUST;
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
            loanDetails.setId("12121");
            response.setLoanDetails(loanDetails);
            DUMMyCUST dUMMyCUST = new DUMMyCUST();
            dUMMyCUST.setId("1234");
            response.setCustomerDetails((List<DUMMyCUST>) dUMMyCUST);
            baseDTOResponse = new BaseDTOResponse<>(response);
        } else {
            Map<String,Object> taskDetailPages = taskRepository.getTaskDetailsByLoanId(loanId);
            baseDTOResponse = new BaseDTOResponse<>(taskDetailPages);
        }

        List<Object> taskDetailsData;

//        baseDTOResponse = ;
        return baseDTOResponse;

    }

}
