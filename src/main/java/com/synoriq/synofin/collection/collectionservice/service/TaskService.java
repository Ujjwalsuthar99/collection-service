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
        List<DUMMyCUST> dumMyCUSTList = new ArrayList<>();

        if(true) {
            DummyResponse response = new DummyResponse();
            DummyLoanDetails loanDetails = new DummyLoanDetails();
            DummyBasicInfo basicInfoApp = new DummyBasicInfo();
            DummyBasicInfo basicInfoCoApp = new DummyBasicInfo();
            DUMMyCUST dUMMyCUSTapp = new DUMMyCUST();
            DUMMyCUST dUMMyCUSTcoapp = new DUMMyCUST();
            loanDetails.setLoanId("123");
            loanDetails.setLpp(500.0);
            loanDetails.setLegalCharges(1100.0);
            loanDetails.setEmiAmount(10000.0);
            loanDetails.setChequeBounceCharges(1180.0);
            loanDetails.setLoanId("567898");
            loanDetails.setCollectionVisitCharges(1100.0);
            loanDetails.setVisitCharges(1100.0);
            loanDetails.setTotalDueAmount(14980.0);
            loanDetails.setPos(14500);
            loanDetails.setEmiStartDate("2023-08-23");
            basicInfoApp.setDob("12-06-2000");
            basicInfoApp.setFirstName("Ujjwal");
            basicInfoApp.setMiddleName("Singh");
            basicInfoApp.setLastName("Towar");
            basicInfoApp.setHomeAddress("WO BABALU HARIJAN 136 HARIJAN MOHALLA SEWA JAIPUR SEWA RAJASTHAN 303008");
            basicInfoApp.setWorkAddress("3rd Floor, Omkaram Tower, Hanuman Nagar, Vaishali Nagar, Jaipur 302021");
            basicInfoCoApp.setMobNo("9767688998");
            basicInfoApp.setAlternativeMobile("8767878990");
            basicInfoApp.setId(12789);
            basicInfoApp.setMobileSpouse("6578766788");
            dUMMyCUSTapp.setId(1567L);
            dUMMyCUSTapp.setCustomerType("applicant");
            dUMMyCUSTapp.setBasicInfo(basicInfoApp);
//

            dumMyCUSTList.add(dUMMyCUSTapp);
            dUMMyCUSTcoapp.setId(1568L);
            dUMMyCUSTcoapp.setCustomerType("co-applicant");
            basicInfoCoApp.setId(12349);
            basicInfoCoApp.setDob("11-05-2000");
            basicInfoCoApp.setFirstName("Sompalle");
            basicInfoCoApp.setMiddleName("");
            basicInfoCoApp.setLastName("Guna");
            basicInfoCoApp.setHomeAddress("SO Somaplle LalSingh 145 Mansorovar JAIPUR RAJASTHAN 303014");
            basicInfoCoApp.setWorkAddress("SO Sompalle Sunil 14 Vaishali Nagar JAIPUR RAJASTHAN 302056");
            basicInfoCoApp.setAlternativeMobile("8767878990");
            basicInfoCoApp.setMobileSpouse("6578766788");
            basicInfoCoApp.setMobNo("8108378326");
            dUMMyCUSTcoapp.setBasicInfo(basicInfoCoApp);
            dumMyCUSTList.add(dUMMyCUSTcoapp);
            response.setLoanDetails(loanDetails);
            response.setCustomerDetails(dumMyCUSTList);
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
