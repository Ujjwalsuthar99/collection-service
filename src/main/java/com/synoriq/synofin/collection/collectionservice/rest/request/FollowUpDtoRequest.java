package com.synoriq.synofin.collection.collectionservice.rest.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.synoriq.synofin.lms.commondto.dto.collection.CollectionActivityLogDTO;
import lombok.Data;

import java.util.Date;

@Data
public class FollowUpDtoRequest {


    Long loanId;
    Boolean isDeleted;
    Long createdBy;
    String followUpReason;


    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    Date nextFollowUpDateTime;

    String otherFollowupReason;
    String remarks;
    CollectionActivityLogDTO activityLog;

}
