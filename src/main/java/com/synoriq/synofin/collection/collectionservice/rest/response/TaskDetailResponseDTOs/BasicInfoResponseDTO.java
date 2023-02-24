package com.synoriq.synofin.collection.collectionservice.rest.response.TaskDetailResponseDTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class BasicInfoResponseDTO {


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

    @JsonProperty("gender")
    public String gender;

    @JsonProperty("profile_picture_url")
    public String profilePictureUrl;

}