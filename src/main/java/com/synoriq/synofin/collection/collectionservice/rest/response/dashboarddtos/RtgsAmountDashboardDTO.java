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
public class RtgsAmountDashboardDTO {

    @JsonProperty("rtgs_amount")
    private Double rtgsAmount;

    @JsonProperty("rtgs_limit")
    private Double rtgsLimit;

}
