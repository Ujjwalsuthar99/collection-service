package com.synoriq.synofin.collection.collectionservice.rest.response.UserDataDTOs;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class FilteredUsersDataDTO extends UsersDataDTO {

    @JsonProperty("id")
    public Long id;

    @JsonProperty("name")
    public String name;

    @JsonProperty("employeeCode")
    public String employeeCode;

    @JsonProperty("username")
    public String username;

    @JsonProperty("transferTo")
    public String transferTo;

}
