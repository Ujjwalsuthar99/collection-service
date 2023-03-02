package com.synoriq.synofin.collection.collectionservice.rest.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Setter
@NoArgsConstructor
public class ActivityLogResponse {

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

}
