package com.synoriq.synofin.collection.collectionservice.implementation;

import com.synoriq.synofin.collection.collectionservice.entity.LoanAllocationEntity;
import com.synoriq.synofin.collection.collectionservice.repository.LoanAllocationRepository;
import com.synoriq.synofin.collection.collectionservice.rest.request.loanAllocationDTOs.LoanAllocationDtoRequest;
import com.synoriq.synofin.collection.collectionservice.rest.request.loanAllocationDTOs.LoanAllocationMultiUsersDtoRequest;
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

            for (Long loanId : loanAllocationDtoRequest.getLoanId()) {
                loanDetail = loanAllocationRepository.getLoansByLoanIdAndDeleted(loanId, false);
                if(!loanDetail.isEmpty()) {
                    duplicate.add(loanId);
                    continue;
                }

                LoanAllocationEntity loanAllocationEntity = new LoanAllocationEntity();

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
            loanAllocationEntities = loanAllocationRepository.getLoansByAllocatedToUserIdAndDeleted(allocatedToUserId, false);
        } catch (Exception e) {
            throw new Exception("1016028");
        }
        return loanAllocationEntities;
    }

    @Override
    public List<Map<String, Object>> getAllocatedUsersByLoanId(Long loanId) throws Exception {
        List<Map<String, Object>> loanAllocationEntities;
        try {
            loanAllocationEntities = loanAllocationRepository.getAllocatedToUserIdsByLoanIdAndDeleted(loanId);
        } catch (Exception e) {
            throw new Exception("1016028");
        }
        return loanAllocationEntities;
    }

    @Override
    public BaseDTOResponse<Object> createLoanAllocationToMultipleUserId(LoanAllocationMultiUsersDtoRequest loanAllocationMultiUsersDtoRequest) throws Exception {

        List<Long> allocatedUserIds = loanAllocationMultiUsersDtoRequest.getAllocatedToUserId();
        List<Long> removeUserIds = loanAllocationMultiUsersDtoRequest.getRemovedUserId();

        if(removeUserIds.size() > 0) {
            for (Long removeUserId: removeUserIds) {
                LoanAllocationEntity loanAllocation = loanAllocationRepository.findByAllocatedToUserIdAndLoanIdAndDeleted(removeUserId, loanAllocationMultiUsersDtoRequest.getLoanId(), false);
                if (loanAllocation != null && !loanAllocation.getDeleted()) {
                    loanAllocation.setDeleted(true);
                    loanAllocationRepository.save(loanAllocation);
                }
            }
        }

        if(allocatedUserIds.size() > 0) {
            for (Long userId : allocatedUserIds) {
                List<LoanAllocationEntity> loanAllocationList = loanAllocationRepository.findByAllocatedToUserIdAndLoanId(userId, loanAllocationMultiUsersDtoRequest.getLoanId());
                for (LoanAllocationEntity loanAllocation : loanAllocationList) {
                    if (loanAllocation == null) {
                        LoanAllocationEntity loanAllocationEntity = new LoanAllocationEntity();
                        loanAllocationEntity.setCreatedDate(new Date());
                        loanAllocationEntity.setCreatedBy(loanAllocationMultiUsersDtoRequest.getCreatedBy());
                        loanAllocationEntity.setDeleted(false);
                        loanAllocationEntity.setLoanId(loanAllocationMultiUsersDtoRequest.getLoanId());
                        loanAllocationEntity.setAllocatedToUserId(userId);
                        loanAllocationRepository.save(loanAllocationEntity);
                    } else if (loanAllocation.getDeleted()) {
                        loanAllocation.setCreatedDate(new Date());
                        loanAllocation.setCreatedBy(loanAllocationMultiUsersDtoRequest.getCreatedBy());
                        loanAllocation.setDeleted(false);
                        loanAllocation.setLoanId(loanAllocationMultiUsersDtoRequest.getLoanId());
                        loanAllocation.setAllocatedToUserId(userId);
                        loanAllocationRepository.save(loanAllocation);
                    }
                }
            }
        }
        return new BaseDTOResponse<>("Data Saved Successfully");
    }

}