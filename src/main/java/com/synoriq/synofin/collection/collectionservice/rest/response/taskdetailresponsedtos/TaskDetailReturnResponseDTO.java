package com.synoriq.synofin.collection.collectionservice.rest.response.taskdetailresponsedtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.synoriq.synofin.collection.collectionservice.rest.response.taskdetailresponsedtos.collateraldetailsresponsedto.CollateralDetailsReturnResponseDTO;
import lombok.Data;

import java.util.List;

@Data
public class TaskDetailReturnResponseDTO {

    @JsonProperty("loanDetails")
    public LoanDetailsResponseDTO loanDetails;

    @JsonProperty("customerDetails")
    public List<CustomerDetailsReturnResponseDTO> customerDetails;

    @JsonProperty("collateralDetails")
    public CollateralDetailsReturnResponseDTO collateralDetails;


}
