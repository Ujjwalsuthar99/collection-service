package com.synoriq.synofin.collection.collectionservice.rest.response.profiledetailsdtos;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class ProfileDataDTO {


    @JsonProperty("name")
    public String name;

    @JsonProperty("roles")
    public List<Object> roles;

    @JsonProperty("permissions")
    public List<Object> permissions;

    @JsonProperty("user_name")
    public String userName;

    @JsonProperty("user_id")
    public Long userId;

    @JsonProperty("email")
    public String email;

    @JsonProperty("mobile_number")
    public String mobileNumber;

    @JsonProperty("branch")
    public String branch;

    @JsonProperty("department")
    public String department;

    @JsonProperty("designation")
    public String designation;

}
