package com.synoriq.synofin.collection.collectionservice.rest.response.dummyTaskDetail;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class DummyResponse {

    @JsonProperty("loanDetails")
    public DummyLoanDetails loanDetails;

    @JsonProperty("customerDetails")
    public List<DUMMyCUST> customerDetails;




}
