package com.synoriq.synofin.collection.collectionservice.service.implementation;

import com.synoriq.synofin.collection.collectionservice.entity.AdditionalContactDetailsEntity;
import com.synoriq.synofin.collection.collectionservice.repository.AdditionalContactDetailsRepository;
import com.synoriq.synofin.collection.collectionservice.rest.request.AdditionalContactDetailsDtoRequest;
import com.synoriq.synofin.collection.collectionservice.rest.response.BaseDTOResponse;
import com.synoriq.synofin.collection.collectionservice.service.AdditionalContactDetailsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class AdditionalContactDetailsServiceImpl implements AdditionalContactDetailsService {

    @Autowired
    private AdditionalContactDetailsRepository additionalContactDetailsRepository;

    @Override
    public List<AdditionalContactDetailsDtoRequest> getAdditionalContactDetailsByLoanId(Long loanId) throws Exception {
        List<AdditionalContactDetailsEntity> additionalContactDetails;
        List<AdditionalContactDetailsDtoRequest> additionalContactDetailsDtoRequests = new ArrayList<>();
        additionalContactDetails = additionalContactDetailsRepository.findAllByLoanId(loanId);
        if (additionalContactDetails.isEmpty()) {
            return additionalContactDetailsDtoRequests;
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
               .address(additionalContact.getAddress())
               .build()));

        return additionalContactDetailsDTO;
    }

    @Override
    public AdditionalContactDetailsDtoRequest getAdditionalContactDetailsById(Long additionalContactDetailId) throws Exception {
        try {
            AdditionalContactDetailsEntity additionalContactDetailsEntity = additionalContactDetailsRepository.findById(additionalContactDetailId).get();

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
    @Override
    public BaseDTOResponse<Object> createAdditionalContactDetail(AdditionalContactDetailsDtoRequest additionalContactDetailsDtoRequest) throws Exception {
        try {

            AdditionalContactDetailsEntity alreadyExistAdditionalContact = additionalContactDetailsRepository.getDetailByLoanIdByRelationByMobileNumber(additionalContactDetailsDtoRequest.getLoanId(), additionalContactDetailsDtoRequest.getRelationWithApplicant(),
                    additionalContactDetailsDtoRequest.getMobileNumber());
            if (alreadyExistAdditionalContact != null) {
                return new BaseDTOResponse<>("Additional Contact Detail Created Successfully");
            } else {
                AdditionalContactDetailsEntity additionalContactDetailsEntity = new AdditionalContactDetailsEntity();
                additionalContactDetailsEntity.setCreatedDate(new Date());
                additionalContactDetailsEntity.setCreatedBy(additionalContactDetailsDtoRequest.getCreatedBy());
                additionalContactDetailsEntity.setLoanId(additionalContactDetailsDtoRequest.getLoanId());
                additionalContactDetailsEntity.setContactName(additionalContactDetailsDtoRequest.getContactName());
                additionalContactDetailsEntity.setMobileNumber(additionalContactDetailsDtoRequest.getMobileNumber());
                additionalContactDetailsEntity.setAltMobileNumber(additionalContactDetailsDtoRequest.getAltMobileNumber());
                additionalContactDetailsEntity.setEmail(additionalContactDetailsDtoRequest.getEmail());
                additionalContactDetailsEntity.setRelationWithApplicant(additionalContactDetailsDtoRequest.getRelationWithApplicant());
                additionalContactDetailsEntity.setAddress(additionalContactDetailsDtoRequest.getAddress());

                additionalContactDetailsRepository.save(additionalContactDetailsEntity);
            }
            return new BaseDTOResponse<>("Additional Contact Detail Created Successfully");
        } catch (Exception e) {
            throw new Exception("1017001");
        }
    }
}