package com.synoriq.synofin.collection.collectionservice.rest.response.DashboardDTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class UpiAmountDashboardDTO {

    @JsonProperty("upi_amount")
    private Double upiAmount;

    @JsonProperty("upi_limit")
    private Double upiLimit;

}
