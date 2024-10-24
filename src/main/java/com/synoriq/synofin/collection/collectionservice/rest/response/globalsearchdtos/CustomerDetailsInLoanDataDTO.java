package com.synoriq.synofin.collection.collectionservice.rest.response.globalsearchdtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

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
