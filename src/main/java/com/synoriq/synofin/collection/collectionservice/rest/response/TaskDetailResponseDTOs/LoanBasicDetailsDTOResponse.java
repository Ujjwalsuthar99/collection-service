package com.synoriq.synofin.collection.collectionservice.rest.response.TaskDetailResponseDTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class LoanBasicDetailsDTOResponse {

    @JsonProperty("response")
    public Boolean response;

    @JsonProperty("data")
    public LoanBasicDetailsDTO data;

    @JsonProperty("error")
    public String error;
}
