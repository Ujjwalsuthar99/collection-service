package com.synoriq.synofin.collection.collectionservice.rest.response.ActivityLogDTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ActivityLogResponseDTO {

    @JsonProperty("activity_log_id")
    Long collectionActivityLogsId;

    @JsonProperty("activity_date")
    Date activityDate;

    @JsonProperty("user_id")
    Long userId;

    @JsonProperty("loan_id")
    Long loanId;

    @JsonProperty("remarks")
    String remarks;

    @JsonProperty("activity_name")
    String activityName;

    @JsonProperty("address")
    Object address;

    @JsonProperty("images")
    Object images;

    @JsonProperty("geo_location")
    Object geolocation;

    @JsonProperty("distance_from_user_branch")
    Double distanceFromUserBranch;

    @JsonProperty("battery_percentage")
    private Long batteryPercentage;

}
