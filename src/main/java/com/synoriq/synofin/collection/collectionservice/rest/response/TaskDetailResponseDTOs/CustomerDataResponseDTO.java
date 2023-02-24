package com.synoriq.synofin.collection.collectionservice.rest.response.TaskDetailResponseDTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@Getter
@Setter
public class CustomerDataResponseDTO {

    @JsonProperty("id")
    Long id;

    @JsonProperty("customer_type")
    String customerType;

    @JsonProperty("basic_info")
    BasicInfoResponseDTO basicInfo;

    @JsonProperty("communication")
    List<CommunicationResponseDTO> communication;

    @JsonProperty("business_info")
    Object businessInfo;

    @JsonProperty("insurance_data")
    List<Object> insuranceData;

    @JsonProperty("family_information")
    List<Object> familyInformation;

    @JsonProperty("in_law_details")
    List<Object> inLawDetails;

    @JsonProperty("references")
    List<Object> references;

    @JsonProperty("obligation")
    List<Object> obligation;

    @JsonProperty("bank_details")
    List<Object> bankDetails;

    @JsonProperty("basic_info_non_individual")
    Object basicInfoNonIndividual;

    @JsonProperty("nominees_details")
    Object nomineesDetails;

    @JsonProperty("relationship_with_applicant")
    Object relationshipWithApplicant;

    @JsonProperty("relationship_with_co_applicant")
    Object relationshipWithCoApplicant;

    @JsonProperty("institution_info")
    Object institutionInfo;

}

