package com.synoriq.synofin.collection.collectionservice.rest.response.TaskDetailResponseDTOs;

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


}
