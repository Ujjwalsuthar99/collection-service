package com.synoriq.synofin.collection.collectionservice.rest.response.TaskDetailResponseDTOs.CollateralDetailsResponseDTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.synoriq.synofin.collection.collectionservice.rest.response.TaskDetailResponseDTOs.LoanSummaryForLoanDTOs.LoanSummaryDataResponseDTO;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
public class CollateralDetailsResponseDTO {

    @JsonProperty("response")
    public Boolean response;

    @JsonProperty("data")
    public Map<String, Map<String, Object>> data;

    @JsonProperty("error")
    public String error;
}
