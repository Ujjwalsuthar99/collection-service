package com.synoriq.synofin.collection.collectionservice.rest.request.repossessionDTOs;


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
