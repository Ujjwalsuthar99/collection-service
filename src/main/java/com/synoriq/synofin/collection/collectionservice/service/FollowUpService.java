package com.synoriq.synofin.collection.collectionservice.service;

import com.synoriq.synofin.collection.collectionservice.entity.FollowUpEntity;
import com.synoriq.synofin.collection.collectionservice.repository.FollowUpRepository;
import com.synoriq.synofin.lms.commondto.dto.collection.FollowUpDTO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class FollowUpService {
    private FollowUpRepository followUpRepository;

    public List<FollowUpDTO> getFollowUpByLoanId (Long loanId) {
        List<FollowUpEntity> followUpEntityList = followUpRepository.findByLoanId(loanId);
        FollowUpDTO followUpDTO;
        List<FollowUpDTO> followUpList = new ArrayList<>();
        for (FollowUpEntity followUpEntity : followUpEntityList) {
            followUpDTO = new FollowUpDTO();
            followUpDTO.setFollowUpId(followUpEntity.getFollowUpId());
            followUpDTO.setLoanId(followUpEntity.getLoanId());
            followUpDTO.setCreatedDate(followUpEntity.getCreatedDate());
            followUpDTO.setCreatedBy(followUpEntity.getCreatedBy());
            followUpDTO.setFollowUpReason(followUpEntity.getFollowUpReason());
            followUpDTO.setFollowUpDateTime(followUpEntity.getFollowUpDateTime());
            followUpList.add(followUpDTO);
        }
        return followUpList;
    }
}
