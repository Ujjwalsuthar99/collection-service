package com.synoriq.synofin.collection.collectionservice.rest.response.ReceiptTransferDTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class ReceiptsDataResponseDTO {

    @JsonProperty("receipt_id")
    private Long receiptId;

    @JsonProperty("created_date")
    private String createdDate;

    @JsonProperty("receipt_amount")
    private Double receiptAmount;

    @JsonProperty("loan_application_number")
    private String loanApplicationNumber;

    @JsonProperty("loan_id")
    private Long loanId;

    @JsonProperty("created_by")
    private String createdBy;

    @JsonProperty("status")
    private String status;

    @JsonProperty("receipt_images")
    private Object receiptImages;

    @JsonProperty("geo_location_data")
    private Object geoLocationData;

}
