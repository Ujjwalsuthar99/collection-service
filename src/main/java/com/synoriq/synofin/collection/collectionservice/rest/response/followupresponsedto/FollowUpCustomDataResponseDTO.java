package com.synoriq.synofin.collection.collectionservice.rest.response.followupresponsedto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class FollowUpCustomDataResponseDTO {

    @JsonProperty("followUp_id")
    private Long followUpId;

    @JsonProperty("created_date")
    private String createdDate;

    @JsonProperty("created_by")
    private String createdBy;

    @JsonProperty("loan_number")
    private String loanNumber;

    @JsonProperty("loan_id")
    private Long loanId;

    @JsonProperty("followup_reason")
    private String followUpReason;

    @JsonProperty("remarks")
    private String remarks;

    @JsonProperty("next_followup_date")
    private String nextFollowupDate;

    @JsonProperty("other_followup_reason")
    private String otherFollowupReason;

    @JsonProperty("followup_images")
    private Object followUpImages;

    @JsonProperty("geo_location_data")
    private Object geoLocationData;

}
