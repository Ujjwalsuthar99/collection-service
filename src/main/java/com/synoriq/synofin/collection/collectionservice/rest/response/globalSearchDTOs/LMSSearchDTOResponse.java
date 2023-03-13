package com.synoriq.synofin.collection.collectionservice.rest.response.globalSearchDTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@Getter
@Setter
public class LMSSearchDTOResponse {

    @JsonProperty("loan_details")
    List<LMSLoanDataDTO> loanDetails;

    @JsonProperty("customer_details")
    String customerDetails;

    @JsonProperty("communication_details")
    String communicationDetails;

    @JsonProperty("total_loan_count")
    String totalLoanCount;

    @JsonProperty("total_loan_search_count")
    String totalLoanSearchCount;

}

