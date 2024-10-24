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
public class CommonCountDashboardDTO {

    @JsonProperty("total_amount")
    private Double totalAmount;

    @JsonProperty("total_count")
    private Double totalCount;

}
