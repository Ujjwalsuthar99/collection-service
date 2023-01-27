package com.synoriq.synofin.collection.collectionservice.service;

import com.synoriq.synofin.collection.collectionservice.entity.FollowUpEntity;
import com.synoriq.synofin.collection.collectionservice.repository.FollowUpRepository;
import com.synoriq.synofin.collection.collectionservice.rest.request.FollowUpDtoRequest;
import com.synoriq.synofin.lms.commondto.dto.collection.FollowUpDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class FollowUpService {

    @Autowired
    private FollowUpRepository followUpRepository;

    public List<FollowUpDTO> getFollowUpByLoanId (Long loanId) {
        List<FollowUpEntity> followUpEntityList = followUpRepository.findByLoanId(loanId);
        FollowUpDTO followUpDTO;
        List<FollowUpDTO> followUpList = new ArrayList<>();
        for (FollowUpEntity followUpEntity : followUpEntityList) {
            followUpDTO = new FollowUpDTO();
//            followUpDTO.setFollowUpId(followUpEntity.getFollowUpId());
            followUpDTO.setLoanId(followUpEntity.getLoanId());
            followUpDTO.setCreatedDate(followUpEntity.getCreatedDate());
            followUpDTO.setCreatedBy(followUpEntity.getCreatedBy());
            followUpDTO.setFollowUpReason(followUpEntity.getFollowUpReason());
            followUpDTO.setFollowUpDateTime(followUpEntity.getFollowUpDateTime());
            followUpDTO.setFollowUpOtherReason(followUpEntity.getOtherFollowUpReason());
            followUpDTO.setRemarks(followUpEntity.getRemarks());
            followUpList.add(followUpDTO);
        }
        return followUpList;
    }


    public List<FollowUpDTO> getFollowUpByUserId(Long userId) {
        List<FollowUpEntity> followUpEntityList = followUpRepository.findByCreatedBy(userId);
        FollowUpDTO followUpDTO;
        List<FollowUpDTO> followUpList = new ArrayList<>();
        for (FollowUpEntity followUpEntity : followUpEntityList) {
            followUpDTO = new FollowUpDTO();
//            followUpDTO.setFollowUpId(followUpEntity.getFollowUpId());
            followUpDTO.setLoanId(followUpEntity.getLoanId());
            followUpDTO.setCreatedDate(followUpEntity.getCreatedDate());
            followUpDTO.setCreatedBy(followUpEntity.getCreatedBy());
            followUpDTO.setFollowUpReason(followUpEntity.getFollowUpReason());
            followUpDTO.setFollowUpDateTime(followUpEntity.getFollowUpDateTime());
            followUpDTO.setFollowUpOtherReason(followUpEntity.getOtherFollowUpReason());
            followUpDTO.setRemarks(followUpEntity.getRemarks());
            followUpList.add(followUpDTO);
        }
        return followUpList;
    }


    public void createFollowUp(FollowUpDtoRequest followUpDtoRequest){

        FollowUpEntity followUpEntity = new FollowUpEntity();

        followUpEntity.setLoanId(followUpDtoRequest.getLoanId());
        followUpEntity.setCreatedBy(followUpDtoRequest.getCreatedBy());
        followUpEntity.setCreatedDate(new Date());
        followUpEntity.setFollowUpReason(followUpDtoRequest.getFollowUpReason());

        followUpEntity.setFollowUpDateTime(followUpDtoRequest.getFollowUpDateTime());

        followUpEntity.setOtherFollowUpReason(followUpDtoRequest.getOtherFollowupReason());
        followUpEntity.setRemarks(followUpDtoRequest.getRemarks());
        followUpEntity.setIsDeleted(false);

        followUpRepository.save(followUpEntity);

    }




}
