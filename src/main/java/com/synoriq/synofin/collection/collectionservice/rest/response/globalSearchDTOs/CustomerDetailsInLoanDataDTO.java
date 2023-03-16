package com.synoriq.synofin.collection.collectionservice.rest.response.globalSearchDTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.synoriq.synofin.collection.collectionservice.rest.response.TaskDetailResponseDTOs.CustomerDataResponseDTO;
import lombok.Data;

import java.util.List;

@Data
public class CustomerDetailsInLoanDataDTO {

    @JsonProperty("name")
    String name;

    @JsonProperty("phone_number")
    String phoneNumber;

    @JsonProperty("pan")
    String pan;

    @JsonProperty("global_customer_id")
    String globalCustomerId;

    @JsonProperty("customer_address")
    String customerAddress;

}
