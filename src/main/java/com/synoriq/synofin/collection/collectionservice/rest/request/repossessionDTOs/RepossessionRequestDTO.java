package com.synoriq.synofin.collection.collectionservice.rest.request.repossessionDTOs;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
public class RepossessionRequestDTO {

    @JsonProperty("initiated_by")
    private Long initiatedBy;

    @JsonIgnore
    @JsonProperty("status")
    private String status;

    @JsonIgnore
    @JsonProperty("repo_id")
    private Long repoId;

    @JsonIgnore
    @JsonProperty("yard_details_json")
    private Object yardDetailsJson;

    @JsonProperty("loan_id")
    private Long loanId;

    @JsonProperty("remarks")
    private String remarks;

    @JsonProperty("battery_percentage")
    private Long batteryPercentage;

    @JsonProperty("geo_location_data")
    private Object geoLocationData;

    @JsonProperty("attachments")
    private Object attachments;
}
