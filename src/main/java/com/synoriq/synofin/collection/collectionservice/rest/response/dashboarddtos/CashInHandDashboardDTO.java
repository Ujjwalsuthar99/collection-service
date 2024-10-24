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
public class CashInHandDashboardDTO {

    @JsonProperty("cash_in_hand")
    private Double cashInHand;

    @JsonProperty("cash_in_hand_limit")
    private Double cashInHandLimit;

}
