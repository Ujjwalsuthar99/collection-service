package com.synoriq.synofin.collection.collectionservice.service;

import com.synoriq.synofin.collection.collectionservice.common.errorcode.ErrorCode;
import com.synoriq.synofin.collection.collectionservice.entity.LoanAllocationEntity;
import com.synoriq.synofin.collection.collectionservice.entity.ReceiptTransferEntity;
import com.synoriq.synofin.collection.collectionservice.entity.ReceiptTransferHistoryEntity;
import com.synoriq.synofin.collection.collectionservice.repository.LoanAllocationRepository;
import com.synoriq.synofin.collection.collectionservice.rest.request.LoanAllocationDtoRequest;
import com.synoriq.synofin.collection.collectionservice.rest.request.ReceiptTransferDtoRequest;
import com.synoriq.synofin.collection.collectionservice.rest.response.BaseDTOResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.lang.reflect.Array;
import java.util.*;

@Service
@Slf4j
public class LoanAllocationService {

    @Autowired
    private LoanAllocationRepository loanAllocationRepository;


    public BaseDTOResponse<Object> createLoanAllocationByAllocatedToUserId(@RequestBody LoanAllocationDtoRequest loanAllocationDtoRequest) throws Exception {

        BaseDTOResponse<Object> baseResponse;
        ResponseEntity<Object> response = null;
        try {

            List<Object> loanDetail;
            List<Object> duplicate = new ArrayList<>();
            List<Object> savedLoans = new ArrayList<Object>();
            Map<String, List<Object>> responseLoans = new HashMap<>();

            LoanAllocationEntity loanAllocationEntity = null;
            for (Long loanId : loanAllocationDtoRequest.getLoanId()) {
                log.info("loan id from iteration {}", loanId);
                loanDetail = loanAllocationRepository.getLoansByLoanId(loanId);
                log.info("loan id from database {}", loanDetail.isEmpty());
                if(!loanDetail.isEmpty()) {
                    duplicate.add(loanId);
                    continue;
                }

                loanAllocationEntity = new LoanAllocationEntity();

                loanAllocationEntity.setCreatedDate(new Date());
                loanAllocationEntity.setCreatedBy(loanAllocationDtoRequest.getCreatedBy());
                loanAllocationEntity.setDeleted(loanAllocationDtoRequest.getDeleted());
                loanAllocationEntity.setLoanId(loanId);
                loanAllocationEntity.setAllocatedToUserId(loanAllocationDtoRequest.getAllocatedToUserId());
                loanAllocationRepository.save(loanAllocationEntity);
                savedLoans.add(loanAllocationEntity);
            }
            log.info("savedLoans [{}]", savedLoans.toString());
            log.info("duplicate {}", duplicate.toString());

            responseLoans.put("savedLoans", savedLoans);
            responseLoans.put("alreadyExists",duplicate);


            baseResponse = new BaseDTOResponse<Object>(responseLoans);
        } catch (Exception ee) {
            throw new Exception(ee);
        }
        return baseResponse;
    }


    public List<LoanAllocationEntity> getLoansByUserId(Long allocatedToUserId) throws Exception {
        List<LoanAllocationEntity> loanAllocationEntities;
        try {
            loanAllocationEntities = loanAllocationRepository.getLoansByAllocatedToUserId(allocatedToUserId);
        } catch (Exception e) {
            throw new Exception("1016028");
        }
        return loanAllocationEntities;
    }

}