package com.synoriq.synofin.collection.collectionservice.rest.response.TaskDetailResponseDTOs;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.persistence.criteria.CriteriaBuilder;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BasicInfoReturnResponseDTO {


    @JsonProperty("id")
    public Long id;

    @JsonProperty("first_name")
    public String firstName;

    @JsonProperty("middle_name")
    public String middleName;

    @JsonProperty("last_name")
    public String lastName;

    @JsonProperty("relation")
    public String relation;

    @JsonProperty("dob")
    public String dob;

    @JsonProperty("address")
    public String address;

}
