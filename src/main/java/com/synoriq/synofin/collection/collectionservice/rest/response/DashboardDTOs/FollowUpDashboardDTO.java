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
public class FollowUpDashboardDTO {

    @JsonProperty("total_count")
    private Double totalCount;

    @JsonProperty("action_count")
    private Double actionCount;

}
