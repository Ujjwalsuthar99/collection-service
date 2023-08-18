package com.synoriq.synofin.collection.collectionservice.rest.response.TaskDetailResponseDTOs.LoanSummaryForLoanDTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.synoriq.synofin.collection.collectionservice.rest.response.TaskDetailResponseDTOs.CustomerDataResponseDTO;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class LoanSummaryResponseDTO {

    @JsonProperty("response")
    public Boolean response;

    @JsonProperty("data")
    public LoanSummaryDataResponseDTO data;

    @JsonProperty("error")
    public String error;
}
