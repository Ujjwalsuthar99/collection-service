package com.synoriq.synofin.collection.collectionservice.rest.request.followUpDTOs;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.synoriq.synofin.collection.collectionservice.rest.request.CollectionActivityLogDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

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

}
