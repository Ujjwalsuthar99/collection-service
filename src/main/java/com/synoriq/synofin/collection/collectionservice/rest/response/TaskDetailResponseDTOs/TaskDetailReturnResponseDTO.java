package com.synoriq.synofin.collection.collectionservice.rest.response.TaskDetailResponseDTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class TaskDetailReturnResponseDTO {

    @JsonProperty("loanDetails")
    public LoanDataDTO loanDetails;

    @JsonProperty("customerDetails")
    public List<CustomerDetailsReturnResponseDTO> customerDetails;




}
