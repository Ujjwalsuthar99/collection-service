package com.synoriq.synofin.collection.collectionservice.rest.response.dummyTaskDetail;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class DUMMyCUST {


    @JsonProperty("id")
    public Long id;


    @JsonProperty("customer_type")
    public String customerType;

    @JsonProperty("basic_info")
    public DummyBasicInfo basicInfo;


}
