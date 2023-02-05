package com.synoriq.synofin.collection.collectionservice.service;

import com.synoriq.synofin.collection.collectionservice.entity.AdditionalContactDetailsEntity;
import com.synoriq.synofin.collection.collectionservice.repository.AdditionalContactDetailsRepository;
import com.synoriq.synofin.collection.collectionservice.rest.request.AdditionalContactDetailsDtoRequest;
import com.synoriq.synofin.collection.collectionservice.rest.response.BaseDTOResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class AdditionalContactDetailsService {

    @Autowired
    private AdditionalContactDetailsRepository additionalContactDetailsRepository;

//    public ResponseEntity<List<AdditionalContactDetailsEntity>> getAdditionalContactDetailsByLoanId (Long loanId) {
//
//        return new ResponseEntity<>(additionalContactDetailsRepository.findAllByLoanId(loanId), HttpStatus.OK);
//    }

    public List<AdditionalContactDetailsDtoRequest> getAdditionalContactDetailsByLoanId(Long loanId) throws Exception {
        List<AdditionalContactDetailsEntity> additionalContactDetails = additionalContactDetailsRepository.findAllByLoanId(loanId);
        if (additionalContactDetails.isEmpty()) {
            throw new Exception("1017002");
        }
        List<AdditionalContactDetailsDtoRequest> additionalContactDetailsDTO = new ArrayList<>();

        additionalContactDetails.forEach(additionalContact -> additionalContactDetailsDTO.add(AdditionalContactDetailsDtoRequest.builder()
               .contactName(additionalContact.getContactName())
               .email(additionalContact.getEmail())
               .createdBy(additionalContact.getCreatedBy())
               .createdDate(additionalContact.getCreatedDate())
               .altMobileNumber(additionalContact.getAltMobileNumber())
               .loanId(additionalContact.getLoanId())
               .mobileNumber(additionalContact.getMobileNumber())
               .deleted(additionalContact.getDeleted())
               .relationWithApplicant(additionalContact.getRelationWithApplicant())
               .build()));
        log.info("additional Contact Data {}", additionalContactDetailsDTO);
        log.info("additional Contact Data isEmpty {}", additionalContactDetailsDTO.isEmpty());

        return additionalContactDetailsDTO;
    }

    public AdditionalContactDetailsDtoRequest getAdditionalContactDetailsById(Long additionalContactDetailId) throws Exception {
        try {
            AdditionalContactDetailsEntity additionalContactDetailsEntity = additionalContactDetailsRepository.findById(additionalContactDetailId).get();
            log.info("additionalContactDetailsEntity Data {}", additionalContactDetailsEntity);

            AdditionalContactDetailsDtoRequest additionalContactDetailsDtoRequest = new AdditionalContactDetailsDtoRequest();
            additionalContactDetailsDtoRequest.setCreatedDate(additionalContactDetailsEntity.getCreatedDate());
            additionalContactDetailsDtoRequest.setCreatedBy(additionalContactDetailsEntity.getCreatedBy());
            additionalContactDetailsDtoRequest.setLoanId(additionalContactDetailsEntity.getLoanId());
            additionalContactDetailsDtoRequest.setDeleted(additionalContactDetailsEntity.getDeleted());
            additionalContactDetailsDtoRequest.setContactName(additionalContactDetailsEntity.getContactName());
            additionalContactDetailsDtoRequest.setMobileNumber(additionalContactDetailsEntity.getMobileNumber());
            additionalContactDetailsDtoRequest.setAltMobileNumber(additionalContactDetailsEntity.getAltMobileNumber());
            additionalContactDetailsDtoRequest.setEmail(additionalContactDetailsEntity.getEmail());
            additionalContactDetailsDtoRequest.setRelationWithApplicant(additionalContactDetailsEntity.getRelationWithApplicant());
            return additionalContactDetailsDtoRequest;
        } catch(Exception e) {
            throw new Exception("1017002");
        }

    }

    public BaseDTOResponse<Object> createAdditionalContactDetail(AdditionalContactDetailsDtoRequest additionalContactDetailsDtoRequest) throws Exception {
        try {
            AdditionalContactDetailsEntity additionalContactDetailsEntity = new AdditionalContactDetailsEntity();

            additionalContactDetailsEntity.setCreatedDate(new Date());
            additionalContactDetailsEntity.setCreatedBy(additionalContactDetailsDtoRequest.getCreatedBy());
            additionalContactDetailsEntity.setLoanId(additionalContactDetailsDtoRequest.getLoanId());
            additionalContactDetailsEntity.setContactName(additionalContactDetailsDtoRequest.getContactName());
            additionalContactDetailsEntity.setMobileNumber(additionalContactDetailsDtoRequest.getMobileNumber());
            additionalContactDetailsEntity.setAltMobileNumber(additionalContactDetailsDtoRequest.getAltMobileNumber());
            additionalContactDetailsEntity.setEmail(additionalContactDetailsDtoRequest.getEmail());
            additionalContactDetailsEntity.setRelationWithApplicant(additionalContactDetailsDtoRequest.getRelationWithApplicant());

            additionalContactDetailsRepository.save(additionalContactDetailsEntity);
            log.info("Additional Contact Detail Data Saved Successfully");
            return new BaseDTOResponse<>("Additional Contact Detail Created Successfully");
        } catch (Exception e) {
            throw new Exception("1017001");
        }
    }
}