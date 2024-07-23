package com.synoriq.synofin.collection.collectionservice.rest.request.loanAllocationDTOs;

import lombok.Data;

import java.util.List;

@Data
public class LoanAllocationDtoRequest {

    private Long createdBy;
    private Boolean deleted;
    private List<Long> loanId;
    private Long allocatedToUserId;

}
