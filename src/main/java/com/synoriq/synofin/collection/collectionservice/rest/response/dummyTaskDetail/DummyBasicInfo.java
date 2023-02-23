package com.synoriq.synofin.collection.collectionservice.rest.response.dummyTaskDetail;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class DummyBasicInfo {


    @JsonProperty("id")
    public Long id;

    @JsonProperty("first_name")
    public String firstName;
    @JsonProperty("middle_name")
    public String middleName;
    @JsonProperty("last_name")
    public String lastName;
    @JsonProperty("dob")
    public String dob;
    @JsonProperty("numbers")
    public String mobNo;
    @JsonProperty("full_address")
    public String FullAddress;


}