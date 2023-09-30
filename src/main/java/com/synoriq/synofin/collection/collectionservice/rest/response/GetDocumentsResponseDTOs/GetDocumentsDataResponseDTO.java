package com.synoriq.synofin.collection.collectionservice.rest.response.GetDocumentsResponseDTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetDocumentsDataResponseDTO {

    @JsonProperty("documentType")
    private String documentType;

    @JsonProperty("documentState")
    private Object documentState;

    @JsonProperty("receivingDate")
    private Object receivingDate;

    @JsonProperty("isVerified")
    private Boolean isVerified;

    @JsonProperty("documentUrl")
    private String documentUrl;

    @JsonProperty("documentStatus")
    private Object documentStatus;

    @JsonProperty("description")
    private Object description;

    @JsonProperty("documentValue")
    private String documentValue;

    @JsonProperty("applicantType")
    private String applicantType;

    @JsonProperty("customerName")
    private String customerName;

    @JsonProperty("otherDocumentName")
    private Object otherDocumentName;

    @JsonProperty("customerId")
    private Integer customerId;

    @JsonProperty("documentId")
    private Integer documentId;

    @JsonProperty("documentOwner")
    private String documentOwner;

    @JsonProperty("documentStage")
    private Object documentStage;

    @JsonProperty("documentDepartment")
    private Object documentDepartment;

    @JsonProperty("disbursalStage")
    private Object disbursalStage;

}
