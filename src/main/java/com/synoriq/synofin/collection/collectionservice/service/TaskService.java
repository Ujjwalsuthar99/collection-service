package com.synoriq.synofin.collection.collectionservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.synoriq.synofin.collection.collectionservice.repository.TaskRepository;
import com.synoriq.synofin.collection.collectionservice.rest.request.taskDetailsDTO.TaskDetailRequestDTO;
import com.synoriq.synofin.collection.collectionservice.rest.response.BaseDTOResponse;
import com.synoriq.synofin.collection.collectionservice.rest.response.TaskDetailResponseDTOs.*;
import com.synoriq.synofin.collection.collectionservice.service.utilityservice.HTTPRequestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    public BaseDTOResponse<Object> getTaskDetails(Long userId, Integer pageNo, Integer pageSize) throws Exception {


        BaseDTOResponse<Object> baseDTOResponse;
        try {
            Pageable pageRequest;
            if (pageNo > 0) {
                pageNo = pageNo - 1;
            }
            pageRequest = PageRequest.of(pageNo, pageSize);
            List<Map<String, Object>> taskDetailPages = taskRepository.getTaskDetailsByPages(userId, pageRequest);
            if (pageNo > 0) {
                if (taskDetailPages.size() == 0) {
                    return new BaseDTOResponse<>(taskDetailPages);
                }
            }
            if (!taskDetailPages.isEmpty()) {
                baseDTOResponse = new BaseDTOResponse<>(taskDetailPages);
            } else {
                throw new Exception("1016025");
            }
        } catch (Exception e) {
            throw new Exception("1017002");
        }

        return baseDTOResponse;

    }

    // for task details --> wrapper binding should be called here //
    public Object getTaskDetailByLoanId(String token, TaskDetailRequestDTO taskDetailRequestDTO) throws Exception {

        TaskDetailDTOResponse loanRes;
        CustomerDetailDTOResponse customerRes;
        TaskDetailRequestDTO loanDataBody = new ObjectMapper().convertValue(taskDetailRequestDTO, TaskDetailRequestDTO.class);
        TaskDetailDTOResponse resp = new TaskDetailDTOResponse();
        BaseDTOResponse<Object> baseDTOResponse = null;
        String loanId = taskDetailRequestDTO.getRequestData().getLoanId();
        Long loanIdNumber = Long.parseLong(loanId);
        try {
            log.info("request dto details {}", taskDetailRequestDTO);
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("Authorization", token);
            httpHeaders.add("Content-Type", "application/json");

            loanRes = HTTPRequestService.<Object, TaskDetailDTOResponse>builder()
                    .httpMethod(HttpMethod.POST)
                    .url("http://localhost:1102/v1/getDataForLoanActions")
                    .httpHeaders(httpHeaders)
                    .body(loanDataBody)
                    .typeResponseType(TaskDetailDTOResponse.class)
                    .build().call();

            log.info("loan details {}", loanRes);

//            if (loanRes) {
//
//            }

            customerRes = HTTPRequestService.<Object, CustomerDetailDTOResponse>builder()
                    .httpMethod(HttpMethod.GET)
                    .url("http://localhost:1102/v1/getCustomerDetails?loanId=" + loanIdNumber)
                    .httpHeaders(httpHeaders)
                    .typeResponseType(CustomerDetailDTOResponse.class)
                    .build().call();

            log.info("customer details {}", customerRes);

            String loanApplicationNumber = taskRepository.getLoanApplicationNumber(loanIdNumber);
            TaskDetailReturnResponseDTO response = new TaskDetailReturnResponseDTO();
            List<CustomerDetailsReturnResponseDTO> customerList = new ArrayList<>();

            if (!(customerRes.getData() == null)) {
                for (CustomerDataResponseDTO customerData : customerRes.getData()) {
                    CustomerDetailsReturnResponseDTO customerDetails = new CustomerDetailsReturnResponseDTO();
                    BasicInfoReturnResponseDTO basicInfoApplicant = new BasicInfoReturnResponseDTO();
                    AddressReturnResponseDTO addressReturnResponseDTO = new AddressReturnResponseDTO();
                    NumbersReturnResponseDTO numbersReturnResponseDTO = new NumbersReturnResponseDTO();
                    customerDetails.setId(customerData.getId());
                    customerDetails.setCustomerType(customerData.getCustomerType());
                    basicInfoApplicant.setId(customerData.getBasicInfo().getId());
                    basicInfoApplicant.setFirstName(customerData.getBasicInfo().getFirstName());
                    basicInfoApplicant.setMiddleName(customerData.getBasicInfo().getMiddleName());
                    basicInfoApplicant.setLastName(customerData.getBasicInfo().getLastName());
                    basicInfoApplicant.setDob(customerData.getBasicInfo().getDob());
                    for (CommunicationResponseDTO communicationData : customerData.getCommunication()) {
                        if (!(communicationData.getAddressType() == null)) {
                            if (communicationData.getAddressType().equals("Permanent Address")) {
                                addressReturnResponseDTO.setHomeAddress(communicationData.getFullAddress());
                            } else if (communicationData.getAddressType().equals("Current Address")) {
                                addressReturnResponseDTO.setWorkAddress(communicationData.getFullAddress());
                                numbersReturnResponseDTO.setMobNo(communicationData.getNumbers());
                            }
                        } else {
                            numbersReturnResponseDTO.setAlternativeMobile(communicationData.getNumbers());
                        }
                    }
                    customerDetails.setBasicInfo(basicInfoApplicant);
                    customerDetails.setAddress(addressReturnResponseDTO);
                    customerDetails.setNumbers(numbersReturnResponseDTO);
                    customerList.add(customerDetails);
                    log.info("applicantDetails {}", customerDetails);
                }
            }
            log.info("customerList {}", customerList);

            loanRes.getData().setLoanApplicationNumber(loanApplicationNumber);
            response.setCustomerDetails(customerList);
            response.setLoanDetails(loanRes.getData());
            baseDTOResponse = new BaseDTOResponse<>(response);
        } catch (Exception e) {
            throw new Exception("1017002");
        }
        return baseDTOResponse;

    }

    public BaseDTOResponse<Object> getTaskDetailsBySearchKey(Long userId, String searchKey, Integer pageNo, Integer pageSize) throws Exception {


        BaseDTOResponse<Object> baseDTOResponse;
        try {
            Pageable pageRequest;
            if (pageNo > 0) {
                pageNo = pageNo - 1;
            }
            pageRequest = PageRequest.of(pageNo, pageSize);
            List<Map<String, Object>> taskDetailPages = taskRepository.getTaskDetailsBySearchKey(userId, searchKey, pageRequest);
            if (pageNo > 0) {
                if (taskDetailPages.size() == 0) {
                    return new BaseDTOResponse<>(taskDetailPages);
                }
            }
            baseDTOResponse = new BaseDTOResponse<>(taskDetailPages);
        } catch (Exception e) {
            throw new Exception("1017002");
        }

        return baseDTOResponse;

    }


    public BaseDTOResponse<Object> getLoanIdsByLoanId(Long loanId) throws Exception {


        BaseDTOResponse<Object> baseDTOResponse;
        try {
            List<Object> loanIds = taskRepository.getLoanIdsByLoanId(loanId);
            baseDTOResponse = new BaseDTOResponse<>(loanIds);
        } catch (Exception e) {
            throw new Exception("1017002");
        }

        return baseDTOResponse;

    }

}
