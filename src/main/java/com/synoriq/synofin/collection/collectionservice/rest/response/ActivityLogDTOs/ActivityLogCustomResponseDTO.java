package com.synoriq.synofin.collection.collectionservice.rest.response.ActivityLogDTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Setter
@Getter
@NoArgsConstructor
public class ActivityLogCustomResponseDTO {

    @JsonProperty("collection_activity_logs_id")
    Long collectionActivityLogsId;

    @JsonProperty("activity_date")
    String activityDate;

    @JsonProperty("activity_by")
    Long activityBy;

    @JsonProperty("activity_name")
    String activityName;

    @JsonProperty("distance_from_user_branch")
    Double distanceFromUserBranch;

    @JsonProperty("address")
    Object address;

    @JsonProperty("images")
    Object images;

    @JsonProperty("remarks")
    String remarks;

    @JsonProperty("loan_id")
    Long loanId;

    @JsonProperty("geo_location")
    Object geolocation;

    @JsonProperty("is_receipt")
    Boolean isReceipt;

    @JsonProperty("receipt_id")
    Long receiptId;

}
