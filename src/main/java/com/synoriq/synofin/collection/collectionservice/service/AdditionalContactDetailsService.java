package com.synoriq.synofin.collection.collectionservice.service;

import com.synoriq.synofin.collection.collectionservice.entity.AdditionalContactDetailsEntity;
import com.synoriq.synofin.collection.collectionservice.repository.AdditionalContactDetailsRepository;
import com.synoriq.synofin.collection.collectionservice.rest.request.AdditionalContactDetailsDtoRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

    public ResponseEntity<List<AdditionalContactDetailsDtoRequest>> getAdditionalContactDetailsByLoanId(Long loanId) {

        List<AdditionalContactDetailsEntity> additionalContactDetails = additionalContactDetailsRepository.findAllByLoanId(loanId);

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

        return new ResponseEntity<>(additionalContactDetailsDTO, HttpStatus.OK);
    }
}