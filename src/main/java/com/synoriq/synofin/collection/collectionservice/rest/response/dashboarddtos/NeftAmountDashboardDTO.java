package com.synoriq.synofin.collection.collectionservice.rest.response.dashboarddtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class NeftAmountDashboardDTO {

    @JsonProperty("neft_amount")
    private Double neftAmount;

    @JsonProperty("neft_limit")
    private Double neftLimit;

}
