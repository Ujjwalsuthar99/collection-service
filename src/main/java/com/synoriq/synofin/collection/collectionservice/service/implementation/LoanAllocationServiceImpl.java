package com.synoriq.synofin.collection.collectionservice.service.implementation;

import com.synoriq.synofin.collection.collectionservice.common.errorcode.ErrorCode;
import com.synoriq.synofin.collection.collectionservice.common.exception.CollectionException;
import com.synoriq.synofin.collection.collectionservice.common.exception.CustomException;
import com.synoriq.synofin.collection.collectionservice.entity.LoanAllocationEntity;
import com.synoriq.synofin.collection.collectionservice.repository.LoanAllocationRepository;
import com.synoriq.synofin.collection.collectionservice.rest.request.loanallocationdtos.LoanAllocationDtoRequest;
import com.synoriq.synofin.collection.collectionservice.rest.request.loanallocationdtos.LoanAllocationMultiUsersDtoRequest;
import com.synoriq.synofin.collection.collectionservice.rest.response.BaseDTOResponse;
import com.synoriq.synofin.collection.collectionservice.service.LoanAllocationService;
import com.synoriq.synofin.collection.collectionservice.service.UtilityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

@Service
@Slf4j
public class LoanAllocationServiceImpl implements LoanAllocationService {

    private static final String EXCEP_CODE = "1016028";
    private final UtilityService utilityService;

    private final LoanAllocationRepository loanAllocationRepository;

    public LoanAllocationServiceImpl(UtilityService utilityService, LoanAllocationRepository loanAllocationRepository) {
        this.utilityService = utilityService;
        this.loanAllocationRepository = loanAllocationRepository;
    }

    @Override
    public BaseDTOResponse<Object> createLoanAllocationByAllocatedToUserId(
            LoanAllocationDtoRequest loanAllocationDtoRequest) throws CustomException {

        BaseDTOResponse<Object> baseResponse;
        try {

            List<Object> loanDetail;
            List<Object> duplicate = new ArrayList<>();
            List<Object> savedLoans = new ArrayList<>();
            Map<String, List<Object>> responseLoans = new HashMap<>();

            for (Long loanId : loanAllocationDtoRequest.getLoanId()) {
                loanDetail = loanAllocationRepository.getLoansByLoanIdAndDeleted(loanId, false);
                if (!loanDetail.isEmpty()) {
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
            responseLoans.put("alreadyExists", duplicate);

            baseResponse = new BaseDTOResponse<>(responseLoans);
        } catch (Exception ee) {
            throw new CustomException(ee.getMessage());
        }
        return baseResponse;
    }

    @Override
    public List<LoanAllocationEntity> getLoansByUserId(Long allocatedToUserId) throws CollectionException {
        List<LoanAllocationEntity> loanAllocationEntities;
        try {
            loanAllocationEntities = loanAllocationRepository.getLoansByAllocatedToUserIdAndDeleted(allocatedToUserId,
                    false);
        } catch (Exception e) {
            ErrorCode errCode = ErrorCode.getErrorCode(Integer.valueOf(EXCEP_CODE));
            throw new CollectionException(errCode, Integer.valueOf(EXCEP_CODE));
        }
        return loanAllocationEntities;
    }

    @Override
    public List<Map<String, Object>> getAllocatedUsersByLoanId(Long loanId) throws CollectionException {
        List<Map<String, Object>> loanAllocationEntities;
        try {
            loanAllocationEntities = loanAllocationRepository.getAllocatedToUserIdsByLoanIdAndDeleted(loanId);
        } catch (Exception e) {
            ErrorCode errCode = ErrorCode.getErrorCode(Integer.valueOf(EXCEP_CODE));
            throw new CollectionException(errCode, Integer.valueOf(EXCEP_CODE));
        }
        return loanAllocationEntities;
    }

    @Override
    public BaseDTOResponse<Object> createLoanAllocationToMultipleUserId(
            LoanAllocationMultiUsersDtoRequest loanAllocationMultiUsersDtoRequest) throws CustomException {

        try {
            List<Long> allocatedUserIds = loanAllocationMultiUsersDtoRequest.getAllocatedToUserId();
            List<Long> removeUserIds = loanAllocationMultiUsersDtoRequest.getRemovedUserId();
            if (!removeUserIds.isEmpty()) {
                for (Long removeUserId : removeUserIds) {
                    LoanAllocationEntity loanAllocation = loanAllocationRepository
                            .findByAllocatedToUserIdAndLoanIdAndDeleted(removeUserId,
                                    loanAllocationMultiUsersDtoRequest.getLoanId(), false);
                    if (loanAllocation != null && !loanAllocation.getDeleted()) {
                        loanAllocation.setDeleted(true);
                        loanAllocationRepository.save(loanAllocation);
                    }
                }
            }

            if (!allocatedUserIds.isEmpty()) {
                for (Long userId : allocatedUserIds) {
                    List<LoanAllocationEntity> loanAllocationList = loanAllocationRepository
                            .findByAllocatedToUserIdAndLoanId(userId, loanAllocationMultiUsersDtoRequest.getLoanId());
                    if (!loanAllocationList.isEmpty()) {
                        for (LoanAllocationEntity loanAllocation : loanAllocationList) {
                            this.createLoanAllocationDetails(loanAllocation, loanAllocationMultiUsersDtoRequest,
                                    userId);
                        }
                    } else {
                        LoanAllocationEntity loanAllocationEntity = new LoanAllocationEntity();
                        loanAllocationEntity.setCreatedDate(new Date());
                        loanAllocationEntity.setCreatedBy(loanAllocationMultiUsersDtoRequest.getCreatedBy());
                        loanAllocationEntity.setDeleted(false);
                        loanAllocationEntity.setLoanId(loanAllocationMultiUsersDtoRequest.getLoanId());
                        loanAllocationEntity.setTaskPurpose(loanAllocationMultiUsersDtoRequest.getTaskPurpose());
                        loanAllocationEntity.setAllocatedToUserId(userId);
                        loanAllocationRepository.save(loanAllocationEntity);
                    }
                }
            }
            return new BaseDTOResponse<>("Data Saved Successfully");
        } catch (Exception ee) {
            throw new CustomException(ee.getMessage());
        }
    }

    @Override
    @Transactional
    public String deleteAllAllocatedLoans(Date fromDate, Date toDate) throws CollectionException {
        try {
            toDate = utilityService.addOneDay(toDate);
            loanAllocationRepository.deleteByCreatedDateBetween(fromDate, toDate);
        } catch (Exception e) {
            ErrorCode errCode = ErrorCode.getErrorCode(Integer.valueOf(EXCEP_CODE));
            throw new CollectionException(errCode, Integer.valueOf(EXCEP_CODE));
        }
        return "Data Deleted Successfully";
    }

    public void createLoanAllocationDetails(
            LoanAllocationEntity loanAllocation,
            LoanAllocationMultiUsersDtoRequest loanAllocationMultiUsersDtoRequest,
            Long userId) {
        if (loanAllocation == null) {
            LoanAllocationEntity loanAllocationEntity = new LoanAllocationEntity();
            loanAllocationEntity.setCreatedDate(new Date());
            loanAllocationEntity.setCreatedBy(loanAllocationMultiUsersDtoRequest.getCreatedBy());
            loanAllocationEntity.setDeleted(false);
            loanAllocationEntity.setLoanId(loanAllocationMultiUsersDtoRequest.getLoanId());
            loanAllocationEntity.setAllocatedToUserId(userId);
            loanAllocationEntity.setTaskPurpose(loanAllocationMultiUsersDtoRequest.getTaskPurpose());
            loanAllocationRepository.save(loanAllocationEntity);
        } else {
            loanAllocation.setCreatedDate(new Date());
            loanAllocation.setCreatedBy(loanAllocationMultiUsersDtoRequest.getCreatedBy());
            loanAllocation.setDeleted(false);
            loanAllocation.setLoanId(loanAllocationMultiUsersDtoRequest.getLoanId());
            loanAllocation.setTaskPurpose(loanAllocationMultiUsersDtoRequest.getTaskPurpose());
            loanAllocation.setAllocatedToUserId(userId);
            loanAllocationRepository.save(loanAllocation);
        }
    }

}