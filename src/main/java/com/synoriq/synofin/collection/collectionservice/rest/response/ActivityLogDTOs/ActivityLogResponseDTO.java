package com.synoriq.synofin.collection.collectionservice.rest.response.ActivityLogDTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
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

    public ActivityLogResponseDTO(Long collectionActivityLogsId, Date activityDate, Long userId,
                                  Long loanId, String remarks, String activityName, Object address,
                                  Object images, Object geolocation, Double distanceFromUserBranch,
                                  Long batteryPercentage) {
        this.collectionActivityLogsId = collectionActivityLogsId;
        this.activityDate = activityDate;
        this.userId = userId;
        this.loanId = loanId;
        this.remarks = remarks;
        this.activityName = activityName;
        this.address = address;
        this.images = images;
        this.geolocation = geolocation;
        this.distanceFromUserBranch = distanceFromUserBranch;
        this.batteryPercentage = batteryPercentage;
    }

    @JsonProperty("battery_percentage")
    private Long batteryPercentage;

}
