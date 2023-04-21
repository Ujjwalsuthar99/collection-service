package com.synoriq.synofin.collection.collectionservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.synoriq.synofin.collection.collectionservice.entity.AdditionalContactDetailsEntity;
import com.synoriq.synofin.collection.collectionservice.repository.AdditionalContactDetailsRepository;
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

    @Autowired
    private UtilityService utilityService;

    @Autowired
    private AdditionalContactDetailsRepository additionalContactDetailsRepository;

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
        LoanBasicDetailsDTOResponse loanDetailRes;

        TaskDetailRequestDTO loanDataBody = new ObjectMapper().convertValue(taskDetailRequestDTO, TaskDetailRequestDTO.class);
        TaskDetailDTOResponse resp = new TaskDetailDTOResponse();
        BaseDTOResponse<Object> baseDTOResponse = null;
        String loanId = taskDetailRequestDTO.getRequestData().getLoanId();
        Long loanIdNumber = Long.parseLong(loanId);
        try {


//            log.info("request dto details {}", taskDetailRequestDTO);
            HttpHeaders httpHeaders = new HttpHeaders();
            log.info("token {}", token);
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

            loanDetailRes = HTTPRequestService.<Object, LoanBasicDetailsDTOResponse>builder()
                    .httpMethod(HttpMethod.GET)
                    .url("http://localhost:1102/v1/getBasicLoanDetails?loanId=" + loanIdNumber)
                    .httpHeaders(httpHeaders)
                    .typeResponseType(LoanBasicDetailsDTOResponse.class)
                    .build().call();

            log.info("getBasicLoanDetails {}", loanDetailRes);

            customerRes = HTTPRequestService.<Object, CustomerDetailDTOResponse>builder()
                    .httpMethod(HttpMethod.GET)
                    .url("http://localhost:1102/v1/getCustomerDetails?loanId=" + loanIdNumber)
                    .httpHeaders(httpHeaders)
                    .typeResponseType(CustomerDetailDTOResponse.class)
                    .build().call();

            log.info("customer details {}", customerRes);

            int dpd = loanDetailRes.getData().getDpd();
            String dpdTextColor;
            String dpdBgColor;
            String dpdBucket;

            if (dpd >= 0 && dpd <= 30) {
                dpdTextColor = "#323232";
                dpdBgColor = "#61B2FF";
                dpdBucket = "0-30 DPD";
            } else if (dpd >= 31 && dpd <= 60) {
                dpdTextColor = "#ffffff";
                dpdBgColor = "#2F80ED";
                dpdBucket = "31-60 DPD";
            } else if (dpd >= 61 && dpd <= 90) {
                dpdTextColor = "#323232";
                dpdBgColor = "#FDAAAA";
                dpdBucket = "61-90 DPD";
            } else if (dpd >= 91 && dpd <= 120) {
                dpdTextColor = "#323232";
                dpdBgColor = "#F2994A";
                dpdBucket = "91-120 DPD";
            } else if (dpd >= 121 && dpd <= 150) {
                dpdTextColor = "#ffffff";
                dpdBgColor = "#FF5359";
                dpdBucket = "121-150 DPD";
            } else if (dpd >= 151 && dpd <= 180) {
                dpdTextColor = "#ffffff";
                dpdBgColor = "#C83939";
                dpdBucket = "151-180 DPD";
            } else {
                dpdTextColor = "#ffffff";
                dpdBgColor = "#722F37";
                dpdBucket = "180+ DPD";
            }

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
                    basicInfoApplicant.setDpd(dpd);
                    basicInfoApplicant.setDpdBucket(dpdBucket);
                    basicInfoApplicant.setDpdBgColor(dpdBgColor);
                    basicInfoApplicant.setDpdTextColor(dpdTextColor);
                    basicInfoApplicant.setPos(loanDetailRes.getData().getPrincipalOutstanding());
                    basicInfoApplicant.setLoanAmount(loanDetailRes.getData().getLoanAmount());
                    basicInfoApplicant.setEmiAmount(loanDetailRes.getData().getEmiAmount());
                    basicInfoApplicant.setLoanTenure(loanDetailRes.getData().getLoanTenure());
                    basicInfoApplicant.setEmiDate("Pending LMS");
                    for (CommunicationResponseDTO communicationData : customerData.getCommunication()) {
                        if (!(communicationData.getAddressType() == null)) {
                            if (communicationData.getAddressType().equals("Permanent Address")) {
                                addressReturnResponseDTO.setHomeAddress(communicationData.getFullAddress());
                                if (!Objects.equals(communicationData.getNumbers(), "")) {
                                    numbersReturnResponseDTO.setMobNo(utilityService.mobileNumberMasking(communicationData.getNumbers()));
                                }
                            } else if (communicationData.getAddressType().equals("Current Address")) {
                                addressReturnResponseDTO.setWorkAddress(communicationData.getFullAddress());
                                if (!Objects.equals(communicationData.getNumbers(), "")) {
                                    numbersReturnResponseDTO.setMobNo(utilityService.mobileNumberMasking(communicationData.getNumbers()));
                                }
                            } else if (communicationData.getAddressType().equals("Residential Address")) {
                                addressReturnResponseDTO.setResidentialAddress(communicationData.getFullAddress());
                                if (!Objects.equals(communicationData.getNumbers(), "")) {
                                    numbersReturnResponseDTO.setMobNo(utilityService.mobileNumberMasking(communicationData.getNumbers()));
                                }
                            } else {
                                if (!Objects.equals(communicationData.getNumbers(), "")) {
                                    numbersReturnResponseDTO.setAlternativeMobile(utilityService.mobileNumberMasking(communicationData.getNumbers()));
                                }
                                addressReturnResponseDTO.setHomeAddress(communicationData.getFullAddress());
                            }
                        } else {
                            if (!Objects.equals(communicationData.getNumbers(), "")) {
                                numbersReturnResponseDTO.setAlternativeMobile(utilityService.mobileNumberMasking(communicationData.getNumbers()));
                            }
                        }
                    }
                    customerDetails.setBasicInfo(basicInfoApplicant);
                    customerDetails.setAddress(addressReturnResponseDTO);
                    customerDetails.setNumbers(numbersReturnResponseDTO);
                    customerList.add(customerDetails);


                    log.info("applicantDetails {}", customerDetails);
                }
                List<AdditionalContactDetailsEntity> additionalContactDetailsEntity = additionalContactDetailsRepository.findAllByLoanId(loanIdNumber);

                if (!additionalContactDetailsEntity.isEmpty()) {
                    for (AdditionalContactDetailsEntity additionalContactDetailsEntity1 : additionalContactDetailsEntity) {
                        NumbersReturnResponseDTO numbersReturnResponseDTO1 = new NumbersReturnResponseDTO();
                        BasicInfoReturnResponseDTO basicInfoOther = new BasicInfoReturnResponseDTO();
                        numbersReturnResponseDTO1.setMobNo(utilityService.mobileNumberMasking(additionalContactDetailsEntity1.getMobileNumber().toString()));
                        if (additionalContactDetailsEntity1.getAltMobileNumber() != null) {
                            numbersReturnResponseDTO1.setAlternativeMobile(utilityService.mobileNumberMasking(additionalContactDetailsEntity1.getAltMobileNumber().toString()));
                        }
                        basicInfoOther.setRelation(additionalContactDetailsEntity1.getRelationWithApplicant());
                        basicInfoOther.setFirstName(additionalContactDetailsEntity1.getContactName());
                        CustomerDetailsReturnResponseDTO customerDetailsOther = new CustomerDetailsReturnResponseDTO();
                        customerDetailsOther.setBasicInfo(basicInfoOther);
                        customerDetailsOther.setNumbers(numbersReturnResponseDTO1);
                        customerDetailsOther.setCustomerType("other");
                        customerList.add(customerDetailsOther);
                    }
                }
            }
            log.info("customerList {}", customerList);
            loanRes.getData().setLoanBranch(loanDetailRes.getData().getSourcingBranch());
            loanRes.getData().setEmiCycle(loanDetailRes.getData().getEmiCycle());
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
