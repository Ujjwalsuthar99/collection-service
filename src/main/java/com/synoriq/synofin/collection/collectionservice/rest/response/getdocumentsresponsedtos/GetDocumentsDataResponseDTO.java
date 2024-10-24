package com.synoriq.synofin.collection.collectionservice.rest.response.getdocumentsresponsedtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetDocumentsDataResponseDTO {

    @JsonProperty("document_type")
    private String documentType;

    @JsonProperty("document_state")
    private Object documentState;

    @JsonProperty("receiving_date")
    private Object receivingDate;

    @JsonProperty("is_verified")
    private Boolean isVerified;

    @JsonProperty("document_url")
    private String documentUrl;

    @JsonProperty("document_status")
    private Object documentStatus;

    @JsonProperty("description")
    private Object description;

    @JsonProperty("document_value")
    private String documentValue;

    @JsonProperty("applicant_type")
    private String applicantType;

    @JsonProperty("customer_name")
    private String customerName;

    @JsonProperty("other_document_name")
    private Object otherDocumentName;

    @JsonProperty("customer_id")
    private Integer customerId;

    @JsonProperty("document_id")
    private Integer documentId;

    @JsonProperty("document_owner")
    private String documentOwner;

    @JsonProperty("document_stage")
    private Object documentStage;

    @JsonProperty("document_department")
    private Object documentDepartment;

    @JsonProperty("disbursal_stage")
    private Object disbursalStage;

}
