package com.synoriq.synofin.collection.collectionservice.implementation;

import com.synoriq.synofin.collection.collectionservice.entity.LoanAllocationEntity;
import com.synoriq.synofin.collection.collectionservice.repository.LoanAllocationRepository;
import com.synoriq.synofin.collection.collectionservice.rest.request.LoanAllocationDtoRequest;
import com.synoriq.synofin.collection.collectionservice.rest.response.BaseDTOResponse;
import com.synoriq.synofin.collection.collectionservice.service.LoanAllocationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class LoanAllocationServiceImpl implements LoanAllocationService {

    @Autowired
    private LoanAllocationRepository loanAllocationRepository;
    @Override
    public BaseDTOResponse<Object> createLoanAllocationByAllocatedToUserId(LoanAllocationDtoRequest loanAllocationDtoRequest) throws Exception {

        BaseDTOResponse<Object> baseResponse;
        try {

            List<Object> loanDetail;
            List<Object> duplicate = new ArrayList<>();
            List<Object> savedLoans = new ArrayList<>();
            Map<String, List<Object>> responseLoans = new HashMap<>();

            LoanAllocationEntity loanAllocationEntity = null;
            for (Long loanId : loanAllocationDtoRequest.getLoanId()) {
                log.info("loan id from iteration {}", loanId);
                loanDetail = loanAllocationRepository.getLoansByLoanId(loanId);
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

            responseLoans.put("savedLoans", savedLoans);
            responseLoans.put("alreadyExists",duplicate);


            baseResponse = new BaseDTOResponse<>(responseLoans);
        } catch (Exception ee) {
            throw new Exception(ee);
        }
        return baseResponse;
    }
    @Override
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