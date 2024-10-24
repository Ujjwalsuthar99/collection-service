package com.synoriq.synofin.collection.collectionservice.rest.response.userdatadtos;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UsersDataDTO {

    @JsonProperty("id")
    public Long id;

    @JsonProperty("name")
    public String name;

    @JsonProperty("employeeCode")
    public String employeeCode;

    @JsonProperty("username")
    public String username;

    @JsonProperty("active")
    public Boolean active;

    @JsonProperty("transferTo")
    public String transferTo;

}
