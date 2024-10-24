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
public class ChequeAmountDashboardDTO {

    @JsonProperty("cheque_amount")
    private Double chequeAmount;

    @JsonProperty("cheque_limit")
    private Double chequeLimit;

}
