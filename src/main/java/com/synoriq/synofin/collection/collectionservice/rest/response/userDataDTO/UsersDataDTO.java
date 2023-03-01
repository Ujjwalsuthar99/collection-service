package com.synoriq.synofin.collection.collectionservice.rest.response.userDataDTO;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class UsersDataDTO {

    @JsonProperty("id")
    public Long id;

    @JsonProperty("createdDate")
    public String createdDate;

    @JsonProperty("modifiedDate")
    public String modifiedDate;

    @JsonProperty("createdBy")
    public Long createdBy;

    @JsonProperty("modifiedBy")
    public Long modifiedBy;

    @JsonProperty("deleted")
    public Boolean deleted;

    @JsonProperty("name")
    public String name;

    @JsonProperty("lastName")
    public String lastName;

    @JsonProperty("employeeCode")
    public String employeeCode;

    @JsonProperty("phoneNumber")
    public String phoneNumber;

    @JsonProperty("mobileNumber")
    public String mobileNumber;

    @JsonProperty("username")
    public String username;

    @JsonProperty("password")
    public String password;

    @JsonProperty("isWaiverUser")
    public Boolean isWaiverUser;

    @JsonProperty("emailId")
    public String emailId;

    @JsonProperty("twoFactorEnabled")
    public Boolean twoFactorEnabled;

    @JsonProperty("authKey")
    public String authKey;

    @JsonProperty("active")
    public Boolean active;


}
