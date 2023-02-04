package com.synoriq.synofin.collection.collectionservice.rest.request;

import lombok.Data;

@Data
public class CollectionActivityLogRequest {

    Long userId;
    Boolean deleted;
    String activityName;
    Double distanceFromUserBranch;
    Object address;
    String remarks;
    Object images;
    Long loanId;
    Object geolocationData;
}
