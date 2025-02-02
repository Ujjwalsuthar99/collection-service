package com.synoriq.synofin.collection.collectionservice.rest.request.followupdtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.synoriq.synofin.collection.collectionservice.rest.request.CollectionActivityLogDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FollowUpDtoRequest {


    Long loanId;
    Long createdBy;
    String followUpReason;
    String status;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    String nextFollowUpDateTime;

    String otherFollowupReason;
    String remarks;
    CollectionActivityLogDTO activityLog;

    @JsonProperty("isReschedule")
    private Boolean isReschedule;

    @JsonProperty("toBeRescheduledId")
    private Long toBeRescheduledId;

}
