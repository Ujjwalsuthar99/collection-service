package com.synoriq.synofin.collection.collectionservice.rest.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class FollowUpDtoRequest {


    Long loanId;
    Long createdBy;
    String followUpReason;


    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    String nextFollowUpDateTime;

    String otherFollowupReason;
    String remarks;
    CollectionActivityLogDTO activityLog;

}
