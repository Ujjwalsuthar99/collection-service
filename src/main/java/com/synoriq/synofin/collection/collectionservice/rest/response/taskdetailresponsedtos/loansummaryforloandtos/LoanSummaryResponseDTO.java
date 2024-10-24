package com.synoriq.synofin.collection.collectionservice.rest.response.taskdetailresponsedtos.loansummaryforloandtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

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
