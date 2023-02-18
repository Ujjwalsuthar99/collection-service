package com.synoriq.synofin.collection.collectionservice.rest.response.dummyTaskDetail;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class DUMMyCUST {


    @JsonProperty("id")
    public String id;


    @JsonProperty("customer_type")
    public String customerType;


}
