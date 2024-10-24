package com.synoriq.synofin.collection.collectionservice.rest.response.userdetailsbyuseriddtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class UserDetailDataByUserIdDTO {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("email")
    private String email;

    @JsonProperty("mobileNumber")
    private String mobileNumber;

    @JsonProperty("joiningDate")
    private String joiningDate;

    @JsonProperty("employeeName")
    private String employeeName;

    @JsonProperty("employeeUserName")
    private String employeeUserName;

    @JsonProperty("password")
    private String password;

    @JsonProperty("employeeCode")
    private String employeeCode;

    @JsonProperty("active")
    private Boolean active;

    @JsonProperty("stage")
    private String stage;

    @JsonProperty("createdBy")
    private String createdBy;

    @JsonProperty("createdDate")
    private String createdDate;

    @JsonProperty("modifiedBy")
    private Long modifiedBy;

    @JsonProperty("modifiedDate")
    private String modifiedDate;

    @JsonProperty("deleted")
    private String deleted;

    @JsonProperty("designation")
    private String designation;

    @JsonProperty("department")
    private String department;

    @JsonProperty("reportingManager")
    private String reportingManager;

    @JsonProperty("lastLogin")
    private String lastLogin;

    @JsonProperty("lastLogout")
    private String lastLogout;

    @JsonProperty("roles")
    private List<Object> roles;

    @JsonProperty("activeStatus")
    private String activeStatus;

    @JsonProperty("roleEntity")
    private String roleEntity;

    @JsonProperty("isBulkUpload")
    private Boolean isBulkUpload;

    @JsonProperty("code")
    private String code;

    @JsonProperty("waiverUser")
    private Boolean waiverUser;

}
