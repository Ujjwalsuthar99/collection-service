package com.synoriq.synofin.collection.collectionservice.service;


import com.synoriq.synofin.collection.collectionservice.entity.CollectionConfigurationsEntity;
import com.synoriq.synofin.collection.collectionservice.repository.CollectionConfigurationsRepository;
import com.synoriq.synofin.collection.collectionservice.repository.ReceiptRepository;
import com.synoriq.synofin.collection.collectionservice.rest.request.CollectionConfigurationDtoRequest;
import com.synoriq.synofin.collection.collectionservice.rest.response.BaseDTOResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ReceiptService {

    @Autowired
    ReceiptRepository receiptRepository;

    public BaseDTOResponse<Object> getReceiptsByUserIdWithDuration(Long userId, String fromDate, String toDate, String status, String paymentMode) throws Exception {


        BaseDTOResponse<Object> baseDTOResponse;
        try {
            paymentMode = paymentMode == "" ? paymentMode = "cash" : paymentMode;
//            Pageable pageRequest;
//            if (pageNo > 0) {
//                pageNo = pageNo - 1;
//            }
//            pageRequest = PageRequest.of(pageNo, pageSize);
            List<Map<String, Object>> taskDetailPages = receiptRepository.getReceiptsByUserIdWithDuration(userId.toString(), fromDate, toDate);

            baseDTOResponse = new BaseDTOResponse<>(taskDetailPages);
        } catch (Exception e) {
            throw new Exception("1017002");
        }

        return baseDTOResponse;

    }

    public BaseDTOResponse<Object> getReceiptsByLoanIdWithDuration(Long loanId, String fromDate, String toDate, String status, String paymentMode) throws Exception {


        BaseDTOResponse<Object> baseDTOResponse;
        try {
            paymentMode = paymentMode == "" ? paymentMode = "cash" : paymentMode;
//            Pageable pageRequest;
//            if (pageNo > 0) {
//                pageNo = pageNo - 1;
//            }
//            pageRequest = PageRequest.of(pageNo, pageSize);
            List<Map<String, Object>> taskDetailPages = receiptRepository.getReceiptsByLoanIdWithDuration(loanId, fromDate, toDate);

            baseDTOResponse = new BaseDTOResponse<>(taskDetailPages);
        } catch (Exception e) {
            throw new Exception("1017002");
        }

        return baseDTOResponse;

    }
}
