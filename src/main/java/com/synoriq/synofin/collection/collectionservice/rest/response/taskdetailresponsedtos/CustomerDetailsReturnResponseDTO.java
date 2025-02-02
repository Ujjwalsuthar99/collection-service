package com.synoriq.synofin.collection.collectionservice.rest.response.taskdetailresponsedtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CustomerDetailsReturnResponseDTO {


    @JsonProperty("id")
    public Long id;

    @JsonProperty("customer_type")
    public String customerType;

    @JsonProperty("basic_info")
    public BasicInfoReturnResponseDTO basicInfo;

    @JsonProperty("address")
    public Object address;

    @JsonProperty("numbers")
    public NumbersReturnResponseDTO numbers;

}
