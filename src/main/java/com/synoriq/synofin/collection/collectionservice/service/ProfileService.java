package com.synoriq.synofin.collection.collectionservice.service;


import com.synoriq.synofin.collection.collectionservice.repository.TaskRepository;
import com.synoriq.synofin.collection.collectionservice.rest.response.BaseDTOResponse;
import com.synoriq.synofin.collection.collectionservice.rest.response.DummyProfileDetailDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ProfileService {

    public BaseDTOResponse<Object> getProfileDetails(Long username) throws Exception {


        BaseDTOResponse<Object> baseDTOResponse = null;
        try {

            if (true) {
                DummyProfileDetailDTO dummyProfileDetailDTO = new DummyProfileDetailDTO();
                dummyProfileDetailDTO.setName("Ujwal Suthar");
                dummyProfileDetailDTO.setMobile("8107767383");
                dummyProfileDetailDTO.setBranch("Jaipur");
                dummyProfileDetailDTO.setEmail("ujwal.suthar@gmail.com");

                baseDTOResponse = new BaseDTOResponse<>(dummyProfileDetailDTO);
            } else {
//                Map<String,Object> taskDetailPages = taskRepository.getTaskDetailsByLoanId(loanId);
                baseDTOResponse = new BaseDTOResponse<>(baseDTOResponse);
            }
        } catch (Exception e) {
            throw new Exception("1017002");
        }

        return baseDTOResponse;

    }
}
